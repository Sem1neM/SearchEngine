package models;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Objects;

public class Key implements Serializable {
    @Column(name = "page_id")
    private int pageId;

    @Column(name = "lemma_id")
    private int lemmaId;

    public Key(int pageId, int lemmaId) {
        this.pageId = pageId;
        this.lemmaId = lemmaId;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public int getLemmaId() {
        return lemmaId;
    }

    public void setLemmaId(int lemmaId) {
        this.lemmaId = lemmaId;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key key = (Key) o;
        return pageId == key.pageId && lemmaId == key.lemmaId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageId, lemmaId);
    }
}
