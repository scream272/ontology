package com.clh.protege.protege;

import com.clh.protege.ievent.InitEvent;
import com.clh.protege.ievent.MiddleEvent;
import com.clh.protege.iobject.Attribute;
import com.clh.protege.iobject.Equipment;
import com.clh.protege.utils.Log;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.clh.protege.utils.Log.*;

public class OwlModel {
    private static final String SOURCE = "http://clh/ontology/chemistry";
    private static final String NS = SOURCE + "#";
    private OntModelSpec ontModelSpec;
    private OntModel baseOnt;
    public static HashMap<String, OntClass> allOntClassMap = new HashMap<String, OntClass>();

    public void convertClassToOwl(String resultPath) {
        FileOutputStream fileOS = null;
        // 创建顶层Class
        OntClass ObjectClass = baseOnt.createClass(NS + "Object");
        OntClass EventClass = baseOnt.createClass(NS + "Event");
        OntClass OperationClass = baseOnt.createClass(NS + "Operation");

        OntClass equipConceptClass = baseOnt.createClass(NS + "Equipment");
        ObjectClass.addSubClass(equipConceptClass);
        OntClass attributeConceptClass = baseOnt.createClass(NS + "Attribute");
        ObjectClass.addSubClass(attributeConceptClass);
        OntClass peopleConceptClass = baseOnt.createClass(NS + "People");
        ObjectClass.addSubClass(peopleConceptClass);

        OntClass initEventConceptClass = baseOnt.createClass(NS + "InitEvent");
        EventClass.addSubClass(initEventConceptClass);
        OntClass middleEventConceptClass = baseOnt.createClass(NS + "MiddleEvent");
        EventClass.addSubClass(middleEventConceptClass);
        OntClass finalEventConceptClass = baseOnt.createClass(NS + "FinalEvent");
        EventClass.addSubClass(finalEventConceptClass);

        OntClass EmerTreatConceptClass = baseOnt.createClass(NS + "EmergencyTreatment");
        OperationClass.addSubClass(EmerTreatConceptClass);
        OntClass PrecautionConceptClass = baseOnt.createClass(NS + "Precaution");
        OperationClass.addSubClass(PrecautionConceptClass);

        // 遍历所有的仪器设备
        for (Map.Entry<String, Equipment> eqEntry : Equipment.allEquipMap.entrySet()) {
            String eqname = eqEntry.getKey();
            Equipment eq = eqEntry.getValue();
            OntClass equipClass = baseOnt.createClass(NS + eqname);
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
            OntClass initEventClass = baseOnt.createClass(NS + initEventName);
            // 新创建的initEventClass要加到全局的initEventConceptClass
            initEventConceptClass.addSubClass(initEventClass);
            // 将Event与器材关联
            initEventClass.addDisjointWith(baseOnt.getOntClass(NS + initEventEntry.getValue().eq.name));
        }
        // 遍历所有MiddleEvent
        for (Map.Entry<String, MiddleEvent> middleEventEntry: MiddleEvent.allMiddleEventMap.entrySet()) {
            String middleEventName = middleEventEntry.getKey();
            OntClass middleEventClass = baseOnt.createClass(NS + middleEventName);
            // 新创建的initEventClass要加到全局的initEventConceptClass
            middleEventConceptClass.addSubClass(middleEventClass);
            // 将Event与InitEvent关联
            Log.Debug(middleEventEntry.getValue().inite.content);
            middleEventClass.addDisjointWith(baseOnt.getOntClass(NS + middleEventEntry.getValue().inite.content));
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
