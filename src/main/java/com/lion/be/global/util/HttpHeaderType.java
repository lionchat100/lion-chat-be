package com.lion.be.global.util;

public enum HttpHeaderType {

    AUTH("Authorization", "Bearer");

    public final String headerName;
    public final String skim;

    HttpHeaderType(String headerName, String skim) {
        this.headerName = headerName;
        this.skim = skim;
    }

}
