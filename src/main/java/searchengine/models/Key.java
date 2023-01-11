package searchengine.models;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Key implements Serializable {
    @Column(name = "page_id")
    private Page pageId;

    @Column(name = "lemma_id")
    private Lemma lemmaId;

    public Key() {
    }

    public Key(Page pageId, Lemma lemmaId) {
        this.pageId = pageId;
        this.lemmaId = lemmaId;
    }

    public Page getPageId() {
        return pageId;
    }

    public void setPageId(Page pageId) {
        this.pageId = pageId;
    }

    public Lemma getLemmaId() {
        return lemmaId;
    }

    public void setLemmaId(Lemma lemmaId) {
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
