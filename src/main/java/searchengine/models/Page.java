package searchengine.models;

import javax.persistence.*;

@Entity
@Table(name = "page")
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String path;
    private int code;

    @Column(name = "content", length = 16777215, columnDefinition = "mediumtext")
    private String content;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "site_id")
    private SiteEntity siteId;

    public Page() {
    }

    public Page(String path, int code, String content, SiteEntity siteEntity) {
        this.siteId = siteEntity;
        this.path = path;
        this.code = code;
        this.content = content;
    }

    public SiteEntity getSiteId() {
        return siteId;
    }

    public void setSiteId(SiteEntity siteId) {
        this.siteId = siteId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
