package com.clh.protege.utils;

public class Entry {
    public String para;
    public String bias;
    public String conseq;
    public String cause;
    public String protection;
    public String severity;
    public String possibiliy;
    public String level;
    public String suggestion;
    public static final int attrCount = 9;
    public Entry(String para, String bias, String conseq, String cause,
                 String protection, String severity, String possibiliy,
                 String level, String suggestion) {
        this.para = para;
        this.bias = bias;
        this.conseq = conseq;
        this.cause = cause;
        this.protection = protection;
        this.severity = severity;
        this.possibiliy = possibiliy;
        this.level = level;
        this.suggestion = suggestion;
    }
    public void setNumAttr(int num, String attr) {
        switch (num) {
            case 1:
                this.para = attr;
                break;
            case 2:
                this.bias = attr;
                break;
            case 3:
                this.conseq = attr;
                break;
            case 4:
                this.cause = attr;
                break;
            case 5:
                this.protection = attr;
                break;
            case 6:
                this.severity = attr;
                break;
            case 7:
                this.possibiliy = attr;
                break;
            case 8:
                this.level = attr;
                break;
            case 9:
                this.suggestion = attr;
                break;
        }
    }
    public String getNumAttr(int num) {
        switch (num) {
            case 1:
                return this.para;
            case 2:
                return this.bias;
            case 3:
                return this.conseq;
            case 4:
                return this.cause;
            case 5:
                return this.protection;
            case 6:
                return this.severity;
            case 7:
                return this.possibiliy;
            case 8:
                return this.level;
            case 9:
                return this.suggestion;
        }
        return "";
    }
}
