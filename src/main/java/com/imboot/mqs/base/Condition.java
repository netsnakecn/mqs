package com.imboot.mqs.base;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class Condition extends HashMap<String,Object> {

    public static Condition create() {
        return new Condition();
    }

    @Override
    public Condition put(String k, Object v){
        if(k == null || v == null){
            log.error("Can not set empty key/value to map");
            return this;
        }
        super.put(k,v);
        return this;
    }
}
