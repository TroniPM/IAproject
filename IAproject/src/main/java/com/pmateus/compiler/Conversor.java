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
package com.pmateus.compiler;

import edu.stanford.nlp.io.StringOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.pmateus.compiler.exception.ConversorException;
import com.pmateus.gui.JFramePrincipal;
import java.util.StringTokenizer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 *
 * @author Matt
 */
public class Conversor {

    private static Conversor conversor = null;

    public OWLOntology ontology = null;
    public OWLOntologyManager manager = null;
    public OWLDataFactory factory = null;
    public String PROJECT_IRI = null;

    private JFramePrincipal jFrameMain;

    public static Conversor getInstance() {
        if (conversor == null) {
            conversor = new Conversor();
        }

        return conversor;
    }

    public boolean init(ArrayList<CompiladorToken> arr, JFramePrincipal jFrameMain) throws ConversorException, OWLOntologyCreationException {
        this.jFrameMain = jFrameMain;

        java.util.logging.Logger.getLogger(Conversor.class.getName()).log(Level.INFO, "Size tokens: " + arr.size());
        this.PROJECT_IRI = "teste.com/#";
        this.manager = OWLManager.createOWLOntologyManager();
        this.ontology = manager.createOntology(IRI.create(PROJECT_IRI));
        this.factory = manager.getOWLDataFactory();

        for (CompiladorToken in : arr) {
            if (in.used) {
                continue;
            }

            String str = in.label.replaceAll(" +", " ").trim();

//            System.out.println(str);
            Set<OWLAxiom> list = new HashSet<OWLAxiom>();
            StringTokenizer st = new StringTokenizer(str);

            boolean flag = false;
            while (st.hasMoreTokens()) {
                String s = st.nextToken();
                if (s.equals("(") || s.equals(")")) {
                    continue;
                }

                for (CompiladorToken out : arr) {
                    if (out.id.equals(s)) {
                        System.out.println(">>>>>>> " + out.string() + " ||||||| " + in.string());
                        flag = true;
                        //list = exec(out);
                        list.addAll(exec(out));
                    }
                }
            }

            if (!flag) {
                flag = false;
                list = exec(in);

                manager.addAxioms(ontology, list);

            } else {
            }
        }
        jFrameMain.coreApp.owlRepository.currentOntology = ontology;
        jFrameMain.coreApp.owlRepository.currentOntologyManager = manager;
        jFrameMain.coreApp.owlRepository.currentDataFactory = factory;
        return true;
    }

    public Set<OWLAxiom> exec(CompiladorToken token) {//PODE SER RECURSIVO
        java.util.logging.Logger.getLogger(Conversor.class.getName()).log(Level.INFO, token.string());

        String[] term = token.label.replace("(", "").replace(")", "").replaceAll(" +", " ").trim().split(" ");
        int size = term.length;
        if (size != 3) {
            String aa = "";
            for (String in : term) {
                aa += " " + in;
            }
            try {
                throw new Exception("Expressão diferente de 3 palavras: " + aa);
            } catch (Exception ex) {
                Logger.getLogger(Conversor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        String op = term[1].toLowerCase();
        List<OWLAxiom> list = null;

        //Operações permitidas
        if (op.equals(Util.OR)
                || op.equals(Util.AND)
                || op.equals(Util.THAT)
                || op.equals(Util.ISA)
                || op.equals(Util.EQUIVALENT)
                || op.equals(Util.ALL)
                || op.equals(Util.SOME)) {
            list = new ArrayList<>();

            OWLClass esqClass = factory.getOWLClass(IRI.create(PROJECT_IRI + term[0]));
            OWLClass dirClass = factory.getOWLClass(IRI.create(PROJECT_IRI + term[2]));

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
                case Util.SOME: {
                    OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(PROJECT_IRI + term[0]));
                    OWLClassExpression exp = factory.getOWLObjectSomeValuesFrom(property, dirClass);
                    OWLSubClassOfAxiom axFinal = factory.getOWLSubClassOfAxiom(dirClass, exp);
                    list.add(axFinal);

//                    manager.addAxiom(ontology, ax1);
                    break;
                }
                case Util.ALL: {
                    OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(PROJECT_IRI + term[0]));
                    OWLClassExpression exp = factory.getOWLObjectAllValuesFrom(property, dirClass);
                    OWLSubClassOfAxiom axFinal = factory.getOWLSubClassOfAxiom(dirClass, exp);
                    list.add(axFinal);

//                    manager.addAxiom(ontology, ax1);
                    break;
                }
                /**
                 * TODO fazer esses casos abaixo;
                 */
//        cmd = substituir3(cmd, Util.SOME);
//        cmd = substituir3(cmd, Util.ALL);
//        cmd = substituir3(cmd, Util.ONLY);
                default: {
                    try {
                        throw new Exception("Erro ao procurar por operação a ser realizada.");
                    } catch (Exception ex) {
                        Logger.getLogger(Conversor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            java.util.logging.Logger.getLogger(Conversor.class.getName()).log(Level.INFO, "OK!" + token.string());

            return new HashSet<OWLAxiom>(list);

        } else {
            try {
                throw new Exception("Operação não permitida: " + op);
            } catch (Exception ex) {
                Logger.getLogger(Conversor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        java.util.logging.Logger.getLogger(Conversor.class.getName()).log(Level.INFO, "NOT OK!!" + token.string());

        return null;
    }

    public void print() {
        RDFXMLDocumentFormat f = new RDFXMLDocumentFormat();
//        ManchesterSyntaxDocumentFormat f = new ManchesterSyntaxDocumentFormat();

        StringOutputStream stream = new StringOutputStream();
        try {
            manager.saveOntology(ontology, f, stream);
            System.out.println(stream.toString());
        } catch (OWLOntologyStorageException ex) {
            Logger.getLogger(Conversor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
