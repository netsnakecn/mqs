package com.imboot.mqs.base;


import java.util.List;

public interface IServiceV2  <T extends BaseEntity, Q extends PagingVo> {
        int insert(T v);

        int update(T v);

        int updateBy(Q var2);

        T select(long v);

        T select(T v);

        T selectOne(Q v);

        List<T> list(Q v);

        int delete(long v);

        int deleteBy(Q v);

        int count(Q v);

        List<T> listOnPage(Q v);
    }