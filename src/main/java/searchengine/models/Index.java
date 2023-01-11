package searchengine.models;

import javax.persistence.*;

@Entity
@Table(name = "index_page")
public class Index {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id")
    private Page pageId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "lemma_id")
    private Lemma lemmaId;

    @Column(name = "page_rank")
    private float rank;

    public Index() {
    }

    public Index(Page pageId, Lemma lemmaId, float rank) {
        this.pageId = pageId;
        this.lemmaId = lemmaId;
        this.rank = rank;
    }

    public int getId() {
        return id;
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

    public float getRank() {
        return rank;
    }

    public void setRank(float rank) {
        this.rank = rank;
    }
}
