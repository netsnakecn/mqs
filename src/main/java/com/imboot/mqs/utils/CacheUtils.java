package com.imboot.mqs.utils;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.imboot.mqs.base.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheUtils {
    private static final Logger log = LoggerFactory.getLogger(CacheUtils.class);
    public static Map<String, Set<String>> relationCacheMap = new HashMap();
    public static Set<String> FORCE_CACHE_ENTITY_LIST = null;
    public static Set<String> FORCE_NO_CACHE_ENTITY_LIST = null;
    public static Map<String, Boolean> cacheableCache = new HashMap();

    public CacheUtils() {
    }

    public static boolean isCacheable(Class<? extends BaseEntity> clazz) {
        if (clazz == null) {
            return false;
        } else {
            String simpleName = ClassUtils.getEntityType(clazz);
            if (cacheableCache.containsKey(simpleName)) {
                return (Boolean)cacheableCache.get(simpleName);
            } else {
                init();
                if (FORCE_CACHE_ENTITY_LIST != null && FORCE_CACHE_ENTITY_LIST.contains(simpleName)) {
                    cacheableCache.put(simpleName, true);
                    return true;
                } else if (FORCE_NO_CACHE_ENTITY_LIST != null && FORCE_NO_CACHE_ENTITY_LIST.contains(simpleName)) {
                    cacheableCache.put(simpleName, false);
                    return false;
                } else {
                    try {
                        boolean cacheable = ((BaseEntity)clazz.getDeclaredConstructor().newInstance()).isCacheable();
                        cacheableCache.put(simpleName, cacheable);
                        return cacheable;
                    } catch (Exception var3) {
                        var3.printStackTrace();
                        return false;
                    }
                }
            }
        }
    }

    public static <T extends BaseEntity> boolean isCacheable(T entity) {
        init();
        String simpleName = entity.getEntityType();
        if (FORCE_CACHE_ENTITY_LIST != null && FORCE_CACHE_ENTITY_LIST.contains(simpleName)) {
            return true;
        } else {
            return FORCE_NO_CACHE_ENTITY_LIST != null && FORCE_NO_CACHE_ENTITY_LIST.contains(simpleName) ? false : entity.isCacheable();
        }
    }

    public static void init() {
//        if (FORCE_CACHE_ENTITY_LIST == null) {
//            try {
//                FORCE_CACHE_ENTITY_LIST = (Set)JsonUtils.getInstance().readValue(ContextUtils.getStringConfig(DataName.FORCE_CACHE_ENTITY_LIST.name(), 0L), new TypeReference<Set<String>>() {
//                });
//            } catch (Exception var2) {
//                FORCE_CACHE_ENTITY_LIST = new HashSet();
//                log.info("系统未配置强制缓存列表:" + var2.getMessage());
//            }
//
//            log.info("初始化可缓存对象列表:" + FORCE_CACHE_ENTITY_LIST);
//        }
//
//        if (FORCE_NO_CACHE_ENTITY_LIST == null) {
//            try {
//                FORCE_NO_CACHE_ENTITY_LIST = (Set)JsonUtils.getInstance().readValue(ContextUtils.getStringConfig(DataName.FORCE_NO_CACHE_ENTITY_LIST.name(), 0L), new TypeReference<Set<String>>() {
//                });
//            } catch (Exception var1) {
//                FORCE_NO_CACHE_ENTITY_LIST = new HashSet();
//                log.info("系统未配置强制不缓存列表:" + var1.getMessage());
//            }
//
//            log.info("初始化不可缓存对象列表:" + FORCE_NO_CACHE_ENTITY_LIST);
//        }

    }

    public static void registerRelationCache(String cacheName, String relationTableName) {
        cacheName = cacheName.trim();
        relationTableName = relationTableName.trim();
        if (relationCacheMap == null) {
            initRelationCacheMap();
        }

        if (!relationCacheMap.containsKey(cacheName)) {
            relationCacheMap.put(cacheName, new HashSet());
        }

        ((Set)relationCacheMap.get(cacheName)).add(relationTableName);
    }

    private static void initRelationCacheMap() {
        relationCacheMap = new HashMap();
    }

    public static Set<String> getRelationCacheTables(String cacheName) {
        cacheName = cacheName.trim();
        if (relationCacheMap != null && relationCacheMap.containsKey(cacheName)) {
            Set<String> set = (Set)relationCacheMap.get(cacheName);
            return set == null ? Collections.emptySet() : set;
        } else {
            return Collections.emptySet();
        }
    }
}
