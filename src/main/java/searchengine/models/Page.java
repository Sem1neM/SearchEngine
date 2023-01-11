package searchengine.models;

import javax.persistence.*;
import java.util.Set;

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

    public Page() {
    }

    public Page(String path, int code, String content) {
        this.path = path;
        this.code = code;
        this.content = content;
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
