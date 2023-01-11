package searchengine.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "site")
public class SiteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String url;
    @Enumerated(EnumType.STRING)
    private SiteStatus status;
    @Column(name = "status_time")
    private Date statusTime;
    @Column(name = "last_error")
    private String lastError;

    public SiteEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public SiteStatus getStatus() {
        return status;
    }

    public void setStatus(SiteStatus status) {
        this.status = status;
    }

    public Date getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(Date statusTime) {
        this.statusTime = statusTime;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }
}
