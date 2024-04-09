package com.imboot.mqs.service.impl;


import lombok.extern.slf4j.Slf4j;
import com.imboot.mqs.dao.mapper.ConfigMapper;
import com.imboot.mqs.entity.Config;
import com.imboot.mqs.service.ConfigService;
import com.imboot.mqs.vo.ConfigVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private ConfigMapper configMapper;



    @Override
    public boolean getBool(String configName) {
        String v = getString(configName);
        if(v == null){
            return false;
        }
        return v.trim().equalsIgnoreCase("true") || v.trim().equalsIgnoreCase("1");
    }

    @Override
    public int getInt(String configName) {
        String v = getString(configName);
        if(v == null){
            return 0;
        }
        return Integer.parseInt(v.trim());
    }

    @Override
    public float getFloat(String configName) {
        String v = getString(configName);
        if(v == null){
            return 0;
        }
        return Float.parseFloat(v.trim());
    }

    @Override
    public String getString(String name) {
        ConfigVo params = new ConfigVo();
        params.setStatus(1);
        params.setName(name);
        List<Config> list = configMapper.list(params);
        if(list == null || list.size() < 1){
            return "";
        }
        String v = list.get(0).getValue();
        if(v == null){
            return "";
        } else {
            return v.trim();
        }
    }
}
