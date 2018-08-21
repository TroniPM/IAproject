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

import java.util.Set;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 *
 * @author Matt
 */
public abstract class AtomoOntologia {

    public String id = null;
    public OWLOntology ontology = null;
    public OWLOntologyManager manager = null;
    public OWLDataFactory factory = null;
    public String PROJECT_IRI = null;
//    public int line;
//    public int column;

    public AtomoOntologia(String PROJECT_IRI, OWLOntologyManager manager) {
        this.PROJECT_IRI = PROJECT_IRI;
        this.manager = manager;
        this.ontology = manager.getOntology(IRI.create(PROJECT_IRI));
        this.factory = manager.getOWLDataFactory();
    }

    public abstract Set<OWLAxiom> execClass();

    public abstract OWLClassExpression execProperty();

    @Override
    public abstract String toString();
}
