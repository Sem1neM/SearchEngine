package searchengine.dto.index;

import searchengine.models.Page;
import searchengine.models.Lemma;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;


public class Link {
    private final String url;
    private volatile Link parent;
    private volatile int depth;
    private volatile CopyOnWriteArraySet<Link> children;
    private volatile List<Lemma> lemmas = new ArrayList<>();
    private volatile Page page;

    private volatile Map<String,Integer> bodyMap;
    private volatile Map<String, Integer> titleMap;


    private volatile int code;
    private volatile String htmlFile;


    public Link(String url) {
        code = 0;
        htmlFile = null;
        this.url = url;
        children = new CopyOnWriteArraySet<>();
        depth = 0;
        parent = null;

    }
    public void addLemma(Lemma lemma){
        lemmas.add(lemma);
    }

    public List<Lemma> getLemmas() {
        return lemmas;
    }

    public void setLemmas(List<Lemma> lemmaList) {
        this.lemmas = lemmaList;
    }

    private void setParent(Link link) {
        synchronized (this) {
            this.parent = link;
            this.depth = setDepth();
        }
    }

    public int getDepth() {
        return depth;
    }

    private int setDepth() {
        if (parent == null) {
            return 0;
        }
        return 1 + parent.getDepth();
    }


    public CopyOnWriteArraySet<Link> getChildren() {
        return children;
    }

    public String getUrl() {
        return url;
    }

    public void addChildren(Link link) {
        if (!children.contains(link) && link.getUrl().startsWith(url)) {
            this.children.add(link);
            link.setParent(this);
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getHtmlFile() {
        return htmlFile;
    }

    public void setHtmlFile(String htmlFile) {
        this.htmlFile = htmlFile;
    }

    public Map<String, Integer> getBodyMap() {
        return bodyMap;
    }

    public void setBodyMap(Map<String, Integer> bodyMap) {
        this.bodyMap = bodyMap;
    }

    public Map<String, Integer> getTitleMap() {
        return titleMap;
    }

    public void setTitleMap(Map<String, Integer> titleMap) {
        this.titleMap = titleMap;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}

