package com.company.IObject;

import com.company.IEvent.FinalEvent;
import com.company.IEvent.InitEvent;
import com.company.IEvent.MiddleEvent;
import org.apache.poi.ss.examples.AddDimensionedImage;

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
