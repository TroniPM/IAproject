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
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.ManchesterSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 *
 * @author Matt
 */
public class CvsC extends AtomoOntologia {

    public String id = null;

    public String esq;
    public String operacao;
    public String dir;

    public CvsC(String PROJECT_IRI, OWLOntologyManager manager, String esq, String operacao, String dir) {
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
        if (isEmpty(esq) || isEmpty(operacao) || isEmpty(dir)) {
            try {
                throw new Exception("Expressões ou operação vazia: " + esq + "|" + operacao + "|" + dir);
            } catch (Exception ex) {
                Logger.getLogger(CvsC.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        String op = this.operacao.toLowerCase();
        List<OWLAxiom> list = null;

        //Operações permitidas
        if (op.equals(Util.OR) || op.equals(Util.AND) || op.equals(Util.THAT)
                || op.equals(Util.ISA) || op.equals(Util.EQUIVALENT)) {
            list = new ArrayList<>();

            OWLClass esqClass = factory.getOWLClass(IRI.create(PROJECT_IRI + esq));
            OWLClass dirClass = factory.getOWLClass(IRI.create(PROJECT_IRI + dir));

            switch (op) {
                case Util.THAT:
                case Util.AND: {
                    OWLObjectIntersectionOf intersection = factory.getOWLObjectIntersectionOf(esqClass, dirClass);
                    OWLSubClassOfAxiom ax1 = factory.getOWLSubClassOfAxiom(dirClass, intersection);
                    OWLSubClassOfAxiom ax2 = factory.getOWLSubClassOfAxiom(esqClass, intersection);
                    list.add(ax1);
                    list.add(ax2);

//                    manager.addAxiom(ontology, list);
//                    manager.addAxiom(ontology, ax2);
                    break;
                }
                case Util.OR: {
                    OWLObjectUnionOf union = factory.getOWLObjectUnionOf(esqClass, dirClass);
                    OWLSubClassOfAxiom ax1 = factory.getOWLSubClassOfAxiom(dirClass, union);
                    OWLSubClassOfAxiom ax2 = factory.getOWLSubClassOfAxiom(esqClass, union);
                    list.add(ax1);
                    list.add(ax2);

//                    manager.addAxiom(ontology, ax1);
//                    manager.addAxiom(ontology, ax2);
                    break;
                }
                case Util.ISA: {
                    OWLSubClassOfAxiom ax1 = factory.getOWLSubClassOfAxiom(esqClass, dirClass);
                    list.add(ax1);
//                    manager.addAxiom(ontology, ax1);
                    break;
                }
                case Util.EQUIVALENT: {
                    OWLEquivalentClassesAxiom ax1 = factory.getOWLEquivalentClassesAxiom(esqClass, dirClass);
                    list.add(ax1);

//                    manager.addAxiom(ontology, ax1);
                    break;
                }
                default: {
                    try {
                        throw new Exception("Erro ao procurar por operação a ser realizada.");
                    } catch (Exception ex) {
                        Logger.getLogger(CvsC.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            return new HashSet<OWLAxiom>(list);

        } else {
            try {
                throw new Exception("Operação não permitida: " + op);
            } catch (Exception ex) {
                Logger.getLogger(CvsC.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public void print() {
        RDFXMLDocumentFormat format = new RDFXMLDocumentFormat();
        ManchesterSyntaxDocumentFormat f = new ManchesterSyntaxDocumentFormat();

        StringOutputStream stream = new StringOutputStream();
        try {
            manager.saveOntology(ontology, f, stream);
            System.out.println(stream.toString());
        } catch (OWLOntologyStorageException ex) {
            Logger.getLogger(CvsC.class.getName()).log(Level.SEVERE, null, ex);
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

        CvsC aaa = new CvsC(URI, manager, "mateus", "equivalent", "raquel");
        Set<OWLAxiom> list = aaa.execClass();

        manager.addAxioms(ontology, list);
//        aaa.exec();
//        aaa.print();

        aaa = new CvsC(URI, manager, "mateus", "isa", "humano");
        list = aaa.execClass();
        manager.addAxioms(ontology, list);
        aaa.print();
    }

    @Override
    public OWLClassExpression execProperty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
