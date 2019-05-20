package com.clh.protege.ievent;

import com.clh.protege.iobject.Equipment;

import java.util.HashMap;

public class FinalEvent extends IEvent{
    public InitEvent inite;
    public MiddleEvent middlee;
    public Equipment eq;
    public String content;

    public static HashMap<String, FinalEvent> allFinalEventMap = new HashMap<>();
    public FinalEvent(Equipment eq, String content) {
        this.content = content;
    }
    public static FinalEvent GetFinalEvent(Equipment eq, String content) {
        if (allFinalEventMap.containsKey(content))
            return allFinalEventMap.get(content);
        FinalEvent fe = new FinalEvent(eq, content);
        allFinalEventMap.put(content, fe);
        return fe;
    }
}
