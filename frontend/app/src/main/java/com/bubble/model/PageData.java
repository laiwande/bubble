package com.bubble.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PageData<T> {
    @SerializedName("records")
    private List<T> records;

    @SerializedName("total")
    private Long total;

    @SerializedName("size")
    private Long size;

    @SerializedName("current")
    private Long current;

    @SerializedName("pages")
    private Long pages;

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public Long getPages() {
        return pages;
    }

    public void setPages(Long pages) {
        this.pages = pages;
    }
}
