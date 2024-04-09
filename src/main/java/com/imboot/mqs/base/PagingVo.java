package com.imboot.mqs.base;

import lombok.Data;

@Data
public class PagingVo {
    public long id;
    public int status;
    public int page;
    public int rows;
    public int limits;
    public int starts;
    public int downloadMode;
    public String orderBy;
}
