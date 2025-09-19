package com.dtcomg.system.common;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a paginated result for API responses.
 */
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * List of items for the current page.
     */
    private List<T> items;

    /**
     * Total number of items across all pages.
     */
    private long total;

    /**
     * The current page number.
     */
    private long page;

    /**
     * The number of items per page.
     */
    private long pageSize;

    public PageResult() {
    }

    public PageResult(List<T> items, long total, long page, long pageSize) {
        this.items = items;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }

    // Getters and Setters

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }
}
