package com.company.IObject;

import com.company.IEvent.FinalEvent;
import com.company.IEvent.InitEvent;
import com.company.IEvent.MiddleEvent;

import java.util.ArrayList;
import java.util.List;
/* 代表每种实验器材的参数（属性），比如
 * D-5761006*/
public class Equipment extends IObject{
    public List<Attribute> attributeList;
    public List<InitEvent> initEventList;
    public List<MiddleEvent> middleEventList;
    public List<FinalEvent> finalEventList; //代表危险
    public int riskScore;
    public int index;
    public static ArrayList<Equipment> allEquipList;
    public Equipment(String name) {
        this.name = name;
        attributeList = new ArrayList<>();
        initEventList = new ArrayList<>();
        middleEventList = new ArrayList<>();
        finalEventList = new ArrayList<>();
        this.index = allEquipList.size();
        allEquipList.add(this);
    }
}
