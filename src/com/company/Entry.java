package com.company;

public class Entry {
    String para;
    String bias;
    String conseq;
    String cause;
    String protection;
    String severity;
    String possibiliy;
    String level;
    String suggestion;
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
            case 4:
                this.cause = attr;
            case 5:
                this.protection = attr;
            case 6:
                this.severity = attr;
            case 7:
                this.possibiliy = attr;
            case 8:
                this.level = attr;
            case 9:
                this.suggestion = attr;
        }

    }
}
