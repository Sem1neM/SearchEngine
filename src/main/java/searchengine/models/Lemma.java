package searchengine.models;

import javax.persistence.*;

@Entity
@Table(name = "lemma")
public class Lemma implements Comparable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String lemma;
    private int frequency;

    public Lemma() {
    }

    public Lemma(String lemma, int frequency) {
        this.lemma = lemma;
        this.frequency = frequency;
    }

    public int getId() {
        return id;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public int compareTo(Object o) {
        Lemma lemma = (Lemma) o;
        return this.lemma.equals(lemma.lemma)? 0 : 1;
    }
}
