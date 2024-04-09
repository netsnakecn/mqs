package com.imboot.mqs.base;



import java.util.List;

public interface IDaoV2<T, Q extends PagingVo> {
    int insert(T var1);

    int update(T var1);

    int updateBy(Q var1);

    T select(long var1);

    List<T> list(Q var1);

    int delete(long var1);

    int deleteBy(Q var1);

    int count(Q var1);

    List<Long> listPk(Q var1);
}
