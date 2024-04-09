package com.imboot.mqs.service;

public interface ConfigService {
    boolean getBool(String configName);

    int getInt(String configName);

    float getFloat(String configName);

    String getString(String name);
}
