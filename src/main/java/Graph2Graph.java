import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * Created by raphael on 8/6/16.
 */
public class Graph2Graph {
    public static void main (String[] args) {
        Repository repo = new SailRepository(new MemoryStore());
        repo.initialize();
        String namespace = "http://ex.co/";
        ValueFactory valueFactory = repo.getValueFactory();
        IRI john = valueFactory.createIRI(namespace, "John");
        try (RepositoryConnection connection = repo.getConnection()) {
            connection.add(john, RDF.TYPE, FOAF.PERSON);
            connection.add(john, RDFS.LABEL, valueFactory.createLiteral("John"));
            RepositoryResult<Statement> statements = connection.getStatements(null, null, null);
            Model model = QueryResults.asModel(statements);
            Rio.write(model, System.out, RDFFormat.TURTLE);
        }
    }
}
