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


public class BookResourceTest extends TestCase {

    private Server server;
    private Client client;
    private WebResource resource;

    private final static Logger LOGGER = LoggerFactory.getLogger(BookResourceTest.class);


    public void setUp() throws Exception {
        // Start server
        server = new Server();
        server.start();

        client = new Client();
        client.setFollowRedirects(true);

        resource = client.resource("http://localhost:9999/dbbd/book");

    }

    public void tearDown() throws Exception {
        server.stop();

    }

    public void testGetBookByIsbn() throws Exception {

        String response = resource.path("isbn").path("9782302007369").get(String.class);

        assertEquals("Les Naufragés d'Ythaq, Tome 7 : La marque des Ythes",JsonPath.read(response, "$.ItemAttributes.Title"));
        assertFalse((Boolean)JsonPath.read(response, "$.presentInDb"));

    }

    public void testSaveBookByIsbn() throws Exception {
        String response = resource.path("isbn").path("9782302007369").put(String.class);

        assertEquals("Les Naufragés d'Ythaq, Tome 7 : La marque des Ythes",JsonPath.read(response, "$.ItemAttributes.Title"));
        assertTrue((Boolean)JsonPath.read(response, "$.presentInDb"));

    }

    public void testDeleteBookByIsbn() throws Exception {
        String response = resource.path("isbn").path("9782302007369").delete(String.class);

        assertEquals("Les Naufragés d'Ythaq, Tome 7 : La marque des Ythes",JsonPath.read(response, "$.ItemAttributes.Title"));
        assertFalse((Boolean)JsonPath.read(response, "$.presentInDb"));

    }

    public void testGetAllOwnedBooks() throws Exception {
        String response = resource.path("owned").get(String.class);

        LOGGER.info(resource.path("title").path("Les Naufragés d'Ythaq").path("all").getURI().toString());
        LOGGER.info(response);

        List<String> books = JsonPath.read(response, "$.[*]");

        assertNotSame(0, books.size());

    }
}
