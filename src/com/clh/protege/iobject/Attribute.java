package com.clh.protege.iobject;

import java.util.ArrayList;

/* 代表每种实验器材的参数（属性），比如
 * D-5761006液位过高 ，则.name == "液位" .bias = bias.high
 * Attribute由自动化文本分析得出，每分析出一个新的Attribute，
 * 就将其加入全局的allAttrList*/
public class Attribute extends IObject{
    int index; //代表该Attribute在allAttrList的位置
    enum bias {low, normal, high}
    static ArrayList<Attribute> allAttrList;
}
