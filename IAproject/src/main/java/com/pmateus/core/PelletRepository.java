/*
 * Copyright 2016 Paulo Mateus [UFRPE-UAG] <paulomatew@gmail.com>
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
package com.pmateus.core;

//import openllet.jena.*;
//import openllet.jena.PelletReasonerFactory;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import edu.stanford.nlp.io.StringOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDisjointClassesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class PelletRepository {

    private CoreApplication coreApp;
    private OWLOntology ontology;
    public OWLOntology ontology_INFERRED_;
    public String ontology_INFERRED_STRING;
    private PelletReasoner reasoner;

    private ArrayList<OWLClassAxiom> axiomasProblematicos;
    public String ontology_INFERRED_LOGICA_DE_DESCRICAO;
    public String inconsistence_STRING;

    public PelletRepository(CoreApplication aThis) {
        this.coreApp = aThis;

        axiomasProblematicos = new ArrayList<OWLClassAxiom>();

    }

    public OWLOntologyManager getOntologyManager() {
        return coreApp.owlRepository.currentOntologyManager;
    }

    public OWLOntology getOntology() {
        return coreApp.owlRepository.currentOntology;
    }

    public void atualizandoReferencia() {
        this.ontology = coreApp.owlRepository.currentOntology;
        this.reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
//        this.reasoner = PelletReasonerFactory.theInstance().createReasoner(ontology);
        //reasoner.flush();//getKB().realize();

        reasoner.precomputeInferences();

        axiomasProblematicos = new ArrayList<OWLClassAxiom>();
    }

    public void getReasoningScheme(boolean isStandard) {
        atualizandoReferencia();

        String fullRetorno = "";

        fullRetorno = "Can perform reasoning? " + reasoner.isConsistent() + ".";
        fullRetorno += getUnsatisfiableClasses(reasoner);

        try {
            getInferring(isStandard);
        } catch (OWLOntologyCreationException ex) {
            Logger.getLogger(PelletRepository.class.getName()).log(Level.SEVERE, null, ex);
        }

        getDeducaEmLogicaDeDescricao();

        //reasoner.pre
        inconsistence_STRING = fullRetorno;
    }

    public String getUnsatisfiableClasses(PelletReasoner reasoner) {
        String retorno = "";
        Node<OWLClass> bottomNode = reasoner.getUnsatisfiableClasses();
        Set<OWLClass> unsatisfiable = bottomNode.getEntitiesMinusBottom();
        if (!unsatisfiable.isEmpty()) {
            retorno += "\n\nThe following classes are inconsistent:";

            Set<OWLClassAxiom> dummySet = new HashSet<OWLClassAxiom>();

            for (OWLClass cls : unsatisfiable) {
                retorno += "\n\t" + cls;

                Set<OWLClassAxiom> axiomas = ontology.getAxioms(cls);
                for (Iterator<OWLClassAxiom> axioma = axiomas.iterator(); axioma.hasNext();) {
                    OWLClassAxiom classAxiom = axioma.next();
                    //retorno += "\n" + classAxiom;//Adicionar todos os axiomas da classe

                    //Se nÃ£o inserir, Ã© pq jÃ¡ tem esse obj. EntÃ£o faÃ§o processamento nele
                    if (!dummySet.add(classAxiom)) {

                        axiomasProblematicos.add(classAxiom);

                    }
                }
            }

            retorno += "\n\nAXIOMS:";
            int i = 1;
            for (OWLClassAxiom ax : axiomasProblematicos) {
                retorno += "\n\t ID: " + (i++) + " || " + ax;
            }
        } else {
            retorno += "\n\nThere are no inconsistence classes.";
        }

        return retorno;
    }

    public void getInferring(boolean isStandard) throws OWLOntologyCreationException {
        List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<>();
        gens.add(new InferredClassAssertionAxiomGenerator());
        //gens.add(new InferredDataPropertyCharacteristicAxiomGenerator());
        gens.add(new InferredDisjointClassesAxiomGenerator());
        gens.add(new InferredEquivalentClassAxiomGenerator());
        gens.add(new InferredEquivalentClassAxiomGenerator());
        //gens.add(new InferredEquivalentDataPropertiesAxiomGenerator());
        //gens.add(new InferredEquivalentObjectPropertyAxiomGenerator());
        //gens.add(new InferredInverseObjectPropertiesAxiomGenerator());
        //gens.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
        //gens.add(new InferredPropertyAssertionGenerator());
        gens.add(new InferredSubClassAxiomGenerator());
        //gens.add(new InferredSubObjectPropertyAxiomGenerator());
        /**
         * Para tirar os parâmetros, só remover o "gens" de
         * InferredOntologyGenerator
         */
        ontology_INFERRED_ = getOntologyManager().createOntology();
        //InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner);
        InferredOntologyGenerator iog = null;
        if (isStandard) {
            iog = new InferredOntologyGenerator(reasoner);
        } else {
            iog = new InferredOntologyGenerator(reasoner, gens);
        }
        iog.fillOntology(getOntologyManager().getOWLDataFactory(), ontology_INFERRED_);

        //Gerando em outputStream ontologia
        StringOutputStream auxiliar = new StringOutputStream();
        try {
            getOntologyManager().saveOntology(ontology_INFERRED_, auxiliar);
        } catch (OWLOntologyStorageException ex) {
            Logger.getLogger(PelletRepository.class.getName()).log(Level.SEVERE, null, ex);
        }

        ontology_INFERRED_STRING = auxiliar.toString();
    }

    public void getDeducaEmLogicaDeDescricao() {
        String retorno = "\n\nDEDUCTION:\n";
        OWLOntology localOntology = ontology_INFERRED_;

        ManchesterOWLSyntaxOWLObjectRendererImpl rendering = new ManchesterOWLSyntaxOWLObjectRendererImpl();

        Set<OWLClassAxiom> setNonRepeat = new HashSet<OWLClassAxiom>();
        Set<OWLClassAxiom> classesAlreadyDefinedByOntology = new HashSet<OWLClassAxiom>();
        Set<OWLClass> classes = ontology_INFERRED_.getClassesInSignature();
        for (OWLClass c1 : classes) {
            /**
             * *****
             */
            //System.out.println("##################### " + c1 + " #####################");
            /*NodeSet<OWLClass> parent = reasoner.getSuperClasses(c1, true);
             Iterator<Node<OWLClass>> pai = parent.iterator();
             while (pai.hasNext()) {
             for (OWLClass aa : pai.next()) {
             //System.out.println("PARENT: " + aa);
             }
             }*/

            /**
             * **********
             */
            Set<OWLClassAxiom> axiomasDaClasse = coreApp.owlRepository.currentOntology.getAxioms(c1);

            //Pulo verificação da classe THING.
            if (c1 == coreApp.owlRepository.currentDataFactory.getOWLThing()
                    || c1 == coreApp.owlRepository.currentDataFactory.getOWLNothing()) {
                continue;
            }

            retorno += "\nIF:\n[";

            int i = 0;
            for (OWLClassAxiom a1 : axiomasDaClasse) {
                classesAlreadyDefinedByOntology.add(a1);

                retorno += "(" + rendering.render(a1) + ")";
                i++;
                if (i < axiomasDaClasse.size()) {
                    retorno += " AND\n";
                }
            }
            //ou seja, não houve parents
            if (i == 0) {
                retorno += "NoAxiom: " + c1;
            }
            retorno += "]\nTHEN:";
            /**
             * ******
             */
            Set<OWLEquivalentClassesAxiom> axioms = localOntology.getEquivalentClassesAxioms(c1);
            for (OWLEquivalentClassesAxiom c1e : axioms) {
                //if (setNonRepeat.add(c1e)) {
                if (classesAlreadyDefinedByOntology.add((OWLClassAxiom) c1e)) {
                    retorno += "\n\t" + rendering.render(c1e);
                    classesAlreadyDefinedByOntology.remove(c1e);
                }
                //}
            }
            Set<OWLDisjointClassesAxiom> axioms1 = localOntology.getDisjointClassesAxioms(c1);
            for (OWLDisjointClassesAxiom c1e : axioms1) {
                // if (setNonRepeat.add(c1e)) {
                if (classesAlreadyDefinedByOntology.add((OWLClassAxiom) c1e)) {
                    retorno += "\n\t" + rendering.render(c1e);
                    classesAlreadyDefinedByOntology.remove(c1e);
                }
                //}
            }
            Set<OWLSubClassOfAxiom> axioms2 = localOntology.getSubClassAxiomsForSubClass(c1);
            for (OWLSubClassOfAxiom c1e : axioms2) {
                //if (setNonRepeat.add(c1e)) {
                if (classesAlreadyDefinedByOntology.add((OWLClassAxiom) c1e)) {
                    retorno += "\n\t" + rendering.render(c1e);
                    classesAlreadyDefinedByOntology.remove(c1e);
                }
                //}
            }
        }

        ontology_INFERRED_LOGICA_DE_DESCRICAO = retorno;
    }

    public boolean canUseIndexDeleteAxioms(int id) {
        if (axiomasProblematicos.size() == 0 || id - 1 < 0 || id - 1 > axiomasProblematicos.size() - 1) {
            return false;
        }
        return true;
    }

    public boolean deleteAllAxiomasInconsistentes(boolean isAll, int id) {
        System.out.println("deleteAllAxiomasInconsistentes()");
        if (isAll) {
            System.out.println("if (isAll) ");
            if (axiomasProblematicos.size() == 0) {
                return false;
            }
            getOntologyManager().removeAxioms(ontology, new HashSet<OWLClassAxiom>(axiomasProblematicos));
            //Habilitando voltar de ontologia
            try {
                coreApp.owlRepository.saveState();
            } catch (OWLOntologyCreationException ex) {
                return false;
            }
            return true;
        } else if (axiomasProblematicos.size() > 0 && id - 1 >= 0 && id - 1 <= axiomasProblematicos.size() - 1) {
            System.out.println("else if (axiomasProblematicos.size() > 0...");
            RemoveAxiom removeAxiom = new RemoveAxiom(ontology, axiomasProblematicos.get(id - 1));
            getOntologyManager().applyChange(removeAxiom);

            try {
                coreApp.owlRepository.saveState();
            } catch (OWLOntologyCreationException ex) {
                Logger.getLogger(InsertionAnalyser.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        } else {
            System.out.println("else...");
            System.out.println("axiomasProblematicos.size() > 0: " + (axiomasProblematicos.size() > 0));
            System.out.println("id - 1 >= 0: " + (id - 1 >= 0));
            System.out.println("id - 1 <= axiomasProblematicos.size() - 1: " + (id - 1 <= axiomasProblematicos.size() - 1));
            return false;
        }

    }
}
