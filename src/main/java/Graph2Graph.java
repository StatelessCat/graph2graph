import com.google.common.io.Files;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by raphael on 8/6/16.
 */
public class Graph2Graph {
    public static void main (String[] args) {
        OptionParser parser = new OptionParser();
        parser.accepts("graph").withRequiredArg();
        parser.accepts("baseuri").withRequiredArg();
        parser.accepts("request").withRequiredArg();
        OptionSet options = parser.parse(args);

        if ( !( options.has("graph") && options.has("baseuri") && options.has("request") ) ) {
            System.err.println("java -jar ./target/graph2graph-0.0.1.jar --graph <graph path> --baseuri <baseuri> --request <sparql construct request path>");
        }

        String graph_path = String.valueOf(options.valueOf("graph"));
        String base_uri = String.valueOf(options.valueOf("baseuri"));
        String request_path = String.valueOf(options.valueOf("request"));

        // Creating a main memory RDF Repository
        // TODO add an OWL reasonner
        Repository repo = new SailRepository(new MemoryStore());
        repo.initialize();

        File file = new File(graph_path);

        try (RepositoryConnection connection = repo.getConnection()) {
            connection.add(file, base_uri, RDFFormat.TURTLE);
            String graph_query =
                    Files.toString(new File(request_path), Charset.defaultCharset());

            Model m = Repositories.graphQuery(repo, graph_query, r -> QueryResults.asModel(r));

            Rio.write(m, System.out, RDFFormat.TURTLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
