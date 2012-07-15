package plo.dbbd.server.resource;

import com.jayway.jsonpath.JsonPath;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plo.dbbd.server.Server;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;


public class SerieResourceTest extends TestCase {

    private Server server;
    private Client client;
    private WebResource resource;

    private final static Logger LOGGER = LoggerFactory.getLogger(SerieResourceTest.class);

    public void setUp() throws Exception {
        // Start server
        server = new Server();
        server.start();

        client = new Client();
        client.setFollowRedirects(true);

        resource = client.resource("http://localhost:9999/dbbd/serie");
    }

    public void tearDown() throws Exception {
        server.stop();

    }

    public void testBuildSerieByIsbn() throws Exception {

        String response = resource.path("isbn").path("9782302007369").get(String.class);

        LOGGER.info(resource.path("isbn").path("9782302007369").getURI().toString());
        LOGGER.info(response);

        assertEquals("Les Naufragés d'Ythaq", JsonPath.read(response, "$.title"));
        assertFalse((Boolean) JsonPath.read(response, "$.presentInDb"));

    }

    public void testCreateSerieByIsbn() throws Exception {

        String response = resource.path("isbn").path("9782302007369").put(String.class);

        LOGGER.info(resource.path("isbn").path("9782302007369").getURI().toString());
        LOGGER.info(response);

        assertEquals("Les Naufragés d'Ythaq", JsonPath.read(response, "$.title"));
        assertTrue((Boolean) JsonPath.read(response, "$.presentInDb"));

    }

    public void testDeleteSerieByTitle() throws Exception {

        String response = resource.path("title").path("Les Naufragés d'Ythaq").delete(String.class);

        LOGGER.info(resource.path("title").path("Les Naufragés d'Ythaq").getURI().toString());
        LOGGER.info(response);

        assertEquals("Les Naufragés d'Ythaq", JsonPath.read(response, "$.title"));
        assertFalse((Boolean) JsonPath.read(response, "$.presentInDb"));

    }

    public void testGetAllBooksInSerieByTitle() throws Exception {

        String response = resource.path("title").path("Les Naufragés d'Ythaq").path("all").get(String.class);

        LOGGER.info(resource.path("title").path("Les Naufragés d'Ythaq").path("all").getURI().toString());
        LOGGER.info(response);

        List<String> books = JsonPath.read(response, "$.[*]");

        assertEquals(10, books.size());
    }


}
