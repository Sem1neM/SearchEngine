package searchengine.config;
import org.hibernate.Session;
import org.hibernate.Transaction;
import searchengine.models.Index;
import searchengine.models.Lemma;
import searchengine.models.Page;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.RecursiveAction;

import static java.lang.Thread.sleep;

public class LinkRecursiveTask extends RecursiveAction {

    private final Link url;
    private final Link rootUrl;
    private final static CopyOnWriteArraySet<String> allLinks = new CopyOnWriteArraySet<>();
    private final LemmaFinder lemmaFinder = LemmaFinder.getInstance();
    private final Map<String, Lemma> allLemmas = new HashMap<>();
    private List<Lemma> lemmas = new ArrayList<>();
    private Map<String, Integer> bodyMap = new HashMap<>();
    private Map<String, Integer> titleMap = new HashMap<>();


    public LinkRecursiveTask(Link url, Link rootUrl) throws IOException{
        this.url = url;
        this.rootUrl = rootUrl;
    }
    @Override
    protected void compute() {
        Set<LinkRecursiveTask> taskList = new HashSet<>();
        try {
            sleep(500);
            Connection connection = Jsoup.connect(url.getUrl())
                    .timeout(1000000)
                    .header("Accept", "text/javascript")
                    .userAgent("OurSearchBot");
            Document doc = connection
                    .userAgent("OurSearchBot")
                    .referrer("http://www.google.com")
                    .get();
            Session session = HibernateSessionFactoryUtils.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            int statusCode = connection.response().statusCode();
            Page page = pageParse(doc, statusCode, url.getUrl());
//            DBModel.savePage(page);
            titleMap = getLemmaMap(doc, "title");
            bodyMap = getLemmaMap(doc, "body");
            lemmas = createLemmas(titleMap, bodyMap);
            getIndices(page,bodyMap,titleMap,lemmas).forEach(session::persist);
            transaction.commit();

//            DBModel.saveLemmas(lemmas);
//            DBModel.saveIndices(getIndices(page, bodyMap, titleMap, lemmas));

            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String absUrl = link.attr("abs:href");
                if (isCorrected(absUrl)) {
                    url.addChildren(new Link(absUrl));
                    allLinks.add(absUrl);
                }
            }
            for (Link link : url.getChildren()) {
                LinkRecursiveTask task = new LinkRecursiveTask(link, rootUrl);
                task.fork();
                taskList.add(task);
            }
            for (LinkRecursiveTask task : taskList) {
                task.join();
            }
        }
        catch(IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

    private boolean isCorrected(String url) {
        return (!url.isEmpty() && url.startsWith(rootUrl.getUrl())
                && !allLinks.contains(url) && !url.contains("#")
                && !url.matches("([^\\s]+(\\.(?i)(jpg|png|gif|bmp|pdf))$)"));
    }

    private Page pageParse(Document document, int code, String url) throws IOException {
        String root = rootUrl.getUrl().substring(0, rootUrl.getUrl().length()-1);
        String path = url.replace(root, "");
        String html = document.html();
        String content = MySQLUtils.mysql_real_escape_string(html);
        return new Page(path, code, content);
    }

    private Map<String, Integer> getLemmaMap(Document document, String element){
        Elements elements = document.select(element);
        return lemmaFinder.collectLemmas(Jsoup.parse(String.valueOf(elements)).text());
    }

    private List<Index> getIndices(Page page, Map<String, Integer> bodyMap, Map<String, Integer> titleMap, List<Lemma> lemmas){
        List<Index> indices = new ArrayList<>();
        bodyMap.forEach((s, integer) -> {
            Lemma lemma = lemmas.stream().filter(lem -> lem.getLemma().equals(s)).findFirst().get();
            float rank;
            rank = (float) 0.8 * integer;
            rank = titleMap.containsValue(s)? rank + titleMap.get(s): rank;
            indices.add(new Index(page, lemma, rank));
        });
        return indices;
    }

    private synchronized List<Lemma> createLemmas(Map<String,Integer> titleMap, Map<String,Integer> bodyMap){
        List<Lemma> lemmaList = new ArrayList<>();
        bodyMap.putAll(titleMap);
        bodyMap.forEach((s, integer) -> {
            Lemma lemma;
            if (allLemmas.containsKey(s)){
                lemma = allLemmas.get(s);
                lemma.setFrequency(+1);
            }
            else {
                lemma = new Lemma(s, 1);
                allLemmas.put(s , lemma);
            }
            lemmaList.add(lemma);
        });
        return lemmaList;
    }

//    private void saveLemma(String url, Document doc){
//        HashMap<String, Float> ranks = getRankLems(doc);
//        Set<String> rankKeys = ranks.keySet();
//        Session session =getSessionFactory().openSession();
//        Transaction tran = session.beginTransaction();
//        Page page = new Page().setPath(getPath(url))
//                .setCode(doc.connection().response().statusCode())
//                .setContent(doc.toString());
//        for(String rKey : rankKeys) {
//            Lemma lemma;
//            if (listLemma.add(rKey)) {
//                lemma = new Lemma();
//                lemma.setLemma(rKey).setFrequency(1);
//            }
//            else{
//                lemma = findLemma(rKey);
//                lemma.setFrequency(lemma.getFrequency() + 1);
//            }
//            Index index=new Index(page,lemma,ranks.get(rKey));
//            session.persist(index);
//        }
//        tran.commit();
//    }

}
