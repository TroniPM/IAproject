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
import java.util.Arrays;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
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

    public ArrayList<CompiladorToken> tokens = null;

    private JFramePrincipal jFrameMain;

    public static Conversor getInstance() {
        if (conversor == null) {
            conversor = new Conversor();
        }

        return conversor;
    }

    public boolean init(ArrayList<CompiladorToken> arr, JFramePrincipal jFrameMain) throws ConversorException, OWLOntologyCreationException {
        this.jFrameMain = jFrameMain;
        this.tokens = arr;

        java.util.logging.Logger.getLogger(Conversor.class.getName()).log(Level.INFO, "Size tokens: " + arr.size());
        if (jFrameMain.filename.trim().isEmpty()) {
            this.PROJECT_IRI = jFrameMain.currentDocument + ".com/#";
        } else {
            this.PROJECT_IRI = jFrameMain.filename + ".com/#";
        }
        this.manager = OWLManager.createOWLOntologyManager();
        this.ontology = manager.createOntology(IRI.create(PROJECT_IRI));
        this.factory = manager.getOWLDataFactory();

        for (CompiladorToken in : arr) {
            if (in.used) {
                continue;
            }

            String str = in.label.replaceAll(" +", " ").trim();

            Set<OWLAxiom> list = new HashSet<OWLAxiom>();
            StringTokenizer st = new StringTokenizer(str);

            boolean flag = false;
            while (st.hasMoreTokens()) {
                String s = st.nextToken();
                if (s.equals("(") || s.equals(")")) {
                    continue;
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

    public Set<OWLAxiom> exec(CompiladorToken token) throws ConversorException {//PODE SER RECURSIVO
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
        Set<OWLAxiom> listEsq = null, listDir = null;
        for (CompiladorToken in : tokens) {
            if (in.id.equals(term[0])) {
                listEsq = exec(in);
            }
            if (in.id.equals(term[2])) {
                listDir = exec(in);
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
                || op.equals(Util.ONLY)
                || op.equals(Util.SOME)) {
            list = new ArrayList<>();

            OWLClass esqClass = factory.getOWLClass(IRI.create(PROJECT_IRI + term[0]));
            OWLClass dirClass = factory.getOWLClass(IRI.create(PROJECT_IRI + term[2]));

            switch (op) {
                case Util.THAT:
                case Util.AND: {
                    if (listEsq != null && listDir == null) {
                        List<OWLAxiom> listaa = intersecao(listEsq, dirClass);
                        list.addAll(listaa);
                    } else if (listDir != null && listEsq == null) {
                        List<OWLAxiom> listaa = intersecao(listDir, esqClass);
                        list.addAll(listaa);
                    } else if (listEsq != null && listDir != null) {
                        List<OWLAxiom> listaa = intersecao(listEsq, listDir);
                        list.addAll(listaa);
                    } else {
                        OWLSubClassOfAxiom ax1 = factory.getOWLSubClassOfAxiom(dirClass, factory.getOWLObjectIntersectionOf(esqClass));
                        OWLSubClassOfAxiom ax2 = factory.getOWLSubClassOfAxiom(esqClass, factory.getOWLObjectIntersectionOf(dirClass));
                        list.add(ax1);
                        list.add(ax2);
                    }
                    break;
                }
                case Util.OR: {
                    if (listEsq != null && listDir == null) {
                        List<OWLAxiom> listaa = uniao(listEsq, dirClass);
                        list.addAll(listaa);
                    } else if (listDir != null && listEsq == null) {
                        List<OWLAxiom> listaa = uniao(listDir, esqClass);
                        list.addAll(listaa);
                    } else if (listEsq != null && listDir != null) {
                        List<OWLAxiom> listaa = uniao(listEsq, listDir);
                        list.addAll(listaa);
                    } else {
                        OWLDisjointClassesAxiom ax1 = factory.getOWLDisjointClassesAxiom(dirClass, esqClass);
                        OWLDisjointClassesAxiom ax2 = factory.getOWLDisjointClassesAxiom(esqClass, dirClass);
                        list.add(ax1);
                        list.add(ax2);
                    }
                    break;
                }
                case Util.ISA: {
                    if (listEsq != null && listDir == null) {
                        List<OWLAxiom> listaa = isa(listEsq, dirClass);
                        list.addAll(listaa);
                    } else if (listDir != null && listEsq == null) {
                        List<OWLAxiom> listaa = isa(listDir, esqClass);
                        list.addAll(listaa);
                    } else if (listEsq != null && listDir != null) {
                        List<OWLAxiom> listaa = isa(listEsq, listDir);
                        list.addAll(listaa);
                    } else {
                        OWLSubClassOfAxiom ax1 = factory.getOWLSubClassOfAxiom(esqClass, dirClass);
                        list.add(ax1);
                    }

                    break;
                }
                case Util.EQUIVALENT: {
                    if (listEsq != null && listDir == null) {
                        List<OWLAxiom> listaa = equivalent(listEsq, dirClass);
                        list.addAll(listaa);
                    } else if (listDir != null && listEsq == null) {
                        List<OWLAxiom> listaa = equivalent(listDir, esqClass);
                        list.addAll(listaa);
                    } else if (listEsq != null && listDir != null) {
                        List<OWLAxiom> listaa = equivalent(listEsq, listDir);
                        list.addAll(listaa);
                    } else {
                        OWLEquivalentClassesAxiom ax1 = factory.getOWLEquivalentClassesAxiom(esqClass, dirClass);
                        list.add(ax1);
                    }
                    break;
                }
                case Util.SOME: {
                    if (listEsq != null && listDir == null) {
                        OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(PROJECT_IRI + term[2]));
                        List<OWLAxiom> listaa = some(listEsq, property);
                        list.addAll(listaa);
                    } else if (listDir != null && listEsq == null) {
                        OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(PROJECT_IRI + term[0]));
                        List<OWLAxiom> listaa = some(listDir, property);
                        list.addAll(listaa);
                    } else if (listEsq != null && listDir != null) {
                        List<OWLAxiom> listaa = some(listEsq, listDir);
                        list.addAll(listaa);
                    } else {
                        OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(PROJECT_IRI + term[0]));
                        OWLClassExpression exp = factory.getOWLObjectSomeValuesFrom(property, dirClass);
                        OWLSubClassOfAxiom axFinal = factory.getOWLSubClassOfAxiom(dirClass, exp);
                        list.add(axFinal);
                    }

                    break;
                }
                case Util.ALL: {
                    if (listEsq != null && listDir == null) {
                        OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(PROJECT_IRI + term[2]));
                        List<OWLAxiom> listaa = all(listEsq, property);
                        list.addAll(listaa);
                    } else if (listDir != null && listEsq == null) {
                        OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(PROJECT_IRI + term[0]));
                        List<OWLAxiom> listaa = all(listDir, property);
                        list.addAll(listaa);
                    } else if (listEsq != null && listDir != null) {
                        List<OWLAxiom> listaa = all(listEsq, listDir);
                        list.addAll(listaa);
                    } else {
                        OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(PROJECT_IRI + term[0]));
                        OWLClassExpression exp = factory.getOWLObjectAllValuesFrom(property, dirClass);
                        OWLSubClassOfAxiom axFinal = factory.getOWLSubClassOfAxiom(dirClass, exp);
                        list.add(axFinal);
                    }

                    break;
                }
                case Util.ONLY: {
                    if (listEsq != null && listDir == null) {
                        OWLIndividual individual = factory.getOWLNamedIndividual(IRI.create(PROJECT_IRI + term[2]));
                        List<OWLAxiom> listaa = only(listEsq, individual);
                        list.addAll(listaa);
                    } else if (listDir != null && listEsq == null) {
                        OWLIndividual individual = factory.getOWLNamedIndividual(IRI.create(PROJECT_IRI + term[0]));
                        List<OWLAxiom> listaa = only(listDir, individual);
                        list.addAll(listaa);
                    } else if (listEsq != null && listDir != null) {
                        List<OWLAxiom> listaa = only(listEsq, listDir);
                        list.addAll(listaa);
                    } else {
                        OWLIndividual individual = factory.getOWLNamedIndividual(IRI.create(PROJECT_IRI + term[0]));
                        OWLClassExpression exp = factory.getOWLObjectOneOf(individual);
                        OWLSubClassOfAxiom axFinal = factory.getOWLSubClassOfAxiom(dirClass, exp);
                        list.add(axFinal);
                    }

                    break;
                }
                /**
                 * TODO fazer esses casos abaixo;
                 */
//        cmd = substituir3(cmd, Util.SOME);
//        cmd = substituir3(cmd, Util.ALL);
//        cmd = substituir3(cmd, Util.ONLY);
                default: {
                    throw new ConversorException("Erro ao procurar por operação a ser realizada.");
                }
            }
            java.util.logging.Logger.getLogger(Conversor.class.getName()).log(Level.INFO, "OK!" + token.string());

            return new HashSet<OWLAxiom>(list);

        } else {
            throw new ConversorException("Operação não permitida: " + op);
        }
//        java.util.logging.Logger.getLogger(Conversor.class.getName()).log(Level.INFO, "NOT OK!!" + token.string());

//        return null;
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

    private List<OWLAxiom> intersecao(Set<OWLAxiom> lista, OWLClass classe) {
        List<OWLAxiom> list = new ArrayList<OWLAxiom>();

        Iterator<OWLAxiom> leftIt = lista.iterator();

        Set<OWLClassExpression> subs = new HashSet<>();
//        Set<OWLClassExpression> equiv = new HashSet<>();
        while (leftIt.hasNext()) {
            OWLAxiom objOutter = leftIt.next();
            if (objOutter.getAxiomType() == AxiomType.SUBCLASS_OF) {
                OWLClassExpression cc = ((OWLSubClassOfAxiom) objOutter).getSubClass();
                subs.add(cc);
            } else if (objOutter.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
                Set<OWLClass> listaEquivalent = ((OWLEquivalentClassesAxiom) objOutter).getNamedClasses();
                Iterator<OWLClass> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs.add(it.next());
                }
            }
        }
        if (!subs.isEmpty()) {
            OWLSubClassOfAxiom ax1 = factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(subs), classe);
            list.add(ax1);
        }
//        if (!equiv.isEmpty()) {
//            OWLSubClassOfAxiom ax2 = factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(equiv), classe);
//            list.add(ax2);
//        }

        return list;
    }

    private List<OWLAxiom> intersecao(Set<OWLAxiom> listEsq, Set<OWLAxiom> listDir) {
        List<OWLAxiom> list = new ArrayList<OWLAxiom>();

        Iterator<OWLAxiom> leftIt = listEsq.iterator();
        Set<OWLClassExpression> subs1 = new HashSet<>();
//        Set<OWLClassExpression> equiv1 = new HashSet<>();
        while (leftIt.hasNext()) {
            OWLAxiom objOutter = leftIt.next();
            if (objOutter.getAxiomType() == AxiomType.SUBCLASS_OF) {
                OWLClassExpression cc = ((OWLSubClassOfAxiom) objOutter).getSubClass();
                subs1.add(cc);
            } else if (objOutter.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
                Set<OWLClass> listaEquivalent = ((OWLEquivalentClassesAxiom) objOutter).getNamedClasses();
                Iterator<OWLClass> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs1.add(it.next());
                }
            }
        }
        Iterator<OWLAxiom> rightIt = listDir.iterator();
        Set<OWLClassExpression> subs2 = new HashSet<>();
//        Set<OWLClassExpression> equiv2 = new HashSet<>();
        while (rightIt.hasNext()) {
            OWLAxiom objOutter = rightIt.next();
            if (objOutter.getAxiomType() == AxiomType.SUBCLASS_OF) {
                OWLClassExpression cc = ((OWLSubClassOfAxiom) objOutter).getSubClass();
                subs2.add(cc);
            } else if (objOutter.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
                Set<OWLClass> listaEquivalent = ((OWLEquivalentClassesAxiom) objOutter).getNamedClasses();
                Iterator<OWLClass> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs2.add(it.next());
                }
            }
        }

        if (!subs1.isEmpty() && !subs2.isEmpty()) {
            OWLSubClassOfAxiom ax1 = factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(subs2), factory.getOWLObjectIntersectionOf(subs1));
            OWLSubClassOfAxiom ax2 = factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(subs1), factory.getOWLObjectIntersectionOf(subs2));
            list.add(ax1);
            list.add(ax2);
        }
        return list;
    }

    private List<OWLAxiom> uniao(Set<OWLAxiom> lista, OWLClass classe) {
        List<OWLAxiom> list = new ArrayList<OWLAxiom>();

        Iterator<OWLAxiom> leftIt = lista.iterator();

        Set<OWLClassExpression> subs = new HashSet<>();
//        Set<OWLClassExpression> equiv = new HashSet<>();
        while (leftIt.hasNext()) {
            OWLAxiom objOutter = leftIt.next();
            if (objOutter.getAxiomType() == AxiomType.SUBCLASS_OF) {
                OWLClassExpression cc = ((OWLSubClassOfAxiom) objOutter).getSubClass();
                subs.add(cc);
            } else if (objOutter.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
                Set<OWLClass> listaEquivalent = ((OWLEquivalentClassesAxiom) objOutter).getNamedClasses();
                Iterator<OWLClass> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs.add(it.next());
                }
            }
        }
        if (!subs.isEmpty()) {
            OWLSubClassOfAxiom ax1 = factory.getOWLSubClassOfAxiom(factory.getOWLObjectUnionOf(subs), classe);
            list.add(ax1);
        }
//        if (!equiv.isEmpty()) {
//            OWLSubClassOfAxiom ax2 = factory.getOWLSubClassOfAxiom(factory.getOWLObjectUnionOf(equiv), classe);
//            list.add(ax2);
//        }

        return list;
    }

    private List<OWLAxiom> uniao(Set<OWLAxiom> listEsq, Set<OWLAxiom> listDir) {
        List<OWLAxiom> list = new ArrayList<OWLAxiom>();

        Iterator<OWLAxiom> leftIt = listEsq.iterator();
        Set<OWLClassExpression> subs1 = new HashSet<>();
//        Set<OWLClassExpression> equiv1 = new HashSet<>();
        while (leftIt.hasNext()) {
            OWLAxiom objOutter = leftIt.next();
            if (objOutter.getAxiomType() == AxiomType.SUBCLASS_OF) {
                OWLClassExpression cc = ((OWLSubClassOfAxiom) objOutter).getSubClass();
                subs1.add(cc);
            } else if (objOutter.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
                Set<OWLClass> listaEquivalent = ((OWLEquivalentClassesAxiom) objOutter).getNamedClasses();
                Iterator<OWLClass> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs1.add(it.next());
                }
            }
        }
        Iterator<OWLAxiom> rightIt = listDir.iterator();
        Set<OWLClassExpression> subs2 = new HashSet<>();
//        Set<OWLClassExpression> equiv2 = new HashSet<>();
        while (rightIt.hasNext()) {
            OWLAxiom objOutter = rightIt.next();
            if (objOutter.getAxiomType() == AxiomType.SUBCLASS_OF) {
                OWLClassExpression cc = ((OWLSubClassOfAxiom) objOutter).getSubClass();
                subs2.add(cc);
            } else if (objOutter.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
                Set<OWLClass> listaEquivalent = ((OWLEquivalentClassesAxiom) objOutter).getNamedClasses();
                Iterator<OWLClass> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs2.add(it.next());
                }
            }
        }

        if (!subs1.isEmpty() && !subs2.isEmpty()) {
            OWLSubClassOfAxiom ax1 = factory.getOWLSubClassOfAxiom(factory.getOWLObjectUnionOf(subs2), factory.getOWLObjectUnionOf(subs1));
//            OWLSubClassOfAxiom ax2 = factory.getOWLSubClassOfAxiom(factory.getOWLObjectUnionOf(subs1), factory.getOWLObjectUnionOf(subs2));
            list.add(ax1);
//            list.add(ax2);
        }

        return list;
    }

    private List<OWLAxiom> isa(Set<OWLAxiom> lista, OWLClass classe) {
        List<OWLAxiom> list = new ArrayList<OWLAxiom>();

        Iterator<OWLAxiom> leftIt = lista.iterator();

        while (leftIt.hasNext()) {
            OWLAxiom objOutter = leftIt.next();
            if (objOutter.getAxiomType() == AxiomType.SUBCLASS_OF) {
                OWLClassExpression cc = ((OWLSubClassOfAxiom) objOutter).getSubClass();
                OWLSubClassOfAxiom ax1 = factory.getOWLSubClassOfAxiom(cc, classe);
                list.add(ax1);
            } else if (objOutter.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
                Set<OWLClass> listaEquivalent = ((OWLEquivalentClassesAxiom) objOutter).getNamedClasses();
                Iterator<OWLClass> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    OWLSubClassOfAxiom ax2 = factory.getOWLSubClassOfAxiom(it.next(), classe);
                    list.add(ax2);
                }
            }
        }

        return list;
    }

    private List<OWLAxiom> isa(Set<OWLAxiom> listEsq, Set<OWLAxiom> listDir) {
        List<OWLAxiom> list = new ArrayList<OWLAxiom>();

        Iterator<OWLAxiom> leftIt = listEsq.iterator();
        Set<OWLClassExpression> subs1 = new HashSet<>();
//        Set<OWLClassExpression> equiv1 = new HashSet<>();
        while (leftIt.hasNext()) {
            OWLAxiom objOutter = leftIt.next();
            if (objOutter.getAxiomType() == AxiomType.SUBCLASS_OF) {
                OWLClassExpression cc = ((OWLSubClassOfAxiom) objOutter).getSubClass();
                subs1.add(cc);
            } else if (objOutter.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
                Set<OWLClass> listaEquivalent = ((OWLEquivalentClassesAxiom) objOutter).getNamedClasses();
                Iterator<OWLClass> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs1.add(it.next());
                }
            }
        }
        Iterator<OWLAxiom> rightIt = listDir.iterator();
        Set<OWLClassExpression> subs2 = new HashSet<>();
//        Set<OWLClassExpression> equiv2 = new HashSet<>();
        while (rightIt.hasNext()) {
            OWLAxiom objOutter = rightIt.next();
            if (objOutter.getAxiomType() == AxiomType.SUBCLASS_OF) {
                OWLClassExpression cc = ((OWLSubClassOfAxiom) objOutter).getSubClass();
                subs2.add(cc);
            } else if (objOutter.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
                Set<OWLClass> listaEquivalent = ((OWLEquivalentClassesAxiom) objOutter).getNamedClasses();
                Iterator<OWLClass> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs2.add(it.next());
                }
            }
        }

        if (!subs1.isEmpty() && !subs2.isEmpty()) {
            OWLSubClassOfAxiom ax1 = factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(subs2), factory.getOWLObjectIntersectionOf(subs1));
            OWLSubClassOfAxiom ax2 = factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(subs1), factory.getOWLObjectIntersectionOf(subs2));
            list.add(ax1);
            list.add(ax2);
        }

        return list;
    }

    private List<OWLAxiom> some(Set<OWLAxiom> lista, OWLObjectProperty property) {
        List<OWLAxiom> list = new ArrayList<OWLAxiom>();
        Iterator<OWLAxiom> leftIt = lista.iterator();

        Set<OWLClassExpression> subs = new HashSet<>();
        while (leftIt.hasNext()) {
            OWLAxiom objOutter = leftIt.next();
            if (objOutter.getAxiomType() == AxiomType.SUBCLASS_OF) {
                OWLClassExpression cc = ((OWLSubClassOfAxiom) objOutter).getSubClass();
                subs.add(cc);
            } else if (objOutter.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
                Set<OWLClass> listaEquivalent = ((OWLEquivalentClassesAxiom) objOutter).getNamedClasses();
                Iterator<OWLClass> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs.add(it.next());
                }
            }
        }
        if (!subs.isEmpty()) {
            OWLObjectPropertyDomainAxiom ax1 = factory.getOWLObjectPropertyDomainAxiom(property, factory.getOWLObjectIntersectionOf(subs));
//            OWLObjectPropertyRangeAxiom ax2 = factory.getOWLObjectPropertyRangeAxiom(property, factory.getOWLThing());
            AddAxiom addAx = new AddAxiom(ontology, ax1);
            manager.applyChange(addAx);
//            addAx = new AddAxiom(ontology, ax2);
//            manager.applyChange(addAx);

            OWLClassExpression exp = factory.getOWLObjectSomeValuesFrom(property, factory.getOWLObjectIntersectionOf(subs));
            OWLSubClassOfAxiom axFinal = factory.getOWLSubClassOfAxiom(factory.getOWLThing(), exp);
            list.add(axFinal);
        }

        return list;
    }

    private List<OWLAxiom> all(Set<OWLAxiom> lista, OWLObjectProperty property) {
        List<OWLAxiom> list = new ArrayList<OWLAxiom>();
        Iterator<OWLAxiom> leftIt = lista.iterator();

        Set<OWLClassExpression> subs = new HashSet<>();
        while (leftIt.hasNext()) {
            OWLAxiom objOutter = leftIt.next();
            if (objOutter.getAxiomType() == AxiomType.SUBCLASS_OF) {
                OWLClassExpression cc = ((OWLSubClassOfAxiom) objOutter).getSubClass();
                subs.add(cc);
            } else if (objOutter.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
                Set<OWLClass> listaEquivalent = ((OWLEquivalentClassesAxiom) objOutter).getNamedClasses();
                Iterator<OWLClass> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs.add(it.next());
                }
            }
        }
        if (!subs.isEmpty()) {
            OWLObjectPropertyDomainAxiom ax1 = factory.getOWLObjectPropertyDomainAxiom(property, factory.getOWLObjectIntersectionOf(subs));
//            OWLObjectPropertyRangeAxiom ax2 = factory.getOWLObjectPropertyRangeAxiom(property, factory.getOWLThing());
            AddAxiom addAx = new AddAxiom(ontology, ax1);
            manager.applyChange(addAx);
//            addAx = new AddAxiom(ontology, ax2);
//            manager.applyChange(addAx);

            OWLClassExpression exp = factory.getOWLObjectAllValuesFrom(property, factory.getOWLObjectIntersectionOf(subs));
            OWLSubClassOfAxiom axFinal = factory.getOWLSubClassOfAxiom(factory.getOWLThing(), exp);
            list.add(axFinal);
        }

        return list;
    }

    private List<OWLAxiom> only(Set<OWLAxiom> lista, OWLIndividual individual) {
        List<OWLAxiom> list = new ArrayList<OWLAxiom>();
        Iterator<OWLAxiom> leftIt = lista.iterator();

        Set<OWLClassExpression> subs = new HashSet<>();
        while (leftIt.hasNext()) {
            OWLAxiom objOutter = leftIt.next();
            if (objOutter.getAxiomType() == AxiomType.SUBCLASS_OF) {
                OWLClassExpression cc = ((OWLSubClassOfAxiom) objOutter).getSubClass();
                subs.add(cc);
            } else if (objOutter.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
                Set<OWLClass> listaEquivalent = ((OWLEquivalentClassesAxiom) objOutter).getNamedClasses();
                Iterator<OWLClass> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs.add(it.next());
                }
            }
        }
        if (!subs.isEmpty()) {
            OWLClassExpression exp = factory.getOWLObjectOneOf(individual);
            OWLSubClassOfAxiom axFinal = factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(subs), exp);
            list.add(axFinal);
        }

        return list;
    }

    private List<OWLAxiom> some(Set<OWLAxiom> listEsq, Set<OWLAxiom> listDir) throws ConversorException {
        throw new ConversorException("Property cannot be used like a class.");
    }

    private List<OWLAxiom> all(Set<OWLAxiom> listEsq, Set<OWLAxiom> listDir) throws ConversorException {
        throw new ConversorException("Property cannot be used like a class.");
    }

    private List<OWLAxiom> only(Set<OWLAxiom> listEsq, Set<OWLAxiom> listDir) throws ConversorException {
        throw new ConversorException("Property cannot be used like a class.");
    }

    private List<OWLAxiom> equivalent(Set<OWLAxiom> lista, OWLClass classe) {
        List<OWLAxiom> list = new ArrayList<OWLAxiom>();
        Iterator<OWLAxiom> leftIt = lista.iterator();

        Set<OWLClassExpression> subs = new HashSet<>();
        while (leftIt.hasNext()) {
            OWLAxiom objOutter = leftIt.next();
            if (objOutter.getAxiomType() == AxiomType.SUBCLASS_OF) {
                OWLClassExpression cc = ((OWLSubClassOfAxiom) objOutter).getSubClass();
                subs.add(cc);
            } else if (objOutter.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
                Set<OWLClass> listaEquivalent = ((OWLEquivalentClassesAxiom) objOutter).getNamedClasses();
                Iterator<OWLClass> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs.add(it.next());
                }
            }
        }
        if (!subs.isEmpty()) {
            OWLEquivalentClassesAxiom ax1 = factory.getOWLEquivalentClassesAxiom(factory.getOWLObjectIntersectionOf(subs), classe);
            list.add(ax1);
        }

        return list;
    }

    private List<OWLAxiom> equivalent(Set<OWLAxiom> listEsq, Set<OWLAxiom> listDir) {
        List<OWLAxiom> list = new ArrayList<OWLAxiom>();

        Iterator<OWLAxiom> leftIt = listEsq.iterator();
        Set<OWLClassExpression> subs1 = new HashSet<>();
        while (leftIt.hasNext()) {
            OWLAxiom objOutter = leftIt.next();
            if (objOutter.getAxiomType() == AxiomType.SUBCLASS_OF) {
                OWLClassExpression cc = ((OWLSubClassOfAxiom) objOutter).getSubClass();
                subs1.add(cc);
            } else if (objOutter.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
                Set<OWLClass> listaEquivalent = ((OWLEquivalentClassesAxiom) objOutter).getNamedClasses();
                Iterator<OWLClass> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs1.add(it.next());
                }
            }
        }
        Iterator<OWLAxiom> rightIt = listDir.iterator();
        Set<OWLClassExpression> subs2 = new HashSet<>();
        while (rightIt.hasNext()) {
            OWLAxiom objOutter = rightIt.next();
            if (objOutter.getAxiomType() == AxiomType.SUBCLASS_OF) {
                OWLClassExpression cc = ((OWLSubClassOfAxiom) objOutter).getSubClass();
                subs2.add(cc);
            } else if (objOutter.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
                Set<OWLClass> listaEquivalent = ((OWLEquivalentClassesAxiom) objOutter).getNamedClasses();
                Iterator<OWLClass> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs2.add(it.next());
                }
            }
        }

        if (!subs1.isEmpty() && !subs2.isEmpty()) {
            OWLEquivalentClassesAxiom ax1 = factory.getOWLEquivalentClassesAxiom(factory.getOWLObjectIntersectionOf(subs2), factory.getOWLObjectIntersectionOf(subs1));
//            OWLEquivalentClassesAxiom ax2 = factory.getOWLEquivalentClassesAxiom(factory.getOWLObjectIntersectionOf(subs1), factory.getOWLObjectIntersectionOf(subs2));
            list.add(ax1);
//            list.add(ax2);
        }

        return list;
    }
}
