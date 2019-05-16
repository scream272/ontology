package com.clh.protege.protege;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ConvertClassToOwl {
    public void test() {
        final String SOURCE = "http://clh/ontology/chemistry";
        final String NS = SOURCE + "#";
        OntModelSpec ontModelSpec = new OntModelSpec(OntModelSpec.OWL_MEM);
        // asserted ontology
        OntModel baseOnt = ModelFactory.createOntologyModel(ontModelSpec);

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
