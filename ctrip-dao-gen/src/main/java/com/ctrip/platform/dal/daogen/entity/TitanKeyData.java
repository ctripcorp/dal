package com.ctrip.platform.dal.daogen.entity;

import java.util.List;

/**
 * Created by taochen on 2019/7/26.
 */
public class TitanKeyData {
    private int totalPage;

    private int total;

    private int pageSize;

    private int page;

    private List<TitanKeyAPIInfo> data;

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<TitanKeyAPIInfo> getData() {
        return data;
    }

    public void setData(List<TitanKeyAPIInfo> data) {
        this.data = data;
    }
}
