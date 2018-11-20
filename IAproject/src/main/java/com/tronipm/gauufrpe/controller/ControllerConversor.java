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
package com.tronipm.gauufrpe.controller;

import com.tronipm.gauufrpe.util.Util;
import com.tronipm.gauufrpe.model.TokenProcessamento;
import edu.stanford.nlp.io.StringOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.tronipm.gauufrpe.exception.ConversorException;
import com.tronipm.gauufrpe.view.JFramePrincipal;
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
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 *
 * @author Matt
 */
public class ControllerConversor {

    private static ControllerConversor conversor = null;

    public OWLOntology ontology = null;
    public OWLOntologyManager manager = null;
    public OWLDataFactory factory = null;
    public String PROJECT_IRI = null;

    public ArrayList<TokenProcessamento> tokens = null;

    private JFramePrincipal jFrameMain;

    public static ControllerConversor getInstance() {
        if (conversor == null) {
            conversor = new ControllerConversor();
        }

        return conversor;
    }

    public boolean init(ArrayList<TokenProcessamento> arr, JFramePrincipal jFrameMain) throws ConversorException, OWLOntologyCreationException {
        this.jFrameMain = jFrameMain;
        this.tokens = arr;

        java.util.logging.Logger.getLogger(ControllerConversor.class.getName()).log(Level.INFO, "Size tokens: " + arr.size());
        if (jFrameMain.filename.trim().isEmpty()) {
            this.PROJECT_IRI = jFrameMain.currentDocument + ".com/#";
        } else {
            this.PROJECT_IRI = jFrameMain.filename + ".com/#";
        }
        this.manager = OWLManager.createOWLOntologyManager();
        this.ontology = manager.createOntology(IRI.create(PROJECT_IRI));
        this.factory = manager.getOWLDataFactory();

        for (TokenProcessamento in : arr) {
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
                list = exec(in, false);

                manager.addAxioms(ontology, list);

            } else {
            }
        }
        jFrameMain.coreApp.owlRepository.currentOntology = ontology;
        jFrameMain.coreApp.owlRepository.currentOntologyManager = manager;
        jFrameMain.coreApp.owlRepository.currentDataFactory = factory;
        return true;
    }

    public Set<OWLAxiom> exec(TokenProcessamento token, boolean hasNotExpression) throws ConversorException {//PODE SER RECURSIVO
        java.util.logging.Logger.getLogger(ControllerConversor.class.getName()).log(Level.INFO, token.string());

        String[] term = token.label.replace("(", "").replace(")", "").replaceAll(" +", " ").trim().split(" ");
        int size = term.length;
        String op = null;

        int posicaoNOT = 0;//0 = nenhum, -1 ESQUERDA, 1 DIREITA, 2, DIREITA-ESQUERDA

        if (size != 3) {//existe algum not
//            System.out.println("||||||||||||||||||||||||||| " + String.join(",", term));//DEBUG

            ArrayList<String> arrayNOTs = new ArrayList<String>(Arrays.asList(term));
            if (arrayNOTs.indexOf("not") != -1) {
                if (arrayNOTs.indexOf("not") == arrayNOTs.lastIndexOf("not")) {
                    if (arrayNOTs.indexOf("not") == 0) {
                        posicaoNOT = -1;

                        op = term[2].toLowerCase();
                        term[0] = term[1];
                        term[2] = term[3];
                    } else {
                        posicaoNOT = 1;

                        op = term[1].toLowerCase();
                        term[2] = term[3];
                    }
                } else {
                    posicaoNOT = 2;

                    op = term[2].toLowerCase();
                    term[0] = term[1];
                    term[3] = term[4];
                }
            } else {
                op = term[1].toLowerCase();
            }
        } else {
            op = term[1].toLowerCase();
        }

        Set<OWLAxiom> listEsq = null, listDir = null;

        if (size == 2) {//NOR CASE
            for (TokenProcessamento in : tokens) {
                if (in.id.equals(term[1])) {
                    Set<OWLAxiom> list = exec(in, true);
                    return list;
                }
            }
        } else {
            for (TokenProcessamento in : tokens) {
                if (in.id.equals(term[0])) {
                    listEsq = exec(in, posicaoNOT == -1 || posicaoNOT == 2);
                }
                if (in.id.equals(term[2])) {
                    listDir = exec(in, posicaoNOT == 1 || posicaoNOT == 2);
                }
            }
        }
//        System.out.println("||||||||||||||||||||||||||| POSICAO: " + posicaoNOT);//DEBUG

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
            OWLClassExpression esq = null;
            if (posicaoNOT == -1 || posicaoNOT == 2) {
                esq = esqClass.getComplementNNF();
            } else {
                esq = esqClass;
            }
            OWLClass dirClass = factory.getOWLClass(IRI.create(PROJECT_IRI + term[2]));
            OWLClassExpression dir = null;
            if (posicaoNOT == 1 || posicaoNOT == 2) {
                dir = dirClass.getComplementNNF();
            } else {
                dir = dirClass;
            }

            switch (op) {
                case Util.THAT:
                case Util.AND: {
                    if (listEsq != null && listDir == null) {
                        List<OWLAxiom> listaa = intersecao(listEsq, dir, hasNotExpression);
                        list.addAll(listaa);
                    } else if (listDir != null && listEsq == null) {
                        List<OWLAxiom> listaa = intersecao(listDir, esq, hasNotExpression);
                        list.addAll(listaa);
                    } else if (listEsq != null && listDir != null) {
                        List<OWLAxiom> listaa = intersecao(listEsq, listDir, hasNotExpression);
                        list.addAll(listaa);
                    } else {
                        OWLSubClassOfAxiom ax1 = factory.getOWLSubClassOfAxiom(dir, factory.getOWLObjectIntersectionOf(esq));
                        OWLSubClassOfAxiom ax2 = factory.getOWLSubClassOfAxiom(esq, factory.getOWLObjectIntersectionOf(dir));
                        if (hasNotExpression) {//NEGAÇÃO DA NEGAÇÃO
                            list.add(ax1.getNNF());
                            list.add(ax2.getNNF());
                        } else {
                            list.add(ax1);
                            list.add(ax2);
                        }
                    }
                    break;
                }
                case Util.OR: {
                    if (listEsq != null && listDir == null) {
                        List<OWLAxiom> listaa = uniao(listEsq, dir, hasNotExpression);
                        list.addAll(listaa);
                    } else if (listDir != null && listEsq == null) {
                        List<OWLAxiom> listaa = uniao(listDir, esq, hasNotExpression);
                        list.addAll(listaa);
                    } else if (listEsq != null && listDir != null) {
                        List<OWLAxiom> listaa = uniao(listEsq, listDir, hasNotExpression);
                        list.addAll(listaa);
                    } else {
                        OWLObjectUnionOf ax = factory.getOWLObjectUnionOf(dir, esq);
                        OWLObjectUnionOf ay = factory.getOWLObjectUnionOf(esq, dir);

                        OWLSubClassOfAxiom ax1 = factory.getOWLSubClassOfAxiom(ax, ay);
                        OWLSubClassOfAxiom ax2 = factory.getOWLSubClassOfAxiom(ay, ax);

                        if (hasNotExpression) {//NEGAÇÃO DA NEGAÇÃO
                            list.add(ax1.getNNF());
                            list.add(ax2.getNNF());
                        } else {
                            list.add(ax1);
                            list.add(ax2);
                        }
                    }
                    break;
                }
                case Util.ISA: {
                    if (listEsq != null && listDir == null) {
                        List<OWLAxiom> listaa = isa(listEsq, dir, hasNotExpression);
                        list.addAll(listaa);
                    } else if (listDir != null && listEsq == null) {
                        List<OWLAxiom> listaa = isa(listDir, esq, hasNotExpression);
                        list.addAll(listaa);
                    } else if (listEsq != null && listDir != null) {
                        List<OWLAxiom> listaa = isa(listEsq, listDir, hasNotExpression);
                        list.addAll(listaa);
                    } else {
                        OWLSubClassOfAxiom ax1 = factory.getOWLSubClassOfAxiom(esq, dir);
                        if (hasNotExpression) {//NEGAÇÃO DA NEGAÇÃO
                            list.add(ax1.getNNF());
                        } else {
                            list.add(ax1);
                        }
                    }

                    break;
                }
                case Util.EQUIVALENT: {
                    if (listEsq != null && listDir == null) {
                        List<OWLAxiom> listaa = equivalent(listEsq, dir, hasNotExpression);
                        list.addAll(listaa);
                    } else if (listDir != null && listEsq == null) {
                        List<OWLAxiom> listaa = equivalent(listDir, esq, hasNotExpression);
                        list.addAll(listaa);
                    } else if (listEsq != null && listDir != null) {
                        List<OWLAxiom> listaa = equivalent(listEsq, listDir, hasNotExpression);
                        list.addAll(listaa);
                    } else {
                        OWLEquivalentClassesAxiom ax1 = factory.getOWLEquivalentClassesAxiom(esq, dir);
                        if (hasNotExpression) {//NEGAÇÃO DA NEGAÇÃO
                            list.add(ax1.getNNF());
                        } else {
                            list.add(ax1);
                        }
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
                        OWLClassExpression exp = factory.getOWLObjectSomeValuesFrom(property, dir);
                        OWLSubClassOfAxiom axFinal = factory.getOWLSubClassOfAxiom(dir, exp);
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
                        OWLClassExpression exp = factory.getOWLObjectAllValuesFrom(property, dir);
                        OWLSubClassOfAxiom axFinal = factory.getOWLSubClassOfAxiom(dir, exp);
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
                        OWLSubClassOfAxiom axFinal = factory.getOWLSubClassOfAxiom(dir, exp);
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
            java.util.logging.Logger.getLogger(ControllerConversor.class.getName()).log(Level.INFO, "OK!" + token.string());

            return new HashSet<OWLAxiom>(list);

        } else {
            System.out.println("||||||||||||||||||||||||||| " + String.join(",", term));//DEBUG
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
            Logger.getLogger(ControllerConversor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<OWLAxiom> intersecao(Set<OWLAxiom> lista, OWLClassExpression classe, boolean hasNotExpression) {
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
            } else if (objOutter.getAxiomType() == AxiomType.DISJOINT_CLASSES) {
                Set<OWLClassExpression> listaEquivalent = ((OWLDisjointClassesAxiom) objOutter).getClassExpressions();
                Iterator<OWLClassExpression> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs.add(it.next());
                }
            }
        }
        if (!subs.isEmpty()) {
            OWLSubClassOfAxiom ax1 = factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(subs), classe);
            if (hasNotExpression) {//NEGAÇÃO DA NEGAÇÃO
                list.add(ax1.getNNF());
            } else {
                list.add(ax1);
            }
        }
//        if (!equiv.isEmpty()) {
//            OWLSubClassOfAxiom ax2 = factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(equiv), classe);
//            list.add(ax2);
//        }

        return list;
    }

    private List<OWLAxiom> intersecao(Set<OWLAxiom> listEsq, Set<OWLAxiom> listDir, boolean hasNotExpression) {
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
            } else if (objOutter.getAxiomType() == AxiomType.DISJOINT_CLASSES) {
                Set<OWLClassExpression> listaEquivalent = ((OWLDisjointClassesAxiom) objOutter).getClassExpressions();
                Iterator<OWLClassExpression> it = listaEquivalent.iterator();
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
            } else if (objOutter.getAxiomType() == AxiomType.DISJOINT_CLASSES) {
                Set<OWLClassExpression> listaEquivalent = ((OWLDisjointClassesAxiom) objOutter).getClassExpressions();
                Iterator<OWLClassExpression> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs2.add(it.next());
                }
            }
        }

        if (!subs1.isEmpty() && !subs2.isEmpty()) {
            OWLSubClassOfAxiom ax1 = factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(subs2), factory.getOWLObjectIntersectionOf(subs1));
            OWLSubClassOfAxiom ax2 = factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(subs1), factory.getOWLObjectIntersectionOf(subs2));
            if (hasNotExpression) {//NEGAÇÃO DA NEGAÇÃO
                list.add(ax1.getNNF());
                list.add(ax2.getNNF());
            } else {
                list.add(ax1);
                list.add(ax2);
            }
        }
        return list;
    }

    private List<OWLAxiom> uniao(Set<OWLAxiom> lista, OWLClassExpression classe, boolean hasNotExpression) {
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
            } else if (objOutter.getAxiomType() == AxiomType.DISJOINT_CLASSES) {
                Set<OWLClassExpression> listaEquivalent = ((OWLDisjointClassesAxiom) objOutter).getClassExpressions();
                Iterator<OWLClassExpression> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs.add(it.next());
                }
            }
        }
        if (!subs.isEmpty()) {
            OWLObjectUnionOf ax = factory.getOWLObjectUnionOf(factory.getOWLObjectUnionOf(subs), classe);
            OWLObjectUnionOf ay = factory.getOWLObjectUnionOf(classe, factory.getOWLObjectUnionOf(subs));

            OWLSubClassOfAxiom ax1 = factory.getOWLSubClassOfAxiom(ax, ay);
            OWLSubClassOfAxiom ax2 = factory.getOWLSubClassOfAxiom(ay, ax);

            if (hasNotExpression) {//NEGAÇÃO DA NEGAÇÃO
                list.add(ax1.getNNF());
                list.add(ax2.getNNF());
            } else {
                list.add(ax1);
                list.add(ax2);
            }
        }
//        if (!equiv.isEmpty()) {
//            OWLSubClassOfAxiom ax2 = factory.getOWLSubClassOfAxiom(factory.getOWLObjectUnionOf(equiv), classe);
//            list.add(ax2);
//        }

        return list;
    }

    private List<OWLAxiom> uniao(Set<OWLAxiom> listEsq, Set<OWLAxiom> listDir, boolean hasNotExpression) {
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
            } else if (objOutter.getAxiomType() == AxiomType.DISJOINT_CLASSES) {
                Set<OWLClassExpression> listaEquivalent = ((OWLDisjointClassesAxiom) objOutter).getClassExpressions();
                Iterator<OWLClassExpression> it = listaEquivalent.iterator();
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
            } else if (objOutter.getAxiomType() == AxiomType.DISJOINT_CLASSES) {
                Set<OWLClassExpression> listaEquivalent = ((OWLDisjointClassesAxiom) objOutter).getClassExpressions();
                Iterator<OWLClassExpression> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs2.add(it.next());
                }
            }
        }

        if (!subs1.isEmpty() && !subs2.isEmpty()) {
            OWLObjectUnionOf ax = factory.getOWLObjectUnionOf(factory.getOWLObjectUnionOf(subs1), factory.getOWLObjectUnionOf(subs2));
            OWLObjectUnionOf ay = factory.getOWLObjectUnionOf(factory.getOWLObjectUnionOf(subs2), factory.getOWLObjectUnionOf(subs1));

            OWLSubClassOfAxiom ax1 = factory.getOWLSubClassOfAxiom(ax, ay);
            OWLSubClassOfAxiom ax2 = factory.getOWLSubClassOfAxiom(ay, ax);

            if (hasNotExpression) {//NEGAÇÃO DA NEGAÇÃO
                list.add(ax1.getNNF());
                list.add(ax2.getNNF());
            } else {
                list.add(ax1);
                list.add(ax2);
            }
        }

        return list;
    }

    private List<OWLAxiom> isa(Set<OWLAxiom> lista, OWLClassExpression classe, boolean hasNotExpression) {
        List<OWLAxiom> list = new ArrayList<OWLAxiom>();

        Iterator<OWLAxiom> leftIt = lista.iterator();

        while (leftIt.hasNext()) {
            OWLAxiom objOutter = leftIt.next();
            if (objOutter.getAxiomType() == AxiomType.SUBCLASS_OF) {
                OWLClassExpression cc = ((OWLSubClassOfAxiom) objOutter).getSubClass();
                OWLSubClassOfAxiom ax1 = factory.getOWLSubClassOfAxiom(cc, classe);
                if (hasNotExpression) {//NEGAÇÃO DA NEGAÇÃO
                    list.add(ax1.getNNF());
                } else {
                    list.add(ax1);
                }
            } else if (objOutter.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
                Set<OWLClass> listaEquivalent = ((OWLEquivalentClassesAxiom) objOutter).getNamedClasses();
                Iterator<OWLClass> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    OWLSubClassOfAxiom ax2 = factory.getOWLSubClassOfAxiom(it.next(), classe);
                    if (hasNotExpression) {//NEGAÇÃO DA NEGAÇÃO
                        list.add(ax2.getNNF());
                    } else {
                        list.add(ax2);
                    }
                }
            } else if (objOutter.getAxiomType() == AxiomType.DISJOINT_CLASSES) {
                Set<OWLClassExpression> listaEquivalent = ((OWLDisjointClassesAxiom) objOutter).getClassExpressions();
                Iterator<OWLClassExpression> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    OWLSubClassOfAxiom ax2 = factory.getOWLSubClassOfAxiom(it.next(), classe);
                    if (hasNotExpression) {//NEGAÇÃO DA NEGAÇÃO
                        list.add(ax2.getNNF());
                    } else {
                        list.add(ax2);
                    }
                }
            }
        }

        return list;
    }

    private List<OWLAxiom> isa(Set<OWLAxiom> listEsq, Set<OWLAxiom> listDir, boolean hasNotExpression) {
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
            } else if (objOutter.getAxiomType() == AxiomType.DISJOINT_CLASSES) {
                Set<OWLClassExpression> listaEquivalent = ((OWLDisjointClassesAxiom) objOutter).getClassExpressions();
                Iterator<OWLClassExpression> it = listaEquivalent.iterator();
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
            } else if (objOutter.getAxiomType() == AxiomType.DISJOINT_CLASSES) {
                Set<OWLClassExpression> listaEquivalent = ((OWLDisjointClassesAxiom) objOutter).getClassExpressions();
                Iterator<OWLClassExpression> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs2.add(it.next());
                }
            }
        }

        if (!subs1.isEmpty() && !subs2.isEmpty()) {
            OWLSubClassOfAxiom ax1 = factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(subs2), factory.getOWLObjectIntersectionOf(subs1));
            OWLSubClassOfAxiom ax2 = factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(subs1), factory.getOWLObjectIntersectionOf(subs2));
            if (hasNotExpression) {//NEGAÇÃO DA NEGAÇÃO
                list.add(ax1.getNNF());
                list.add(ax2.getNNF());
            } else {
                list.add(ax1);
                list.add(ax2);
            }
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
            } else if (objOutter.getAxiomType() == AxiomType.DISJOINT_CLASSES) {
                Set<OWLClassExpression> listaEquivalent = ((OWLDisjointClassesAxiom) objOutter).getClassExpressions();
                Iterator<OWLClassExpression> it = listaEquivalent.iterator();
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
            } else if (objOutter.getAxiomType() == AxiomType.DISJOINT_CLASSES) {
                Set<OWLClassExpression> listaEquivalent = ((OWLDisjointClassesAxiom) objOutter).getClassExpressions();
                Iterator<OWLClassExpression> it = listaEquivalent.iterator();
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
            } else if (objOutter.getAxiomType() == AxiomType.DISJOINT_CLASSES) {
                Set<OWLClassExpression> listaEquivalent = ((OWLDisjointClassesAxiom) objOutter).getClassExpressions();
                Iterator<OWLClassExpression> it = listaEquivalent.iterator();
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

    private List<OWLAxiom> equivalent(Set<OWLAxiom> lista, OWLClassExpression classe, boolean hasNotExpression) {
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
            } else if (objOutter.getAxiomType() == AxiomType.DISJOINT_CLASSES) {
                Set<OWLClassExpression> listaEquivalent = ((OWLDisjointClassesAxiom) objOutter).getClassExpressions();
                Iterator<OWLClassExpression> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs.add(it.next());
                }
            }
        }
        if (!subs.isEmpty()) {
            OWLEquivalentClassesAxiom ax1 = factory.getOWLEquivalentClassesAxiom(factory.getOWLObjectIntersectionOf(subs), classe);
            if (hasNotExpression) {//NEGAÇÃO DA NEGAÇÃO
                list.add(ax1.getNNF());
            } else {
                list.add(ax1);
            }
        }

        return list;
    }

    private List<OWLAxiom> equivalent(Set<OWLAxiom> listEsq, Set<OWLAxiom> listDir, boolean hasNotExpression) {
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
            } else if (objOutter.getAxiomType() == AxiomType.DISJOINT_CLASSES) {
                Set<OWLClassExpression> listaEquivalent = ((OWLDisjointClassesAxiom) objOutter).getClassExpressions();
                Iterator<OWLClassExpression> it = listaEquivalent.iterator();
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
            } else if (objOutter.getAxiomType() == AxiomType.DISJOINT_CLASSES) {
                Set<OWLClassExpression> listaEquivalent = ((OWLDisjointClassesAxiom) objOutter).getClassExpressions();
                Iterator<OWLClassExpression> it = listaEquivalent.iterator();
                while (it.hasNext()) {
                    subs2.add(it.next());
                }
            }
        }

        if (!subs1.isEmpty() && !subs2.isEmpty()) {
            OWLEquivalentClassesAxiom ax1 = factory.getOWLEquivalentClassesAxiom(factory.getOWLObjectIntersectionOf(subs2), factory.getOWLObjectIntersectionOf(subs1));
            if (hasNotExpression) {//NEGAÇÃO DA NEGAÇÃO
                list.add(ax1.getNNF());
            } else {
                list.add(ax1);
            }
//            OWLEquivalentClassesAxiom ax2 = factory.getOWLEquivalentClassesAxiom(factory.getOWLObjectIntersectionOf(subs1), factory.getOWLObjectIntersectionOf(subs2));
//            list.add(ax2);
        }

        return list;
    }
}
