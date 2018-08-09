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
package dl;

import edu.stanford.nlp.io.StringOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxParserFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.io.OWLParser;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.parameters.Imports;

/**
 * Big thanks to Joshua Taylor
 *
 * https://stackoverflow.com/questions/21005908/convert-string-in-manchester-syntax-to-owlaxiom-object-using-owlapi-3-in-java
 *
 * @author Matt
 */
public class ReadManchesterString {

    public static void main(String[] args) throws OWLOntologyCreationException, IOException {
        // Get a manager and create an empty ontology, and a parser that 
        // can read Manchester syntax.
        final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        final OWLOntology ontology = manager.createOntology();
        final OWLParser parser = new ManchesterOWLSyntaxParserFactory().createParser(manager);

        // A small OWL ontology in the Manchester syntax.
        final String content = ""
                + "Prefix: so: <http://stackoverflow.com/q/21005908/1281433/>\n"
                + "Class: so:Person\n"
                + "Class: so:Young\n"
                + "\n"
                + "Class: so:Teenager\n"
                + "  SubClassOf: (so:Person and so:Young)\n"
                + "";

        // Create an input stream from the ontology, and use the parser to read its 
        // contents into the ontology.
        final InputStream in = new ByteArrayInputStream(content.getBytes());
        StreamDocumentSource src = new StreamDocumentSource(in);

        parser.parse(src, ontology, new OWLOntologyLoaderConfiguration());

        // Iterate over the axioms of the ontology. There are more than just the subclass
        // axiom, because the class declarations are also axioms.  All in all, there are
        // four:  the subclass axiom and three declarations of named classes.
        System.out.println("== All Axioms: ==");
        for (final OWLAxiom axiom : ontology.getAxioms()) {
            System.out.println(axiom);
        }

        // You can iterate over more specific axiom types, though.  For instance, 
        // you could just iterate over the TBox axioms, in which case you'll just
        // get the one subclass axiom. You could also iterate over
        // ontology.getABoxAxioms() to get ABox axioms.
        System.out.println("== TBox Axioms: ==");
        for (OWLAxiom axiom : ontology.getTBoxAxioms(Imports.INCLUDED)) {
            System.out.println(axiom);
        }

        OWLDocumentFormat format = new RDFXMLDocumentFormat();

        StringOutputStream currentOutputStreamOntology = new StringOutputStream();
        try {
            manager.saveOntology(ontology, format, currentOutputStreamOntology);

            System.out.println("----------------------------");
            System.out.println("----------------------------");
            System.out.println("----------------------------");
            System.out.println(currentOutputStreamOntology);
        } catch (OWLOntologyStorageException ex) {
            Logger.getLogger(ReadManchesterString.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
