package searchengine.dto.index;
import org.jsoup.HttpStatusException;
import searchengine.models.Index;
import searchengine.models.Lemma;
import searchengine.models.Page;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.RecursiveAction;

import static java.lang.Thread.sleep;

public class LinkRecursiveTask extends RecursiveAction {

    private final Link url;
    private final Link rootUrl;
    private final static CopyOnWriteArraySet<String> allLinks = new CopyOnWriteArraySet<>();
    private final HashMap<String, Lemma> allLemmas;
    private final LemmaFinder lemmaFinder = LemmaFinder.getInstance();
    private Map<String, Integer> bodyMap = new HashMap<>();
    private Map<String, Integer> titleMap = new HashMap<>();
    private Page page;
    private final IndexRepository indexRepository;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;


    public LinkRecursiveTask(Link url, Link rootUrl, HashMap<String, Lemma> lemmas, IndexRepository indexRepository, SiteRepository siteRepository, PageRepository pageRepository) throws IOException{
        this.url = url;
        this.siteRepository = siteRepository;
        this.indexRepository = indexRepository;
        this.pageRepository = pageRepository;
        this.rootUrl = rootUrl;
        this.allLemmas = lemmas;
    }
    @Override
    protected void compute() {
        Set<LinkRecursiveTask> taskList = new HashSet<>();
        try {
            sleep(5000);
            Connection connection = Jsoup.connect(url.getUrl())
                    .timeout(100000)
                    .header("Accept", "text/javascript")
                    .userAgent("OurSearchBot")
                    .referrer("http://www.yandex.ru");
            Document doc = connection
                    .get();
            int statusCode = connection.response().statusCode();
                page = pageParse(doc, statusCode, url.getUrl());
                bodyMap = getLemmaMap(doc,"body");
                titleMap = getLemmaMap(doc, "title");
                synchronized (indexRepository) {
                    indexRepository.saveAll(getIndices(createLemmas()));
                }
                bodyMap.clear();
                titleMap.clear();
                rootUrl.getSite().setStatusTime(new Date());
                siteRepository.save(rootUrl.getSite());

            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String absUrl = link.attr("abs:href");
                if (isCorrected(absUrl)) {
                    url.addChildren(new Link(absUrl, rootUrl.getSite()));
                    allLinks.add(absUrl);
                }
            }
            for (Link link : url.getChildren()) {
                LinkRecursiveTask task = new LinkRecursiveTask(link, rootUrl, allLemmas, indexRepository, siteRepository, pageRepository);
                task.fork();
                taskList.add(task);
            }
            for (LinkRecursiveTask task : taskList) {
                task.join();
            }
        }
        catch(IOException | InterruptedException e){
            assert e instanceof HttpStatusException;
            HttpStatusException exception = (HttpStatusException) e;
                String path = getPath(exception.getUrl());
                int code = exception.getStatusCode();
                e.printStackTrace();
                Page error = new Page(path, code, "");
            }
        }

    private boolean isCorrected(String url) {
        return (!url.isEmpty() && url.startsWith(rootUrl.getUrl())
                && !url.equals(rootUrl.getUrl()) && !url.equals(rootUrl.getUrl() + "/")
                && !allLinks.contains(url) && !url.contains("#")
                && !url.matches("([^\\s]+(\\.(?i)(jpg|png|gif|bmp|pdf))$)"));
    }

    private Page pageParse(Document document, int code, String url) throws IOException {
        String path = getPath(url);
        String html = document.html();
        String content = MySQLUtils.mysql_real_escape_string(html);
        return new Page(path, code, content);
    }

    private String getPath(String url){
        if (url.equals(rootUrl.getUrl()) && rootUrl.getUrl().endsWith("/")){
            String root = rootUrl.getUrl().substring(0, rootUrl.getUrl().length()-1);
            return url.replace(root, "");
        }
        else {
            url = url.replace(rootUrl.getUrl(), "/");
            return url;
        }
    }


    private Map<String, Integer> getLemmaMap(Document document, String element){
        Elements elements = document.select(element);
        return lemmaFinder.collectLemmas(Jsoup.parse(String.valueOf(elements)).html());
    }

    private  List<Index> getIndices(List<Lemma> lemmas){
        List<Index> indices = new ArrayList<>();
        bodyMap.forEach((s, integer) -> {
            Optional<Lemma> lemmaOptional = lemmas.stream().filter(lem -> lem.getLemma().equals(s)).findFirst();
            if (lemmaOptional.isPresent()){
                Lemma lemma = lemmaOptional.get();
                float rank;
                rank = (float) 0.8 * integer;
                rank = titleMap.containsKey(s) ? rank + titleMap.get(s) : rank;
                indices.add(new Index(page, lemma, rank));
            }
        });
        return indices;
    }

    private synchronized List<Lemma> createLemmas(){
        List<Lemma> lemmaList = new ArrayList<>();
        bodyMap.putAll(titleMap);
        bodyMap.forEach((s, integer) -> {
            Lemma lemma = new Lemma();
            lemma.setLemma(s);
            lemma.setFrequency(1);
            lemmaList.add(lemma);
        });
        return lemmaList;
    }
}
