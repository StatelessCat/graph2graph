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
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.*;
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
            System.err.println("java -jar ./target/graph2graph-0.0.1.jar " +
                    "--graph <graph path> --baseuri <baseuri> --request <sparql construct request path>");
        }

        String graph_path = String.valueOf(options.valueOf("graph"));
        String base_uri = String.valueOf(options.valueOf("baseuri"));
        String request_path = String.valueOf(options.valueOf("request"));

        // Creating a main memory RDF Repository
        // TODO add an OWL reasonner
        Repository repo = new SailRepository(new MemoryStore());
        repo.initialize();

        try (RepositoryConnection connection = repo.getConnection()) {
            File file = new File(graph_path);
            connection.add(file, base_uri, RDFFormat.TURTLE);
            String graph_query =
                    Files.toString(new File(request_path), Charset.defaultCharset());

            Model m = Repositories.graphQuery(repo, graph_query, r -> QueryResults.asModel(r));

            // TODO remove this workaround. The purpose of this is to ensure each statements having the same subject
            //  will be grouped.
            // Write the new graph serialised in JSON-LD to a temporary file
            final String PATH_OF_TEMP_JSONLD = "/tmp/jsonld";
            Rio.write(m, new FileOutputStream(PATH_OF_TEMP_JSONLD), RDFFormat.JSONLD);

            // Read the new graph serialised in JSON-LD, parse it, re-serialise it in TURTLE and print it to STDOUT
            FileInputStream fileInputStream = new FileInputStream(new File(PATH_OF_TEMP_JSONLD));
            RDFParser rdfParser = Rio.createParser(RDFFormat.JSONLD);
            RDFWriter rdfWriter = Rio.createWriter(RDFFormat.TURTLE, System.out);
            rdfParser.setRDFHandler(rdfWriter);
            rdfParser.parse(fileInputStream, base_uri);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
