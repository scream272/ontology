package com.clh.protege.iobject;

import com.clh.protege.ievent.FinalEvent;
import com.clh.protege.ievent.InitEvent;
import com.clh.protege.ievent.MiddleEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/* 代表每种实验器材的参数（属性），比如
 * D-5761006*/
public class Equipment extends IObject{
    public HashMap<String, Attribute> attributeMap;
    public List<InitEvent> initEventList;
    public List<MiddleEvent> middleEventList;
    public List<FinalEvent> finalEventList; //代表危险
    public int riskScore;
    public static HashMap<String, Equipment> allEquipMap = new HashMap<String, Equipment>();
    public Equipment(String name) {
        this.name = name;
        this.attributeMap = new HashMap<>();
        this.initEventList = new ArrayList<>();
        this.middleEventList = new ArrayList<>();
        this.finalEventList = new ArrayList<>();
    }
    public static Equipment GetEquipment(String name) {
        if (allEquipMap.containsKey(name))
            return allEquipMap.get(name);
        Equipment eq = new Equipment(name);
        allEquipMap.put(name, eq);
        return eq;
    }
    public Attribute GetAttribute(String name) {
        if (attributeMap.containsKey(name))
            return attributeMap.get(name);
        Attribute attr = new Attribute(name);
        attributeMap.put(name, attr);
        return attr;
    }
}
