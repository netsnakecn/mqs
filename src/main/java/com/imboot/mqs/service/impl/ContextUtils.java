package com.imboot.mqs.service.impl;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Component
public final class ContextUtils implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(ContextUtils.class);


    private static ApplicationContext context;

    private static Environment env;

    static ConfigServiceImpl configService;


    public ContextUtils() {
    }

    public static String[] getBeanNamesForType(Class<?> clazz) {
        return context.getBeanNamesForType(clazz);
    }


    public static String getProperty(String key) {
        return env.getProperty(key);
    }

    public static boolean getBoolProperty(String key) {
        Object o = env.getProperty(key);
        if(o  == null){
            return false;
        }
        String str = o.toString();
        return str.equalsIgnoreCase("true") || str.equalsIgnoreCase("1");
    }

    public static int getIntProperty(String key) {
        String v = env.getProperty(key);
        if(StringUtils.isBlank(v)){
            return 0;
        }
        return Integer.parseInt(v.trim());
    }

    public static boolean getBooleanConfig(String configName) {
        return configService.getBool(configName);
    }

    public static int getIntConfig(String configName) {
        return configService.getInt(configName);
    }

    public static float getFloatConfig(String configName) {
        return configService.getFloat(configName);
    }

    public static String getStringConfig(String configName) {
        return configService.getString(configName);
    }
//
//    public static void setContext(ApplicationContext c) {
//        context = c;
//        log.debug("Application context initialized");
//        initService();
//    }

    private static void initService() {
        if(context == null){
            log.error("Context is null, MQS init fail.");
            return;
        }
        if (configService == null || env == null) {
            configService = context.getBean(ConfigServiceImpl.class);
            env = context.getBean(Environment.class);
            log.info("Init MQS context utils, configService:" + configService + ",env:" + env);

        }
    }

    public static Annotation findAnnotationOnBean(String beanName, Class clazz) {
        return context.findAnnotationOnBean(beanName, clazz);
    }
/*
    public static String getDataDir() {
        String UPLOAD = "/upload";
        boolean autoDetect = true;
        String baseDir = configService.getValue(DataName.baseDir.name(), 0L);
        if (StringUtils.isBlank(baseDir)) {
            baseDir = servletContext.getRealPath("/").replaceAll("/$", "");
            String[] data = baseDir.split("/");
            int offset = data.length - 3;
            StringBuffer sb = new StringBuffer();

            for(int i = 0; i < offset; ++i) {
                sb.append(data[i]).append(File.separator);
            }

            baseDir = sb.toString();
        }

        if (StringUtils.isBlank(baseDir)) {
            baseDir = System.getProperty("user.dir");
        }

        String dataDir = baseDir.replaceAll("/$", "") + "/upload";
        log.debug("当前基本目录是:" + baseDir + ",数据存储目录是:" + dataDir);
        return dataDir;
    }*/

    public static <T> T getBean(String name) {
        if (!containsBean(name)) {
            return null;
        } else {
            try {
                return (T)context.getBean(name);
            } catch (Exception var2) {
                var2.printStackTrace();
                return null;
            }
        }
    }

    public static <T> T getBean(Class<T> clz) {
        String[] names = context.getBeanNamesForType(clz);
        if (names.length < 1) {
            return null;
        } else {
            try {
                return context.getBean(clz);
            } catch (Exception var3) {
                var3.printStackTrace();
                return null;
            }
        }
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static boolean containsBean(String name) {
        return context.containsBean(name);
    }

    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return context.isSingleton(name);
    }

    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return context.getType(name);
    }

    public void setApplicationContext(ApplicationContext a) throws BeansException {
        context = a;
        log.debug("Application context initialized");
        initService();
    }
}
