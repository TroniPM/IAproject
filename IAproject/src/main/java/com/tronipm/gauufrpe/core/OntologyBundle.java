/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tronipm.gauufrpe.core;

import edu.stanford.nlp.io.StringOutputStream;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * @project Gauufpe 2016 Apache License 2.0
 * @author Paulo Mateus [UFRPE-UAG] <paulomatew@gmail.com>
 */
public class OntologyBundle {

    private OWLOntologyManager ontologyManager = null;
    private OWLOntology ontology = null;
    private OWLDataFactory dataFactory = null;
    private StringOutputStream outputStreamOntology = null;
//    private StringOutputStream outputStreamOntologyManchester = null;

    public OntologyBundle() {

    }

    /**
     *
     * @return the ontologyManager
     */
    public OWLOntologyManager getOntologyManager() {
        return ontologyManager;
    }

    /**
     *
     * @param ontologyManager the ontologyManager to set
     */
    public void setOntologyManager(OWLOntologyManager ontologyManager) {
        this.ontologyManager = ontologyManager;
    }

    /**
     * @return the ontology
     */
    public OWLOntology getOntology() {
        return ontology;
    }

    /**
     * @param ontology the ontology to set
     */
    public void setOntology(OWLOntology ontology) {
        this.ontology = ontology;
    }

    /**
     * @return the dataFactory
     */
    public OWLDataFactory getDataFactory() {
        return dataFactory;
    }

    /**
     * @param dataFactory the dataFactory to set
     */
    public void setDataFactory(OWLDataFactory dataFactory) {
        this.dataFactory = dataFactory;
    }

    /**
     * @return the outputStreamOntology
     */
    public StringOutputStream getOutputStreamOntology() {
        return outputStreamOntology;
    }

    /**
     * @param outputStreamOntology the outputStreamOntology to set
     */
    public void setOutputStreamOntology(StringOutputStream outputStreamOntology) {
        this.outputStreamOntology = outputStreamOntology;
    }

    /**
     * @return the outputStreamOntologyManchester
     */
//    public StringOutputStream getOutputStreamOntologyManchester() {
//        return outputStreamOntologyManchester;
//    }
    /**
     * @param outputStreamOntologyManchester the outputStreamOntologyManchester
     * to set
     */
//    public void setOutputStreamOntologyManchester(StringOutputStream outputStreamOntologyManchester) {
//        this.outputStreamOntologyManchester = outputStreamOntologyManchester;
//    }
}
