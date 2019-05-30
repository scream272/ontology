package com.clh.protege.protege;

import com.clh.protege.ievent.FinalEvent;
import com.clh.protege.ievent.InitEvent;
import com.clh.protege.ievent.MiddleEvent;
import com.clh.protege.iobject.Attribute;
import com.clh.protege.iobject.Equipment;
import com.clh.protege.utils.Log;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.clh.protege.utils.Log.*;

public class OwlModel {
    private static final String SOURCE = "http://clh/ontology/chemistry";
    public static final String NS = SOURCE + "#";
    private OntModelSpec ontModelSpec;
    public OntModel baseOnt;

    public void convertClassToOwl(String resultPath) {
        FileOutputStream fileOS = null;
        List<String> indection = new ArrayList<>();
        indection.add("无");
        indection.add("空白");
        indection.add("多");
        indection.add("过量");
        indection.add("少");
        indection.add("减量");
        indection.add("伴随");
        indection.add("部分");
        indection.add("相反");
        indection.add("异常");
        indection.add("早");
        indection.add("晚");
        indection.add("先");
        indection.add("后");
        // 创建顶层Class
        OntClass IndexClass = baseOnt.createClass(NS + "索引");
        OntClass equipConceptClass = baseOnt.createClass(NS + "器材");
        IndexClass.addSubClass(equipConceptClass);
        OntClass attributeConceptClass = baseOnt.createClass(NS + "属性");
        IndexClass.addSubClass(attributeConceptClass);

        OntClass OperationClass = baseOnt.createClass(NS + "操作O");
        OntClass EmerTreatConceptClass = baseOnt.createClass(NS + "补救操作");
        OperationClass.addSubClass(EmerTreatConceptClass);
        OntClass PrecautionConceptClass = baseOnt.createClass(NS + "预防类操作");
        OperationClass.addSubClass(PrecautionConceptClass);


        OntClass initEventConceptClass = baseOnt.createClass(NS + "初始原因R");
        OntClass R1 = baseOnt.createClass(NS + "人员失误");
        initEventConceptClass.addSubClass(R1);
        OntClass R2 = baseOnt.createClass(NS + "器材异常");
        initEventConceptClass.addSubClass(R2);
        OntClass R3 = baseOnt.createClass(NS + "外部异常");
        initEventConceptClass.addSubClass(R3);

        OntClass middleEventConceptClass = baseOnt.createClass(NS + "偏差P");

        OntClass finalEventConceptClass = baseOnt.createClass(NS + "后果C");
        OntClass C1 = baseOnt.createClass(NS + "人员损伤");
        finalEventConceptClass.addSubClass(C1);

        OntClass C2 = baseOnt.createClass(NS + "器材损坏");
        finalEventConceptClass.addSubClass(C2);
        OntClass c2baozha = baseOnt.createClass(NS + "器材爆炸");
        C2.addSubClass(c2baozha);
        OntClass c2huozai = baseOnt.createClass(NS + "火灾");
        C2.addSubClass(c2huozai);
        OntClass c2xielou = baseOnt.createClass(NS + "泄露");
        C2.addSubClass(c2xielou);
        OntClass c2qita = baseOnt.createClass(NS + "其他");
        C2.addSubClass(c2qita);

        OntClass C3 = baseOnt.createClass(NS + "过程终止");
        finalEventConceptClass.addSubClass(C3);

        // 遍历所有的仪器设备
        for (Map.Entry<String, Equipment> eqEntry : Equipment.allEquipMap.entrySet()) {
            String eqname = eqEntry.getKey();
            Equipment eq = eqEntry.getValue();
            OntClass equipClass = baseOnt.createClass(NS + eqname);
            eqEntry.getValue().oc = equipClass;
            equipConceptClass.addSubClass(equipClass);

            // 遍历当前Equipment的所有Attribute
            for (Map.Entry<String, Attribute> attrEntry: eq.attributeMap.entrySet()) {
                String attrName = attrEntry.getKey();
                Debug("attr is " + attrName);
                Attribute attr = attrEntry.getValue();
                OntClass attrClass = baseOnt.createClass(NS + attrName);
                // 新创建的attrClass要加到全局的attributeConceptClass
                attributeConceptClass.addSubClass(attrClass);
                for (Map.Entry<String, Attribute.Bias> biasentry: attr.biasmap.entrySet()) {
                    String biasName = biasentry.getValue().name();
                    Property biasProp = baseOnt.createProperty(NS + "bias");
                    attrClass.addProperty(biasProp, biasName);
                }
                equipClass.addSubClass(attrClass);
            }
        }

        // 遍历所有InitEvent
        for (Map.Entry<String, InitEvent> initEventEntry: InitEvent.allInitEventMap.entrySet()) {
            String initEventName = initEventEntry.getKey();
            Log.Debug(initEventName);
            OntClass initEventClass = baseOnt.createClass(NS + initEventName);
            OntProperty riskProperty = baseOnt.createOntProperty(NS + "风险");
            initEventClass.addProperty(riskProperty, initEventEntry.getValue().riskScore);
//            initEventClass.addLabel(initEventEntry.getValue().riskScore, "风险");
            initEventEntry.getValue().oc = initEventClass;

            boolean flag = false;
            for (int i = 0; i < indection.size(); i++) {
                if (initEventName.contains(indection.get(i))) {
                    flag = true;
                    break;
                }
            }
            if (initEventName.contains("人") || initEventName.contains("操作")) {
                R1.addSubClass(initEventClass);
                initEventEntry.getValue().type = R1.getLocalName();
            } else if (flag) {
                R2.addSubClass(initEventClass);
                initEventEntry.getValue().type = R2.getLocalName();
            } else {
                R3.addSubClass(initEventClass);
                initEventEntry.getValue().type = R3.getLocalName();
            }

            // 将Event与器材关联
            initEventClass.addIsDefinedBy(initEventEntry.getValue().eq.oc);
        }
        // 遍历所有MiddleEvent
        for (Map.Entry<String, MiddleEvent> middleEventEntry: MiddleEvent.allMiddleEventMap.entrySet()) {
            String middleEventName = middleEventEntry.getKey();
            OntClass middleEventClass = baseOnt.createClass(NS + middleEventName);
            middleEventEntry.getValue().oc = middleEventClass;
            // 新创建的middleEventClass要加到全局的middleEventConceptClass
            middleEventConceptClass.addSubClass(middleEventClass);
            // 将Event与InitEvent关联
            middleEventClass.addIsDefinedBy(middleEventEntry.getValue().inite.oc);
        }

        // 遍历所有FinalEvent
        for (Map.Entry<String, FinalEvent> finalEventEntry: FinalEvent.allFinalEventMap.entrySet()) {
            String finalEventName = finalEventEntry.getKey();
            OntClass finalEventClass = baseOnt.createClass(NS + finalEventName);
            finalEventEntry.getValue().oc = finalEventClass;
            if (finalEventName.contains("人") || finalEventName.contains("伤")) {
                finalEventEntry.getValue().type = C1.getLocalName();
                C1.addSubClass(finalEventClass);
            } else if (finalEventName.contains("坏") || finalEventName.contains("爆")) {
                if (finalEventName.contains("爆")) {
                    finalEventEntry.getValue().type = c2baozha.getLocalName();
                    c2baozha.addSubClass(finalEventClass);
                } else if (finalEventName.contains("火")) {
                    finalEventEntry.getValue().type = c2huozai.getLocalName();
                    c2huozai.addSubClass(finalEventClass);
                } else if (finalEventName.contains("泄")) {
                    finalEventEntry.getValue().type = c2xielou.getLocalName();
                    c2xielou.addSubClass(finalEventClass);
                } else {
                    finalEventEntry.getValue().type = c2qita.getLocalName();
                    c2qita.addSubClass(finalEventClass);
                }
            } else {
                C3.addSubClass(finalEventClass);
            }
            // 将Event与MiddleEvent关联
            finalEventClass.addIsDefinedBy(finalEventEntry.getValue().inite.oc);
            OntClass opClass = baseOnt.createClass(NS + finalEventEntry.getValue().op.name);
            PrecautionConceptClass.addSubClass(opClass);
            finalEventClass.addSubClass(opClass);
        }
        try {
            fileOS = new FileOutputStream(resultPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        baseOnt.write(fileOS, "RDF/XML");
    }
    public OwlModel() {
        ontModelSpec = new OntModelSpec(OntModelSpec.OWL_MEM);
        baseOnt = ModelFactory.createOntologyModel(ontModelSpec);
    }
}
