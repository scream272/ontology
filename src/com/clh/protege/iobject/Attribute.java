package com.clh.protege.iobject;

import java.util.HashMap;

/* 代表每种实验器材的参数（属性），比如
 * D-5761006液位过高 ，则.name == "液位" .bias = bias.high
 * Attribute由自动化文本分析得出，每分析出一个新的Attribute，
 * 就将其加入全局的allAttrMap*/

public class Attribute extends IObject{
    public enum Bias {low, abnormal, high}
    public HashMap<String, Bias> biasmap;

    public Attribute(String name) {
        this.name = name;
        this.biasmap = new HashMap<>();
    }

    public void AddBias(String name) {
        if (name.contains("高") || name.contains("大")) {
            this.biasmap.put(name, Bias.high);
        } else if (name.contains("低") || name.contains("小")) {
            this.biasmap.put(name, Bias.low);
        } else {
            this.biasmap.put(name, Bias.abnormal);
        }
    }
}
