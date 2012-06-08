package plo.dbbd.server.resource;

import com.jayway.jsonpath.JsonPath;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import junit.framework.TestCase;
import plo.dbbd.server.Server;

import javax.ws.rs.core.MultivaluedMap;


public class SerieResourceTest extends TestCase {

    private Server server;
    private Client client;
    private WebResource resource;
    private MultivaluedMap<String,String> params;

    public void setUp() throws Exception {
        // Start server
        server = new Server();
        server.start();

        client = new Client();
        client.setFollowRedirects(true);

        resource = client.resource("http://localhost:9999/dbbd/serie");

        params = new MultivaluedMapImpl();
        params.add("isbn", "9782302007369");
        params.add("title", "Les Naufragés d'Ythaq");
    }

    public void tearDown() throws Exception {
        server.stop();

    }

    public void testBuildSerieFromIsbn() throws Exception {

        String response = resource.
                queryParams(params).
                get(String.class);

        assertEquals("Les Naufragés d'Ythaq", JsonPath.read(response, "$.title"));
        assertFalse((Boolean) JsonPath.read(response, "$.presentInDb"));

    }

    public void testCreateSerieFromIsbn() throws Exception {

        String response = resource.
                queryParams(params).
                post(String.class);

        assertEquals("Les Naufragés d'Ythaq", JsonPath.read(response, "$.title"));
        assertTrue((Boolean) JsonPath.read(response, "$.presentInDb"));

    }

    public void testDeleteSerieFromTitle() throws Exception {

        String response = resource.
                queryParams(params).
                delete(String.class);

        assertEquals("Les Naufragés d'Ythaq", JsonPath.read(response, "$.title"));
        assertFalse((Boolean) JsonPath.read(response, "$.presentInDb"));

    }


}
