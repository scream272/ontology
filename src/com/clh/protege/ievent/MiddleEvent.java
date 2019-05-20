package com.clh.protege.ievent;

import com.clh.protege.iobject.Attribute;
import com.clh.protege.iobject.Equipment;

import java.util.HashMap;

public class MiddleEvent extends IEvent {
    public Equipment eq;
    public Attribute attr;
    public Attribute.Bias bias;
    public InitEvent inite;
    public FinalEvent finale;
    public String content;

    public static HashMap<String, MiddleEvent> allMiddleEventMap = new HashMap<>();
    public MiddleEvent(String content, Equipment eq, Attribute attr, Attribute.Bias bias) {
        this.content = content;
        this.eq = eq;
        this.attr = attr;
        this.bias = bias;
    }
    public static MiddleEvent GetMiddleEvent(Equipment el, Attribute attr, Attribute.Bias bias) {
        String key = el.name + attr.name + bias.name();
        if (allMiddleEventMap.containsKey(key))
            return allMiddleEventMap.get(key);
        MiddleEvent ie = new MiddleEvent(key, el, attr, bias);
        allMiddleEventMap.put(key, ie);
        return ie;
    }
}
