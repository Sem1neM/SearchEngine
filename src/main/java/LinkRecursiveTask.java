import models.Index;
import models.Lemma;
import models.Page;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.sleep;

public class LinkRecursiveTask extends RecursiveAction {

    private final Link url;
    private final Link rootUrl;
    private final static CopyOnWriteArraySet<String> allLinks = new CopyOnWriteArraySet<>();
    private final Map<String, Integer> titleMap = new HashMap<>();
    private final Map<String, Integer> bodyMap = new HashMap<>();
    private final Map<String, Integer> finalMap = new HashMap<>();
    private final List<Page> pages = new ArrayList<>();
    private final List<Lemma> lemmas = new ArrayList<>();
    private final List<Index> indices = new ArrayList<>();



    public LinkRecursiveTask(Link url, Link rootUrl) {
        this.url = url;
        this.rootUrl = rootUrl;
    }
    @Override
    protected void compute() {
        Set<LinkRecursiveTask> taskList = new HashSet<>();
        try {
            sleep(500);
            linkParse(url);
            Connection connection = Jsoup.connect(url.getUrl())
                    .timeout(1000000);
            Document doc = connection
                    .userAgent("OurSearchBot")
                    .referrer("http://www.google.com")
                    .get();
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String absUrl = link.attr("abs:href");
                if (isCorrected(absUrl)) {
                    url.addChildren(new Link(absUrl));
                    allLinks.add(absUrl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Link link : url.getChildren()) {
            LinkRecursiveTask task = new LinkRecursiveTask(link, rootUrl);
            try{
                linkParse(link);

                link.setBodyMap(fieldParse(link, "body"));
                link.setTitleMap(fieldParse(link, "title"));

                Page page = getPage(link);
                link.setPage(page);
                pages.add(page);

                lemmasWrite(link);

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            task.fork();
            taskList.add(task);

        }
        for (LinkRecursiveTask task : taskList) {
            task.join();
        }
    }

    private boolean isCorrected(String url) {
        return (!url.isEmpty() && url.startsWith(rootUrl.getUrl())
                && !allLinks.contains(url) && !url.contains("#")
                && !url.matches("([^\\s]+(\\.(?i)(jpg|png|gif|bmp|pdf))$)"));
    }

    private void linkParse(Link link) throws Exception{
        link.setHtmlFile(Jsoup.connect(link.getUrl()).get().html());
        Connection.Response inboxJson = Jsoup.connect(link.getUrl())
                .timeout(1000000)
                .header("Accept", "text/javascript")
                .userAgent("OurSearchBot")
                .execute();
        int statusCode = inboxJson.statusCode();
        link.setCode(statusCode);
        Jsoup.parse(link.getUrl()).getAllElements();
    }

    private Map<String, Integer> fieldParse(Link link, String element) throws Exception {
        LemmaFinder lemmaFinder = LemmaFinder.getInstance();
        Connection connection = Jsoup.connect(link.getUrl());
        Document document = connection.get();
        Elements elements = document.select(element);
        return lemmaFinder.collectLemmas(Jsoup.parse(String.valueOf(elements)).text());
    }

    private Page getPage(Link link){
        String root = rootUrl.getUrl().substring(0, rootUrl.getUrl().length()-1);
        String path = link.getUrl().replace(root, "");
        String html = link.getHtmlFile();
        String content = MySQLUtils.mysql_real_escape_string(html);
        return new Page(path, link.getCode(), content);
    }

    private void lemmasWrite(Link link){
        link.getTitleMap().forEach((s, integer) -> {
            if (titleMap.containsKey(s)) {
                titleMap.replace(s, titleMap.get(s) + 1);
            } else {
                titleMap.put(s, 1);
            }
        });
        link.getBodyMap().forEach((s, integer) -> {
            if (bodyMap.containsKey(s)) {
                bodyMap.replace(s, bodyMap.get(s) + 1);
            } else {
                bodyMap.put(s, 1);
            }
        });
        finalMap.putAll(titleMap);
            bodyMap.forEach((s, integer) -> {
                if (finalMap.containsKey(s)){
                    finalMap.replace(s, finalMap.get(s) + 1);
                }
                else {
                    finalMap.put(s,1);
                }
        });
    }
    private void addIndices(Link rootLink){
        for (Link child : rootLink.getChildren()){
            Page pageId = child.getPage();
            child.getBodyMap().forEach((s, integer) -> {
                AtomicReference<Float> rank = new AtomicReference<>((float) 0);
                Optional<Lemma> bodyOpt = lemmas.stream().filter(l -> l.getLemma().equals(s)).findFirst();
                if (bodyOpt.isPresent()){
                Lemma bodyLemma = bodyOpt.get();
                rank.set((float) (integer * 0.8));
                if(child.getTitleMap().containsKey(s)){
                    child.getTitleMap().forEach((s1, integer1) -> {
                        if (s1.equals(s)){
                            rank.updateAndGet(v -> new Float((float) (v + integer1)));
                        }
                    });
                    }
                    indices.add(new Index(pageId, bodyLemma, rank.get()));
                }
            });
        }
    }

    public void addLemmaIndex(){
        finalMap.forEach((s, integer) -> {

            Lemma lemma = new Lemma(s, integer);
            lemmas.add(lemma);

        });
         addIndices(rootUrl);
    }

    public Map<String, Integer> getTitleMap() {
        return titleMap;
    }

    public Map<String, Integer> getBodyMap() {
        return bodyMap;
    }

    public Map<String, Integer> getFinalMap() {
        return finalMap;
    }

    public List<Page> getPages() {
        return pages;
    }

    public List<Lemma> getLemmas() {
        return lemmas;
    }

    public List<Index> getIndices() {
        return indices;
    }
}
