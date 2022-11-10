package models;

import javax.persistence.*;

@Entity
@Table(name = "index")
public class Index {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "page_id")
    private int page_id;
    @Column(name = "lemma_id")
    private int lemma_id;
    private float rank;

    public Index(int page_id, int lemma_id, float rank) {
        this.page_id = page_id;
        this.lemma_id = lemma_id;
        this.rank = rank;
    }

    public int getId() {
        return id;
    }

    public int getPage_id() {
        return page_id;
    }

    public void setPage_id(int page_id) {
        this.page_id = page_id;
    }

    public int getLemma_id() {
        return lemma_id;
    }

    public void setLemma_id(int lemma_id) {
        this.lemma_id = lemma_id;
    }

    public float getRank() {
        return rank;
    }

    public void setRank(float rank) {
        this.rank = rank;
    }
}
