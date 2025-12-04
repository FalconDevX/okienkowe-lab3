package com.example.demo.service;

import java.util.List;

/**
 * Klasa do przechowywania wyników z paginacją
 */
public class PagedResult<T> {
    private final List<T> data;
    private final long totalCount;
    private final int currentPage;
    private final int pageSize;

    public PagedResult(List<T> data, long totalCount, int currentPage, int pageSize) {
        this.data = data;
        this.totalCount = totalCount;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public List<T> getData() {
        return data;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPages() {
        return pageSize > 0 ? (int) Math.ceil((double) totalCount / pageSize) : 0;
    }

    public boolean hasNext() {
        return currentPage < getTotalPages();
    }

    public boolean hasPrevious() {
        return currentPage > 1;
    }
}

