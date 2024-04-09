package com.imboot.mqs.utils;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.imboot.mqs.constants.CodeEnum;
import com.imboot.mqs.base.BaseEntity;
import com.imboot.mqs.base.BizException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@Slf4j
public class ClassUtils {
    protected static final Logger logger = LoggerFactory.getLogger(ClassUtils.class);
    static Map<String, Class<? extends BaseEntity>> classCache = new HashMap();

    public static Map<String, Set<String>> relationCacheMap = new HashMap();
    public static Set<String> FORCE_CACHE_ENTITY_LIST = new HashSet<>();
    public static Set<String> FORCE_NO_CACHE_ENTITY_LIST = new HashSet<>();
    public static Map<String, Boolean> cacheableCache = new HashMap();

    public ClassUtils() {
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

    public static void findAllClass(Object object, HashMap<String, Class> classMap) {
        if (object != null) {
            if (!object.getClass().isPrimitive()) {
                Iterator it;
                if (object instanceof List) {
                    it = ((List)object).iterator();

                    while(it.hasNext()) {
                        Object subObj = it.next();
                        findAllClass(subObj, classMap);
                    }
                } else if (object instanceof Collection) {
                    it = ((Collection)object).iterator();

                    while(it.hasNext()) {
                        findAllClass(it.next(), classMap);
                    }
                } else {
                    try {
                        BeanInfo bif = Introspector.getBeanInfo(object.getClass());
                        PropertyDescriptor[] pds = bif.getPropertyDescriptors();
                        if (pds != null) {
                            PropertyDescriptor[] var4 = pds;
                            int var5 = pds.length;

                            for(int var6 = 0; var6 < var5; ++var6) {
                                PropertyDescriptor pd = var4[var6];
                                if (!pd.getPropertyType().isPrimitive()) {
                                    Method method;
                                    Iterator var10;
                                    Object subObj;
                                    if (pd.getPropertyType().getName().indexOf("List") >= 0) {
                                        method = pd.getReadMethod();
                                        List<Object> listObject = null;

                                        try {
                                            listObject = (List)method.invoke(object);
                                        } catch (Exception var13) {
                                        }

                                        if (listObject != null && listObject.size() > 0) {
                                            var10 = listObject.iterator();

                                            while(var10.hasNext()) {
                                                subObj = var10.next();
                                                findAllClass(subObj, classMap);
                                            }
                                        }
                                    }

                                    if (pd.getPropertyType().getClass().getName().indexOf("Map") >= 0) {
                                        method = pd.getReadMethod();
                                        Map<String, Object> mapObject = null;

                                        try {
                                            mapObject = (Map)method.invoke(object);
                                        } catch (Exception var12) {
                                        }

                                        if (mapObject != null && mapObject.size() > 0) {
                                            var10 = mapObject.values().iterator();

                                            while(var10.hasNext()) {
                                                subObj = var10.next();
                                                findAllClass(subObj, classMap);
                                            }
                                        }
                                    }

                                    classMap.put(pd.getPropertyType().getName(), pd.getPropertyType());
                                }
                            }
                        }
                    } catch (Exception var14) {
                        var14.printStackTrace();
                    }

                    classMap.put(object.getClass().getName(), object.getClass());
                }

            }
        }
    }

    public static void bindBeanFromMap(Object object, Map<String, ?> requestDataMap) {
        bindBeanFromMap(object, requestDataMap, (String)null);
    }

    public static void bindBeanFromMap(Object object, Map<String, ?> requestDataMap, String prefix) {
        BeanInfo bif = null;

        try {
            bif = Introspector.getBeanInfo(object.getClass());
        } catch (IntrospectionException var16) {
            var16.printStackTrace();
        }

        if (bif == null) {
        } else {
            PropertyDescriptor[] pds = bif.getPropertyDescriptors();
            PropertyDescriptor[] var5 = pds;
            int var6 = pds.length;

            label57:
            for(int var7 = 0; var7 < var6; ++var7) {
                PropertyDescriptor pd = var5[var7];
                Iterator var9 = requestDataMap.keySet().iterator();

                while(true) {
                    String attributeName;
                    String modifyAttributeName;
                    while(true) {
                        if (!var9.hasNext()) {
                            continue label57;
                        }

                        attributeName = (String)var9.next();
                        modifyAttributeName = attributeName;
                        if (prefix == null) {
                            break;
                        }

                        if (attributeName.startsWith(prefix)) {
                            modifyAttributeName = attributeName.replaceFirst("^" + prefix, "");
                            break;
                        }
                    }

                    if (pd.getName().equals(modifyAttributeName)) {
                        Method method = pd.getWriteMethod();
                        String text = requestDataMap.get(attributeName).toString();

                        try {
                            PropertyEditor pe = PropertyEditorManager.findEditor(pd.getPropertyType());
                            if (pe == null) {
                                Logger var10000 = logger;
                                String var10001 = object.getClass().getName();
                            } else {
                                pe.setAsText(text);
                                method.invoke(object, pe.getValue());
                            }
                        } catch (Exception var15) {
                            var15.printStackTrace();
                        }
                    }
                }
            }

        }
    }


    public static String getValue(Object object, String attributeName, String columnType) {


        BeanInfo bif = null;

        try {
            bif = Introspector.getBeanInfo(object.getClass());
        } catch (IntrospectionException var12) {
            var12.printStackTrace();
        }

        if (bif == null) {
            return null;
        } else {
            PropertyDescriptor[] pds = bif.getPropertyDescriptors();
            PropertyDescriptor[] var5 = pds;
            int var6 = pds.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                PropertyDescriptor pd = var5[var7];
                if (pd.getName().equals(attributeName)) {
                    Method method = pd.getReadMethod();

                    try {
                        Object result = method.invoke(object);
                        if (result != null) {
                            return result.toString();
                        }
                    } catch (Exception var11) {
                        var11.printStackTrace();
                    }

                    return null;
                }
            }

            return null;
        }
    }

    public static String getEntityType(Class<?> clazz) {
        return StringUtils.uncapitalize(clazz.getSimpleName());
    }

    public static long getObjectId(BaseEntity eisObject) {
        if (eisObject.getId() > 0L) {
            return eisObject.getId();
        } else {
            String value = getValue(eisObject, getEntityType(eisObject.getClass()) + "Id", "native");
            return StringUtils.isNumeric(value) ? Long.parseLong(value) : 0L;
        }
    }

    public static void copyProperties(Object fromObject, Object toObject, Set<String> copyProperties) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException {
        if (fromObject.getClass() == toObject.getClass()) {
            BeanInfo bif = Introspector.getBeanInfo(fromObject.getClass());
            if (bif == null) {
            } else {
                PropertyDescriptor[] pds = bif.getPropertyDescriptors();
                if (pds != null && pds.length >= 1) {
                    PropertyDescriptor[] var5 = pds;
                    int var6 = pds.length;

                    for(int var7 = 0; var7 < var6; ++var7) {
                        PropertyDescriptor pd = var5[var7];
                        Iterator var9 = copyProperties.iterator();

                        while(var9.hasNext()) {
                            String attributeName = (String)var9.next();
                            if (pd.getName().equals(attributeName)) {
                                Method writeMethod = pd.getWriteMethod();
                                Method readMethod = pd.getReadMethod();
                                PropertyEditor pe = PropertyEditorManager.findEditor(pd.getPropertyType());
                                if (pe != null) {
                                    Object attributeValue = readMethod.invoke(fromObject);
                                    if (attributeValue == null) {
                                        logger.error("Can not read:" + attributeName);
                                    } else {
                                        pe.setAsText(attributeValue.toString());
                                        writeMethod.invoke(toObject, pe.getValue());
                                        if (logger.isDebugEnabled()) {
                                        }
                                    }
                                } else {
                                    logger.error("PropertyEditor:" + attributeName + " not found");
                                }
                                break;
                            }
                        }
                    }

                } else {
                    logger.error("PropertyDescriptor:" + fromObject.getClass().getName() + " not found");
                }
            }
        }
    }

    public static Set<String> getPropertyNames(Class<?> clazz) {
        BeanInfo bif = null;

        try {
            bif = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException var8) {
            var8.printStackTrace();
        }

        if (bif == null) {
            throw new BizException(CodeEnum.DATA_ILLEGAL.code, "");
        } else {
            Set<String> names = new HashSet();
            PropertyDescriptor[] pds = bif.getPropertyDescriptors();
            PropertyDescriptor[] var4 = pds;
            int var5 = pds.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                PropertyDescriptor pd = var4[var6];
                names.add(pd.getName());
            }

            return names;
        }
    }
//
//    public static <T> T textToObj(Class<T> clazz, String text) {
//        if (StringUtils.isBlank(text)) {
//            return null;
//        } else {
//            try {
//                return JsonUtils.getInstance().readValue(text, clazz);
//            } catch (Exception var3) {
//                var3.printStackTrace();
//                return null;
//            }
//        }
//    }
//
//    public static <T> T jsonToObj(Class<T> clazz, JsonNode json) {
//        return json != null && !json.isNull() && !json.isEmpty() ? textToObj(clazz, json.toString()) : null;
//    }

    public static Class<? extends BaseEntity> getGenericParameter(Object o) {
        String key = o.getClass().getName();
        if (classCache.containsKey(key)) {
            return (Class)classCache.get(key);
        } else {
            Logger var10000;
            try {
                Type genericSuperClass = o.getClass().getGenericSuperclass();
                ParameterizedType p = null;

                for(int count = 0; p == null && count < 10; ++count) {
                    if (genericSuperClass instanceof ParameterizedType) {
                        p = (ParameterizedType)genericSuperClass;
                    } else {
                        genericSuperClass = ((Class)genericSuperClass).getGenericSuperclass();
                    }
                }

                if (p != null) {
                    Class<? extends BaseEntity> clazz = (Class)p.getActualTypeArguments()[0];
                    var10000 = logger;
                    String var7 = clazz.getName();
                    var10000.debug("Determined actual type:" + var7 + " from object:" + key);
                    classCache.put(key, clazz);
                    return clazz;
                }
            } catch (Exception var6) {
                var10000 = logger;
                Class var10001 = o.getClass();
                var10000.warn("Can not read:" + var10001 + "'s generic:" + var6.getMessage());
            }

            return null;
        }
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

}
