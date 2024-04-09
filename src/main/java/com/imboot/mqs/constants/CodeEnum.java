package com.imboot.mqs.constants;

public enum CodeEnum {

  DATA_ILLEGAL(1000),

    DATA_DUPLICATE(1001), SYS_EXCEPTION(1002);

    public final int code;

    CodeEnum(int c) {
        this.code = c;
    }
}
