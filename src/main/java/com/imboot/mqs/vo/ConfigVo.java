package com.imboot.mqs.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import com.imboot.mqs.base.PagingVo;

@Data
@Accessors(chain = true)
public class ConfigVo extends PagingVo {

    public String name;
}
