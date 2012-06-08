package plo.dbbd.server.resource;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plo.dbbd.server.amazon.ItemLookup;
import plo.dbbd.server.helpers.JSonMapper;
import plo.dbbd.server.model.Book;

@Path("/test")
public class TestRessource {

    private final static Logger LOGGER = LoggerFactory.getLogger(TestRessource.class);

    @GET
    @Produces("application/json")
    public String getOneTestBook() {
        Book book = ItemLookup.getInstance().lookupItemByIsbn("9782505011781");

        LOGGER.debug(book.toString());

        return JSonMapper.generateJson(book);
    }
   
    
    @Path("/test/amazon/{title}/{editor}")
    @Produces("application/json")
    public String getSeriesFirstBook(@PathParam("title") String title, @PathParam("editor") String editor) {
        Collection<Book> books = ItemLookup.getInstance().lookupCollectionByTitleAndPublisher(title, editor);
        return JSonMapper.generateJson(books);
    }

}
