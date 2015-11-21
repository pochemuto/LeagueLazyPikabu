package com.pochemuto.pikabu.dao;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Kramarev (pochemuto@gmail.com)
 * @date 17.11.2015
 */
@Entity
public class PikabuThread {
    @Id
    private long id;

    @ElementCollection
    private List<String> urls = new ArrayList<>();

    private String title;

    private String description;

    @Column(columnDefinition="TEXT")
    private String content;

    private boolean commented;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean hasUrls() {
        return !urls.isEmpty();
    }

    public boolean isCommented() {
        return commented;
    }

    public void setCommented(boolean commented) {
        this.commented = commented;
    }

    @Override
    public String toString() {
        return "PikabuThread{" +
            "id=" + id +
            ", urls='" + urls + '\'' +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", content='" + content + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PikabuThread)) return false;

        PikabuThread thread = (PikabuThread) o;

        return id == thread.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
