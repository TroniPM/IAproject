/**
 * https://github.com/owlcs/owlapi/wiki/DL-Queries-with-a-real-reasoner
 */
package dl;

import java.util.Set;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.obolibrary.macro.ManchesterSyntaxTool;
//import org.obolibrary.macro.ManchesterSyntaxTool;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;

public class DLQueryParser {

    private final OWLOntology rootOntology;
    private final BidirectionalShortFormProvider bidiShortFormProvider;

    public DLQueryParser(OWLOntology rootOntology, ShortFormProvider shortFormProvider) {
        this.rootOntology = rootOntology;
        OWLOntologyManager manager = rootOntology.getOWLOntologyManager();
        Set<OWLOntology> importsClosure = rootOntology.getImportsClosure();
        // Create a bidirectional short form provider to do the actual mapping.
        // It will generate names using the input
        // short form provider.
        bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(manager,
                importsClosure, shortFormProvider);
    }

    public OWLClassExpression parseClassExpression(String classExpressionString) {
        OWLDataFactory dataFactory = rootOntology.getOWLOntologyManager()
                .getOWLDataFactory();
        ManchesterSyntaxTool parser = new ManchesterSyntaxTool(rootOntology);
        OWLClassExpression aa = parser.parseManchesterExpression(classExpressionString);
//        ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(
//                dataFactory, classExpressionString);
//        parser.setDefaultOntology(rootOntology);
//        OWLEntityChecker entityChecker = new ShortFormEntityChecker(bidiShortFormProvider);
//        parser.setOWLEntityChecker(entityChecker);
//        return parser.parseClassExpression();
        return aa;
    }
}