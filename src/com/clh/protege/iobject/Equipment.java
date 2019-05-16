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
    public List<Attribute> attributeList;
    public List<InitEvent> initEventList;
    public List<MiddleEvent> middleEventList;
    public List<FinalEvent> finalEventList; //代表危险
    public int riskScore;
    public static HashMap<String, Equipment> allEquipMap;
    public Equipment(String name) {
        this.name = name;
        this.attributeList = new ArrayList<>();
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
}
