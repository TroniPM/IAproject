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

import com.pmateus.util.Session;
import edu.stanford.nlp.io.StringOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import org.semanticweb.owlapi.vocab.OWLFacet;

public class OWLRepository {

    public static final IRI PROJECT_IRI = IRI.create("http://com.iaproject.edu#");
    public OWLOntologyManager currentOntologyManager = null;
    public OWLOntology currentOntology = null;
    public OWLDataFactory currentDataFactory = null;
    public StringOutputStream currentOutputStreamOntology = null;
    public int ontologyCurrendPosition = 0;
    public Process aProcess;

    public int currentErro = 0;
    private CoreApplication coreApp;

    public UndoRedoManager undoRedoOntologyManager = null;
    private boolean isTheStart = false;

    public void destroy() {
        currentOntologyManager = null;
        currentOntology = null;
        aProcess = null;
        coreApp = null;
        currentDataFactory = null;
        currentOutputStreamOntology = null;
    }

    OWLRepository(CoreApplication aThis) {
        this.coreApp = aThis;

        undoRedoOntologyManager = new UndoRedoManager();
    }

    public void saveState() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        try {
            saveOntologyToOutputStream();
        } catch (Exception ex) {
            Logger.getLogger(OWLRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        OWLOntology clone = m.loadOntologyFromOntologyDocument(new StringDocumentSource(currentOutputStreamOntology.toString()));
        OWLDataFactory df_clone = m.getOWLDataFactory();
        StringOutputStream output = new StringOutputStream();
        try {
            m.saveOntology(clone, output);
        } catch (OWLOntologyStorageException ex) {
            Logger.getLogger(OWLRepository.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*OWLOntology clone = m.createOntology();
         m.addAxioms(clone, original.getAxioms());
         for (OWLImportsDeclaration d : original.getImportsDeclarations()) {
         m.applyChange(new AddImport(clone, d));
         }*/
        OntologyBundle bundle = new OntologyBundle();
        bundle.setOntologyManager(m);
        bundle.setOntology(clone);
        bundle.setDataFactory(df_clone);
        bundle.setOutputStreamOntology(output);

        undoRedoOntologyManager.insert(bundle);

        try {
            coreApp.frame.verifyOntologyUndoRedoButtonEnabled();
        } catch (NullPointerException e) {
            //vai cair no catch no inicio do programa...
        }

    }

    public void setCurrentView(OntologyBundle bundle) {
        currentOntologyManager = bundle.getOntologyManager();
        currentOntology = bundle.getOntology();
        currentDataFactory = bundle.getDataFactory();
        currentOutputStreamOntology = bundle.getOutputStreamOntology();

        coreApp.atualizarTelas();
    }

    public void getPrevious() {
        if (undoRedoOntologyManager.canGetPrevious()) {
            OntologyBundle bundle = undoRedoOntologyManager.getPrevious();
            setCurrentView(bundle);
        }
    }

    public void getNext() {
        if (undoRedoOntologyManager.canGetNext()) {
            OntologyBundle bundle = undoRedoOntologyManager.getNext();
            setCurrentView(bundle);
        }
    }

    public void init(String path) {
        Object[] objs;
        try {
            //objs = loadOntologyFromFile("./data/ontology/knowledge_base.owl");
            objs = loadOntologyFromFile(path);

            currentOntologyManager = (OWLOntologyManager) objs[0];
            currentOntology = (OWLOntology) objs[1];
            currentDataFactory = currentOntologyManager.getOWLDataFactory();

        } catch (OWLOntologyCreationException | NullPointerException ex) {
            /*if (Session.isDebbug) {
             Logger.getLogger(OWLRepository.class.getName()).log(Level.SEVERE, null, ex);
             }*/

            currentOntologyManager = OWLManager.createOWLOntologyManager();
            try {
                currentOntology = currentOntologyManager.createOntology();
            } catch (OWLOntologyCreationException ex1) {
                if (Session.isDebbug) {
                    Logger.getLogger(OWLRepository.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            currentDataFactory = currentOntologyManager.getOWLDataFactory();

            currentErro = 1;
        }

        try {
            saveOntologyToOutputStream();
        } catch (Exception ex) {
            Logger.getLogger(OWLRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveOntologyToFile(/*OWLOntologyManager currentOntologyManager, OWLOntology currentOntology, */String path)
            throws Exception {
        File output = new File(path);
        IRI documentIRI2 = IRI.create(output);
        currentOntologyManager.saveOntology(currentOntology, new OWLXMLDocumentFormat(), documentIRI2);
        currentOntologyManager.saveOntology(currentOntology, documentIRI2);
        StringDocumentTarget target = new StringDocumentTarget();

        currentOntologyManager.saveOntology(currentOntology, target);
    }

    public void saveOntologyInferredToFile(/*OWLOntologyManager currentOntologyManager, OWLOntology currentOntology,*/String path)
            throws Exception {
        File output = new File(path);
        IRI documentIRI2 = IRI.create(output);
        currentOntologyManager.saveOntology(coreApp.pelletRepository.ontology_INFERRED_, new OWLXMLDocumentFormat(), documentIRI2);
        currentOntologyManager.saveOntology(coreApp.pelletRepository.ontology_INFERRED_, documentIRI2);
        StringDocumentTarget target = new StringDocumentTarget();

        currentOntologyManager.saveOntology(coreApp.pelletRepository.ontology_INFERRED_, target);
    }

    public void saveOntologyToOutputStream(/*OWLOntologyManager currentOntologyManager, OWLOntology currentOntology, String path*/)
            throws Exception {

        currentOutputStreamOntology = new StringOutputStream();

        currentOntologyManager.saveOntology(currentOntology, currentOutputStreamOntology);

    }

    public Object[] loadOntologyFromFile(String path)
            throws OWLOntologyCreationException {
        if (path == null || path.isEmpty()) {
            return null;
        }
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(new File(path));

        return new Object[]{ontologyManager, ontology};
    }

    public void saveOntologyToView(String string)
            throws Exception {
        File output = new File(string);

        IRI documentIRI2 = IRI.create(output);
        // save in OWL/XML format
        currentOntologyManager.saveOntology(currentOntology, new OWLXMLDocumentFormat(), documentIRI2);
        // save in RDF/XML
        currentOntologyManager.saveOntology(currentOntology, documentIRI2);
        // print out the currentOntology
        StringDocumentTarget target = new StringDocumentTarget();

        currentOntologyManager.saveOntology(currentOntology, target);

    }

    public void mergedOntology(String pathNewOntology)
            throws OWLOntologyStorageException, OWLOntologyCreationException {
        //OWLOntologyManager m = create();
        Object[] objs = null;
        try {
            objs = loadOntologyFromFile(pathNewOntology);
        } catch (OWLOntologyCreationException ex) {
            Logger.getLogger(OWLRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        OWLOntologyManager ontologyManager_new = (OWLOntologyManager) objs[0];
        OWLOntology ontology_new = (OWLOntology) objs[1];
        StringOutputStream aAux = new StringOutputStream();
        ontologyManager_new.saveOntology(ontology_new, aAux);
        String aString = aAux.toString();
        System.out.println(aString);

        currentOntologyManager.addAxiom(ontology_new, currentDataFactory.getOWLDeclarationAxiom(
                currentDataFactory.getOWLClass(IRI.create("PAULOMATEUS"))));

        OWLOntologyMerger merger = new OWLOntologyMerger(currentOntologyManager);
        IRI mergedOntologyIRI = IRI.create("Mateus");
        OWLOntology merged = merger.createMergedOntology(ontologyManager_new, mergedOntologyIRI);

        StringOutputStream aaa = new StringOutputStream();
        ontologyManager_new.saveOntology(merged, aaa);

        System.out.println("NEW ONTOLOGY START");
        System.out.println(aaa.toString());
        System.out.println("NEW ONTOLOGY END----------------------------------");
        //ontologyManager = ontologyManager_new;
        currentOntology = merged;
        currentDataFactory = currentOntologyManager.getOWLDataFactory();

        coreApp.atualizarTelas();
    }

    public void testMergedOntology()
            throws OWLException {
        /*OWLOntologyManager m = create();
         OWLOntology o1 = loadPizzaOntology(m);
         OWLOntology o2 = m.createOntology(EXAMPLE_IRI);
         m.addAxiom(o2, df.getOWLDeclarationAxiom(df.getOWLClass(IRI.create(EXAMPLE_IRI + "#Weasel"))));
         // Create our currentOntology merger
         OWLOntologyMerger merger = new OWLOntologyMerger(m);
         // We merge all of the loaded ontologies. Since an OWLOntologyManager is
         // an OWLOntologySetProvider we
         // just pass this in. We also need to specify the URI of the new
         // currentOntology that will be created.
         IRI mergedOntologyIRI = IRI.create("http://www.semanticweb.com/mymergedont");
         OWLOntology merged = merger.createMergedOntology(m, mergedOntologyIRI);
         assertTrue(merged.getAxiomCount() > o1.getAxiomCount());
         assertTrue(merged.getAxiomCount() > o2.getAxiomCount());*/
    }

    public void printCurrentOntology() {
        try {
            saveOntologyToOutputStream();
        } catch (Exception ex) {
            Logger.getLogger(OWLRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(currentOutputStreamOntology.toString());
    }

    /**
     * A PARTIR DAQUI, É O REPOSITÓRIO...
     */
    public void addEntity(String fullCommand) {
        fullCommand = fullCommand.replace(" ", "");
        if (fullCommand.contains(",")) {
            String[] aString = fullCommand.split(",");
            for (String in : aString) {
                currentOntologyManager.addAxiom(currentOntology, currentDataFactory.getOWLDeclarationAxiom(currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + in))));
            }
        } else {
            currentOntologyManager.addAxiom(currentOntology, currentDataFactory.getOWLDeclarationAxiom(currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + fullCommand))));
        }

    }

    public void addProperty(String fullCommand) {
        //datatypeRestriction();

        fullCommand = fullCommand.replace(" ", "");
        String mainClass = fullCommand.split(">")[0];
        if (fullCommand.contains(",")) {
            String[] aString = fullCommand.split(",");
            for (String in : aString) {
                currentOntologyManager.addAxiom(currentOntology, currentDataFactory.getOWLDeclarationAxiom(currentDataFactory.getOWLObjectProperty(IRI.create(PROJECT_IRI + in))));

            }
        } else {
            currentOntologyManager.addAxiom(currentOntology, currentDataFactory.getOWLDeclarationAxiom(currentDataFactory.getOWLObjectProperty(IRI.create(PROJECT_IRI + fullCommand))));
        }
    }

    /**
     * FAZER
     *
     * @param fullCommand
     */
    public void addComplement(String fullCommand) {

    }

    public void addUnionNewSintaxe(String fullCommand) {
        addUnion(fullCommand);

    }

    public void addIntersectionNewSintaxe(String fullCommand) {

    }

    public void addDisjointClass(String fullCommand) {
        fullCommand = fullCommand.replace(" ", "");
        String mainClass = fullCommand.split(">")[0];

        if (fullCommand.contains(",")) {
            String[] classesToLink = fullCommand.split(">")[1].split(",");
            OWLClass classeMain = currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + mainClass));
            for (String in : classesToLink) {
                //addDisjointClass(mainClass + ">" + in);
                currentOntologyManager.addAxiom(currentOntology, currentDataFactory.getOWLDisjointClassesAxiom(classeMain, currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + in))));
            }

            /*OWLClass classeMain = currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + mainClass));
             HashSet<OWLClass> intersecao = new HashSet<OWLClass>();
            
             for (String in : classesToLink) {
             intersecao.add(currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + in)));
             }
             intersecao.add(classeMain);
             Set<OWLClass> j = intersecao;
             OWLDisjointClassesAxiom objJuncao = currentDataFactory.getOWLDisjointClassesAxiom(j);
             currentOntologyManager.addAxiom(currentOntology, objJuncao);*/
        } else {
            OWLClass parent = currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + mainClass));
            String label = fullCommand.split(">")[1];
            OWLClass children = currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + label));
            OWLDisjointClassesAxiom objJuncao = currentDataFactory.getOWLDisjointClassesAxiom(children, parent);
            currentOntologyManager.addAxiom(currentOntology, objJuncao);

        }
    }

    public void addSubclass(String fullCommand) {
        fullCommand = fullCommand.replace(" ", "");
        String mainClass = fullCommand.split(">")[0];
        if (fullCommand.contains(",")) {
            OWLClass classeMain = currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + mainClass));
            String[] classesToLink = fullCommand.split(">")[1].split(",");
            for (String in : classesToLink) {
                currentOntologyManager.addAxiom(currentOntology, currentDataFactory.getOWLSubClassOfAxiom(classeMain, currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + in))));
            }

        } else {
            String rightPartExpression = fullCommand.split(">")[1];
            OWLClass classeChildren = currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + mainClass));
            OWLClass classeParent = currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + rightPartExpression));
            currentOntologyManager.addAxiom(currentOntology, currentDataFactory.getOWLSubClassOfAxiom(classeChildren, classeParent));
        }
        /**
         * Subclasse não funciona como interseção. Então não é necessário fazer
         * uso de collection para passar ao mesmo tempo as subclasses de uma
         * classe.
         */
    }

    public void addIntersection(String fullCommand) {
        fullCommand = fullCommand.replace(" ", "");
        String mainClass = fullCommand.split(">")[0];
        if (fullCommand.contains(",")) {
            OWLClass classeMain = currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + mainClass));
            String[] classesToLink = fullCommand.split(">")[1].split(",");
            HashSet<OWLClass> intersecao = new HashSet<OWLClass>();
            for (String in : classesToLink) {
                intersecao.add(currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + in)));
            }
            Set<OWLClass> j = intersecao;
            OWLObjectIntersectionOf objJuncao = currentDataFactory.getOWLObjectIntersectionOf(j);
            currentOntologyManager.addAxiom(currentOntology, currentDataFactory.getOWLSubClassOfAxiom(classeMain, objJuncao));
        } else {
            String rightPartExpression = fullCommand.split(">")[1];
            OWLObjectIntersectionOf objJuncao = currentDataFactory.getOWLObjectIntersectionOf(currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + rightPartExpression)));
            currentOntologyManager.addAxiom(currentOntology, currentDataFactory.getOWLSubClassOfAxiom(currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + mainClass)), objJuncao));
        }

    }

    public void addUnion(String fullCommand) {
        fullCommand = fullCommand.replace(" ", "");
        String mainClass = fullCommand.split(">")[0];
        if (fullCommand.contains(",")) {
            OWLClass classeMain = currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + mainClass));
            String[] classesToLink = fullCommand.split(">")[1].split(",");
            HashSet<OWLClass> intersecao = new HashSet<OWLClass>();

            for (String in : classesToLink) {
                intersecao.add(currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + in)));
            }
            Set<OWLClass> j = intersecao;
            OWLObjectUnionOf objJuncao = currentDataFactory.getOWLObjectUnionOf(j);
            currentOntologyManager.addAxiom(currentOntology, currentDataFactory.getOWLSubClassOfAxiom(classeMain, objJuncao));

        } else {
            String rightPartExpression = fullCommand.split(">")[1];
            OWLObjectUnionOf objJuncao = currentDataFactory.getOWLObjectUnionOf(currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + rightPartExpression)));
            currentOntologyManager.addAxiom(currentOntology, currentDataFactory.getOWLSubClassOfAxiom(currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + mainClass)), objJuncao));
        }
    }

    public void addRelationship(String fullCommand) {
        fullCommand = fullCommand.replace(" ", "");
        String childrenClassStr = fullCommand.split(">")[0];
        String rsLabelStr = fullCommand.split(">")[1]
                .replace(InsertionAnalyser.command_qtf_existencial, "")
                .replace(InsertionAnalyser.command_qtf_universal, "");
        String quantificador = fullCommand.split(">")[1].replace(rsLabelStr, "");
        String parentClassStr = fullCommand.split(">")[2];

        if (fullCommand.contains(",")) {
            //OWLClass classeMain = currentDataFactory.getOWLClass(mainClass);
            String[] classesToLink = parentClassStr.split(",");
            for (String in : classesToLink) {
                makeRelation(rsLabelStr, childrenClassStr, in, quantificador);
            }

        } else {
            makeRelation(rsLabelStr, childrenClassStr, parentClassStr, quantificador);
        }

    }

    public void addRelationshipNewSintax(String fullCommand) {
        fullCommand = fullCommand.replace(" ", "");
        String childrenClassStr = fullCommand.split(">")[0];
        String rsLabelStr = fullCommand.split(">")[1]
                .replace(InsertionAnalyser.command_qtf_existencial, "")
                .replace(InsertionAnalyser.command_qtf_universal, "");
        String quantificador = fullCommand.split(">")[1].replace(rsLabelStr, "");
        String parentClassStr = fullCommand.split(">")[2];

        if (fullCommand.contains(",")) {
            //OWLClass classeMain = currentDataFactory.getOWLClass(mainClass);
            String[] classesToLink = parentClassStr.split(",");
            for (String in : classesToLink) {
                makeRelationnewSintaxe(rsLabelStr, childrenClassStr, in, quantificador);
            }

        } else {
            makeRelationnewSintaxe(rsLabelStr, childrenClassStr, parentClassStr, quantificador);
        }

    }

    private void makeRelation(String label, String children, String parent, String quantificador) {
        OWLObjectProperty relacaoLabel = currentDataFactory.getOWLObjectProperty(IRI.create(PROJECT_IRI + label));
        OWLClass childrenClass = currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + children));
        OWLClass parentClass = currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + parent));

        OWLObjectPropertyDomainAxiom ax1 = currentDataFactory.getOWLObjectPropertyDomainAxiom(relacaoLabel, childrenClass);
        OWLObjectPropertyRangeAxiom ax2 = currentDataFactory.getOWLObjectPropertyRangeAxiom(relacaoLabel, parentClass);
        AddAxiom addAx = new AddAxiom(currentOntology, ax1);
        currentOntologyManager.applyChange(addAx);
        addAx = new AddAxiom(currentOntology, ax2);
        currentOntologyManager.applyChange(addAx);

        if (quantificador.equals(InsertionAnalyser.command_qtf_existencial)) {
            OWLClassExpression relationExp = currentDataFactory.getOWLObjectSomeValuesFrom(relacaoLabel, parentClass);
            OWLSubClassOfAxiom axFinal = currentDataFactory.getOWLSubClassOfAxiom(childrenClass, relationExp);
            addAx = new AddAxiom(currentOntology, axFinal);
            currentOntologyManager.applyChange(addAx);
        } else if (quantificador.equals(InsertionAnalyser.command_qtf_universal)) {
            OWLClassExpression relationExp = currentDataFactory.getOWLObjectAllValuesFrom(relacaoLabel, parentClass);
            OWLSubClassOfAxiom axFinal = currentDataFactory.getOWLSubClassOfAxiom(childrenClass, relationExp);
            addAx = new AddAxiom(currentOntology, axFinal);
            currentOntologyManager.applyChange(addAx);
        }
    }

    private void makeRelationnewSintaxe(String label, String children, String parent, String quantificador) {
        OWLObjectProperty relacaoLabel = currentDataFactory.getOWLObjectProperty(IRI.create(PROJECT_IRI + label));
        OWLClass childrenClass = currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + children));

        OWLClass parentClass = currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + parent));

        OWLObjectPropertyDomainAxiom ax1 = currentDataFactory.getOWLObjectPropertyDomainAxiom(relacaoLabel, childrenClass);
        OWLObjectPropertyRangeAxiom ax2 = currentDataFactory.getOWLObjectPropertyRangeAxiom(relacaoLabel, parentClass);
        AddAxiom addAx = new AddAxiom(currentOntology, ax1);
        currentOntologyManager.applyChange(addAx);
        addAx = new AddAxiom(currentOntology, ax2);
        currentOntologyManager.applyChange(addAx);

        if (quantificador.equals(InsertionAnalyser.command_qtf_existencial)) {
            OWLClassExpression relationExp = currentDataFactory.getOWLObjectSomeValuesFrom(relacaoLabel, childrenClass);
            OWLSubClassOfAxiom axFinal = currentDataFactory.getOWLSubClassOfAxiom(parentClass, relationExp);
            addAx = new AddAxiom(currentOntology, axFinal);
            currentOntologyManager.applyChange(addAx);
        } else if (quantificador.equals(InsertionAnalyser.command_qtf_universal)) {
            OWLClassExpression relationExp = currentDataFactory.getOWLObjectAllValuesFrom(relacaoLabel, childrenClass);
            OWLSubClassOfAxiom axFinal = currentDataFactory.getOWLSubClassOfAxiom(parentClass, relationExp);
            addAx = new AddAxiom(currentOntology, axFinal);
            currentOntologyManager.applyChange(addAx);
        }
    }

    void addEquivalencesClass(String fullCommand) {
        fullCommand = fullCommand.replace(" ", "");
        String mainClass = fullCommand.split(">")[0];

        if (fullCommand.contains(",")) {
            //OWLClass classeMain = currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + mainClass));
            String[] classesToLink = fullCommand.split(">")[1].split(",");
            /*HashSet<OWLClass> intersecao = new HashSet<OWLClass>();
             intersecao.add(classeMain);*/
            for (String in : classesToLink) {
                //intersecao.add(currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + in)));
                addEquivalencesClass(mainClass + ">" + in);
            }
            /*Set<OWLClass> j = intersecao;
             OWLEquivalentClassesAxiom objJuncao = currentDataFactory.getOWLEquivalentClassesAxiom(j);
             currentOntologyManager.addAxiom(currentOntology, objJuncao);*/

        } else {
            OWLClass classeMain = currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + mainClass));
            String classesToLink = fullCommand.split(">")[1];
            HashSet<OWLClass> intersecao = new HashSet<OWLClass>();
            intersecao.add(classeMain);
            intersecao.add(currentDataFactory.getOWLClass(IRI.create(PROJECT_IRI + classesToLink)));

            Set<OWLClass> j = intersecao;
            OWLEquivalentClassesAxiom objJuncao = currentDataFactory.getOWLEquivalentClassesAxiom(j);
            currentOntologyManager.addAxiom(currentOntology, objJuncao);

        }
    }
}
