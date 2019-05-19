package com.clh.protege.protege;

import com.clh.protege.iobject.Attribute;
import com.clh.protege.iobject.Equipment;
import com.clh.protege.utils.Entry;
import com.clh.protege.utils.Log;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OwlModel {
    private static final String SOURCE = "http://clh/ontology/chemistry";
    private static final String NS = SOURCE + "#";
    private OntModelSpec ontModelSpec;
    private OntModel baseOnt;
    public static HashMap<String, OntClass> allOntClassMap = new HashMap<String, OntClass>();

    public void convertClassToOwl(String resultPath) {
        //输出owl文件到文件系统
        FileOutputStream fileOS = null;

        for (Map.Entry<String, Equipment> eqentry : Equipment.allEquipMap.entrySet()) {
            String eqname = eqentry.getKey();
            Equipment eq = eqentry.getValue();
            OntClass equipClass = baseOnt.createClass(NS + eqname);

            for (Map.Entry<String, Attribute> attrenty: eq.attributeMap.entrySet()) {
                String attrname = attrenty.getKey();
                Log.Debug("attr is " + attrname);
                Attribute attr = attrenty.getValue();
                Property attrProp = baseOnt.createProperty(NS + "attr");
                for (Map.Entry<String, Attribute.Bias> biasentry: attr.biasmap.entrySet()) {
                    String biasname = biasentry.getValue().name();
                    Property biasProp = baseOnt.createProperty(NS + "bias");
                    attrProp.addProperty(biasProp, biasname);
                }
                equipClass.addProperty(attrProp, attrname);
            }
            allOntClassMap.put(eqname, equipClass);
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
    public void test() {
        //构建本体
        OntClass event = baseOnt.createClass(NS+"Event");
        OntClass InitialEvent = baseOnt.createClass(NS+"InitialEvent");
        OntClass middleEvent = baseOnt.createClass(NS+"middleEvent");
        OntClass finalEvent = baseOnt.createClass(NS+"finalEvent");

        OntClass operation = baseOnt.createClass(NS + "Operation");
        OntClass EmergencyTreatment = baseOnt.createClass(NS + "EmergencyTreatment");
        OntClass Precaution = baseOnt.createClass(NS + "Precaution");

        OntClass Object = baseOnt.createClass(NS + "Object");
        OntClass Attribute = baseOnt.createClass(NS + "Attribute");
        OntClass Equipment = baseOnt.createClass(NS + "Equipment");
        OntClass People = baseOnt.createClass(NS + "People");

        event.addSubClass(InitialEvent);
        event.addSubClass(middleEvent);
        event.addSubClass(finalEvent);

        operation.addSubClass(EmergencyTreatment);
        operation.addSubClass(Precaution);

        Object.addSubClass(Attribute);
        Object.addSubClass(Equipment);
        Object.addSubClass(People);

        //输出owl文件到文件系统
        String filepath = "../meta.owl";
        FileOutputStream fileOS = null;
        try {
            fileOS = new FileOutputStream(filepath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        baseOnt.write(fileOS, "RDF/XML");
    }
}
