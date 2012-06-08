package plo.dbbd.server;

import java.io.IOException;
import plo.dbbd.server.filter.JsonpCallbackFilter;
import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.http.servlet.ServletAdapter;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class Server {

    private final static int LISTEN_PORT = 9999;
    private final static String ROOT_CONTEXT = "/dbbd";
    private static GrizzlyWebServer ws;

    // Start Grizzly Server
    public static void main(String[] args) throws IOException {
        start();
    }

    public static void start() throws IOException {
        ws = new GrizzlyWebServer(LISTEN_PORT);
        ServletAdapter jerseyServletAdapter = new ServletAdapter();
        jerseyServletAdapter.setServletInstance(new ServletContainer());
        jerseyServletAdapter.addInitParameter("com.sun.jersey.config.property.packages", "plo.dbbd.server.resource");
        jerseyServletAdapter.setServletPath(ROOT_CONTEXT);

        jerseyServletAdapter.addFilter(new JsonpCallbackFilter(), "JsonPFilter", null);

        ws.addGrizzlyAdapter(jerseyServletAdapter, null);

        ws.start();
    }

    public static void stop(){
        ws.stop();
    }
}
