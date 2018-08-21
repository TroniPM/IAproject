/*
 * Copyright 2018 Paulo Mateus da Silva.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pmateus.compiler.classes;

import com.pmateus.compiler.Util;
import com.pmateus.util.RandomString;
import edu.stanford.nlp.io.StringOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.ManchesterSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 *
 * @author Matt
 */
public class PvsC extends AtomoOntologia {

    public String id = null;

    public String esq;
    public String operacao;
    public String dir;

    public PvsC(String PROJECT_IRI, OWLOntologyManager manager, String esq, String operacao, String dir) {
        super(PROJECT_IRI, manager);
        this.esq = esq;
        this.operacao = operacao;
        this.dir = dir;

        this.id = new RandomString(15).nextString();
    }

    private static boolean isEmpty(String a) {
        return a == null || a.trim().isEmpty();
    }

    @Override
    public Set<OWLAxiom> execClass() {

        return null;
    }

//    private void makeRelation(String label, String children, String parent, String quantificador) {
//        OWLObjectProperty relacaoLabel = currentDataFactory.getOWLObjectProperty(IRI.create(PROJECT_IRI + label));
//        OWLClass childrenClass = currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + children));
//        OWLClass parentClass = currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + parent));
//
//        OWLObjectPropertyDomainAxiom ax1 = currentDataFactory.getOWLObjectPropertyDomainAxiom(relacaoLabel, childrenClass);
//        OWLObjectPropertyRangeAxiom ax2 = currentDataFactory.getOWLObjectPropertyRangeAxiom(relacaoLabel, parentClass);
//        AddAxiom addAx = new AddAxiom(currentOntology, ax1);
//        currentOntologyManager.applyChange(addAx);
//        addAx = new AddAxiom(currentOntology, ax2);
//        currentOntologyManager.applyChange(addAx);
//
//        if (quantificador.equals(InsertionAnalyser.command_qtf_existencial)) {
//            OWLClassExpression relationExp = currentDataFactory.getOWLObjectSomeValuesFrom(relacaoLabel, parentClass);
//            OWLSubClassOfAxiom axFinal = currentDataFactory.getOWLSubClassOfAxiom(childrenClass, relationExp);
//            addAx = new AddAxiom(currentOntology, axFinal);
//            currentOntologyManager.applyChange(addAx);
//        } else if (quantificador.equals(InsertionAnalyser.command_qtf_universal)) {
//            OWLClassExpression relationExp = currentDataFactory.getOWLObjectAllValuesFrom(relacaoLabel, parentClass);
//            OWLSubClassOfAxiom axFinal = currentDataFactory.getOWLSubClassOfAxiom(childrenClass, relationExp);
//            addAx = new AddAxiom(currentOntology, axFinal);
//            currentOntologyManager.applyChange(addAx);
//        }
//    }
    public void print() {
        RDFXMLDocumentFormat format = new RDFXMLDocumentFormat();
        ManchesterSyntaxDocumentFormat f = new ManchesterSyntaxDocumentFormat();

        StringOutputStream stream = new StringOutputStream();
        try {
            manager.saveOntology(ontology, f, stream);
            System.out.println(stream.toString());
        } catch (OWLOntologyStorageException ex) {
            Logger.getLogger(PvsC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override

    public String toString() {
        return esq + " " + operacao + " " + dir;
    }

    public static void main(String[] args) throws OWLOntologyCreationException {
//    public OWLOntologyManager currentOntologyManager = null;
//    public OWLOntology currentOntology = null;
        String URI = "";
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.createOntology(IRI.create(URI));

        PvsC a = new PvsC(URI, manager, "hasMinEffectiveFocalLength", "value", "35");
        OWLClassExpression list = a.execProperty();

        a = new PvsC(URI, manager, "hasMaxEffectiveFocalLength", "value", "120");
        list = a.execProperty();

//        a = new PvsC(URI, manager, "temFilha", "value", "1");
//        list = a.execClass();
//        manager.addAxioms(ontology, list);

//        CvsC b = new CvsC(URI, manager, "raquel", "isa", "humano");
//        Set<OWLAxiom> list2 = b.exec();
//        manager.addAxioms(ontology, list2);

        a.print();
    }

    @Override
    public OWLClassExpression execProperty() {
        if (isEmpty(esq) || isEmpty(operacao) || isEmpty(dir)) {
            try {
                throw new Exception("Expressões ou operação vazia: " + esq + "|" + operacao + "|" + dir);
            } catch (Exception ex) {
                Logger.getLogger(PvsC.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        String op = this.operacao.toLowerCase();
        List<OWLAxiom> list = null;

        //Operações permitidas
        if (op.equals(Util.SOME) || op.equals(Util.ONLY) || op.equals(Util.ALL)) {
            list = new ArrayList<>();

            OWLClass dirClass = factory.getOWLClass(IRI.create(PROJECT_IRI + dir));

            switch (op) {
                case Util.SOME: {
                    OWLObjectProperty propriedade = factory.getOWLObjectProperty(IRI.create(PROJECT_IRI + esq));
                    OWLClassExpression relationExp = factory.getOWLObjectSomeValuesFrom(propriedade, dirClass);
                    return relationExp;
                }
                case Util.ONLY: {
                    OWLObjectProperty propriedade = factory.getOWLObjectProperty(IRI.create(PROJECT_IRI + esq));
                    OWLClassExpression relationExp = factory.getOWLObjectSomeValuesFrom(propriedade, dirClass);
                    return relationExp;
                }
                case Util.ALL: {
                    OWLObjectProperty propriedade = factory.getOWLObjectProperty(IRI.create(PROJECT_IRI + esq));
                    OWLClassExpression relationExp = factory.getOWLObjectAllValuesFrom(propriedade, dirClass);
                    return relationExp;
                }
                default: {
                    try {
                        throw new Exception("Erro ao procurar por operação a ser realizada.");
                    } catch (Exception ex) {
                        Logger.getLogger(PvsC.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else if (op.equals(Util.MIN) || op.equals(Util.MAX) || op.equals(Util.EXACTLY) || op.equals(Util.VALUE)) {
            list = new ArrayList<>();
            int card = 0;
            try {
                card = Integer.parseInt(dir);
            } catch (Exception e) {
                try {
                    throw new Exception("Number invalid at '" + this.toString() + "'");
                } catch (Exception ex) {
                    Logger.getLogger(PvsC.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            switch (op) {
                case Util.MIN: {
                    OWLObjectProperty propriedade = factory.getOWLObjectProperty(IRI.create(PROJECT_IRI + esq));
                    OWLClassExpression relationExp = factory.getOWLObjectMinCardinality(card, propriedade);
                    return relationExp;
                }
                case Util.MAX: {
                    OWLObjectProperty propriedade = factory.getOWLObjectProperty(IRI.create(PROJECT_IRI + esq));
                    OWLClassExpression relationExp = factory.getOWLObjectMaxCardinality(card, propriedade);
                    return relationExp;
                }
                case Util.EXACTLY:
                case Util.VALUE: {
                    OWLObjectProperty propriedade = factory.getOWLObjectProperty(IRI.create(PROJECT_IRI + esq));
                    OWLClassExpression relationExp = factory.getOWLObjectExactCardinality(card, propriedade);
                    return relationExp;
                }
                default: {
                    try {
                        throw new Exception("Erro ao procurar por operação a ser realizada.");
                    } catch (Exception ex) {
                        Logger.getLogger(PvsC.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            try {
                throw new Exception("Operação não permitida: " + op);
            } catch (Exception ex) {
                Logger.getLogger(PvsC.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}
