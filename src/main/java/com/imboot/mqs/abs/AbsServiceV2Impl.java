package com.imboot.mqs.abs;

import com.imboot.mqs.base.*;
import com.imboot.mqs.service.impl.ContextUtils;
import com.imboot.mqs.constants.CodeEnum;
import com.imboot.mqs.constants.NameEnum;
import com.imboot.mqs.utils.CacheUtils;
import com.imboot.mqs.utils.ClassUtils;
import com.imboot.mqs.utils.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.*;

public abstract class AbsServiceV2Impl<T extends BaseEntity, Q extends PagingVo, M extends IDaoV2<T,Q>> implements IServiceV2<T,Q> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    protected PropertyHolder encryptPropertyPlaceholderConfigurer;
    @Autowired
    protected M mapper;
    @Autowired
    protected RedisCacheManager cacheManager;
    static RedisTemplate<Object, Object> redisTemplate;

    public AbsServiceV2Impl() {
    }

    public long genUuid() {
        int serverId = StringUtils.parseInt(this.encryptPropertyPlaceholderConfigurer.getProperty("systemServerId"));
        if (redisTemplate == null) {
            redisTemplate = (RedisTemplate) ContextUtils.getBean("redisTemplate");
        }

        String key = "UUID_SEQUENCE";
        long inc = redisTemplate.opsForValue().increment(key);
        return Long.parseLong("" + serverId + RandomStringUtils.randomNumeric(3) + inc);
    }

    protected boolean getBoolProperty(String key) {
        String v = this.encryptPropertyPlaceholderConfigurer.getProperty(key);
        return StringUtils.isPositive(v);
    }

    protected T fromCache(String cacheType, String key) {
        String cacheName = this.getCacheName(cacheType);
        Object o = null;
        if (this.cacheManager.getCache(cacheName) != null) {
            Cache.ValueWrapper vw = this.cacheManager.getCache(cacheName).get(key);
            if (vw != null) {
                o = vw.get();
                this.logger.debug("Reading:" + cacheName + ",target:" + key + "=>" + o);
                if (o != null) {
                    try {
                        return (T) o;
                    } catch (Exception var7) {
                        this.logger.error("Ca not transfer:" + o.getClass().getName() + " to required object");
                    }
                }
            }
        }

        return null;
    }

    protected void putCache(BaseEntity entity) {
        if (entity == null) {
            this.logger.error("Null object");
        } else if (!CacheUtils.isCacheable(entity)) {
            this.logger.warn("Object:" + entity + " disallow cache");
        } else {
            String cacheName = this.getCacheName(entity);
            String var10000 = entity.getEntityType();
            String key = var10000 + "#" + entity.getId();
            this.logger.debug("Put :" + entity + " to cache:" + cacheName + "=>" + key);

            try {
                this.cacheManager.getCache(cacheName).put(key, entity);
            } catch (Exception var5) {
                var5.printStackTrace();
            }

        }
    }

    private String getCacheName(BaseEntity entity) {
        return this.getCacheName(entity.getEntityType());
    }

    private String getCacheName(String entityName) {
        String systemCode = this.encryptPropertyPlaceholderConfigurer.getProperty(NameEnum.systemCode.name());
        return "EIS_CACHE_" + systemCode + "_" + StringUtils.uncapitalize(entityName);
    }

    protected void removeCache(T entity) {
        if (entity == null) {
            this.logger.error("Try remove object is null");
        } else {
            String cacheName = this.getCacheName(entity);
            if (this.cacheManager.getCache(cacheName) != null && CacheUtils.isCacheable(entity)) {
                String var10000 = entity.getEntityType();
                String key = var10000 + "#" + entity.getId();
                this.logger.debug("Remove :" + entity + " from cache:" + cacheName + ",key:" + key);
                ((Cache) Objects.requireNonNull(this.cacheManager.getCache(cacheName))).evictIfPresent(key);
            }

        }
    }

    public T select(long id) {
        if (id <= 0L) {
            this.logger.error("Wrong select, id is 0 at " + StringUtils.stackTrace(Thread.currentThread().getStackTrace()));
            return null;
        } else {
            Class<? extends BaseEntity> clazz = ClassUtils.getGenericParameter(this);
            boolean isCacheable = CacheUtils.isCacheable(clazz);
            BaseEntity entity;
            if (clazz != null) {
                String cacheName = ClassUtils.getEntityType(clazz);
                if (isCacheable) {
                    entity = this.fromCache(cacheName, cacheName + "#" + id);
                    if (entity != null) {
                        return (T)entity;
                    }
                }
            }

            String var10001 = clazz == null ? "null" : clazz.getName();
            this.logger.debug("Performance db select for clazz:" + var10001 + "#" + id);
            entity =  this.mapper.select(id);
            if (entity != null) {
                this.afterFetch((T) entity);
                if (isCacheable) {
                    this.putCache(entity);
                }
            }

            return (T)entity;
        }
    }

    public T select(T model) {
        T entity = null;
        boolean isCacheable = CacheUtils.isCacheable(model);
        String var10001;
        if (isCacheable) {
            var10001 = model.getEntityType();
            String var10002 = model.getEntityType();
            entity = this.fromCache(var10001, var10002 + "#" + model.getId());
            if (entity != null) {
                return entity;
            }
        }

        Logger var10000 = this.logger;
        var10001 = model.getClass().getSimpleName();
        var10000.debug("Performance db select for clazz:" + var10001 + "#" + model.getId());
        entity = this.mapper.select(model.getId());
        if (entity != null) {
            this.afterFetch(entity);
            if (isCacheable) {
                this.putCache(entity);
            }
        }

        return entity;
    }

    public int insert(T entity) {
        if (entity.getId() <= 0L) {
          //  entity.setId(this.genUuid());
        }

        Logger var10000 = this.logger;
        String var10001 = entity.getEntityType();
        var10000.debug("Try to insert new entity:" + var10001 + "#" + entity.getId());

        int rs;
        try {
            rs = this.mapper.insert(entity);
        } catch (DuplicateKeyException var4) {
            var10000 = this.logger;
            var10001 = entity.getEntityType();
            var10000.error("Can not insert entity:" + var10001 + "#" + entity.getId() + ", because duplicate key");
            return CodeEnum.DATA_DUPLICATE.code;
        }

        if (rs == 1) {
            this.putCache(entity);
        }

        return rs;
    }

    public int update(T entity) {
        Logger var10000 = this.logger;
        String var10001 = entity.getEntityType();
        var10000.debug("Try to update entity:" + var10001 + "#" + entity.getId());
         int rs;
        if (entity.getId() > 0L && this.mapper.select(entity.getId()) == null) {
            rs = this.mapper.insert(entity);
        } else {
            rs = this.mapper.update(entity);
        }

        if (rs == 1 && CacheUtils.isCacheable(entity)) {
            this.putCache(entity);
        }

        return rs;
    }

    public int updateBy(int expectCount, Q paramMap) {
        throw new BizException(CodeEnum.SYS_EXCEPTION.code, "Invalid access");
    }

    public T selectOne(Q paramMap) {
       // PagingUtils.paging(paramMap, 1, 1);
        List<T> list = this.list(paramMap);
        return list != null && list.size() >= 1 ? list.get(0) : null;
    }

    @Override
    public List<T> list(Q paramMap) {
        Class<? extends BaseEntity> clazz = ClassUtils.getGenericParameter(this);
        boolean isCacheable = CacheUtils.isCacheable(clazz);
        String cacheType = clazz == null ? null : ClassUtils.getEntityType(clazz);
         List<T> list = new ArrayList();
        if (cacheType != null && isCacheable) {
            List<Long> idList = null;


            if (idList == null) {
                idList = this.mapper.listPk(paramMap);
            }

            if (idList == null) {
                return Collections.emptyList();
            }

            Iterator var15 = idList.iterator();

            while (var15.hasNext()) {
                long id = (Long) var15.next();
                if (id > 0L) {
                    String key = cacheType + "#" + id;
                    T cached = this.fromCache(cacheType, key);
                    if (cached == null) {
                        this.logger.debug("Can not read object from :" + key);
                        T entity = this.select(id);
                        if (entity != null) {
                            this.afterFetch(entity);
                            if (CacheUtils.isCacheable(entity)) {
                                this.putCache(entity);
                            }

                            ((List) list).add(entity);
                        }
                    } else {
                        ((List) list).add(cached);
                    }
                }
            }
        } else {
            this.logger.debug("Performance non cache list,type=" + cacheType);
            list = this.mapper.list(paramMap);
            if (list != null && ((List) list).size() > 0) {
                Iterator var6 = ((List) list).iterator();

                while (var6.hasNext()) {
                    T entity = (T) var6.next();
                    this.afterFetch(entity);
                }
            }
        }

        return (List) list;
    }

    public void afterFetch(T entity) {
    }

    public int delete(long id) {
        return this.mapper.delete(id);
    }

    public int deleteBy(Q paramMap) {
        List<T> list = null;
        if (CacheUtils.isCacheable(ClassUtils.getGenericParameter(this))) {
            list = this.list(paramMap);
        }

        int rs = this.mapper.deleteBy(paramMap);
        if (list != null && rs != list.size()) {
            this.logger.warn("Conditional delete,db exist count:" + rs + " not match required count :" + list.size());
        }

        if (list != null) {
            list.forEach(this::removeCache);
        }

        return rs;
    }

    public int count(Q paramMap) {
        return this.mapper.count(paramMap);
    }

    public List<T> listOnPage(Q paramMap) {
        return this.list(paramMap);
    }
}

