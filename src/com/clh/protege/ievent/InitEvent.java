package com.clh.protege.ievent;

import com.clh.protege.iobject.Equipment;

import java.util.HashMap;

public class InitEvent extends IEvent {
    public Equipment eq;
    public MiddleEvent middlee;
    public FinalEvent finale;
    public String content;

    public static HashMap<String, InitEvent> allInitEventMap = new HashMap<>();
    public InitEvent(Equipment eq, String content) {
        this.eq = eq;
        this.content = content;
    }
    public static InitEvent GetInitEvent(Equipment el, String content) {
        String key = el.name + content;
        if (allInitEventMap.containsKey(key))
            return allInitEventMap.get(key);
        InitEvent me = new InitEvent(el, content);
        allInitEventMap.put(key, me);
        return me;
    }
}
