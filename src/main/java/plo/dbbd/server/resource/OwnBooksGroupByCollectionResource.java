package plo.dbbd.server.resource;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plo.dbbd.server.helpers.JSonMapper;
import plo.dbbd.server.model.Book;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import com.sun.jersey.spi.resource.Singleton;

@Path("/own/groupByCollection")
@Singleton
public class OwnBooksGroupByCollectionResource {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(OwnBooksGroupByCollectionResource.class);

    @GET
    @Produces("application/json")
    public String getBookByIsbn() {
        Collection<Book> books = Book.findAll();
        Function<Book, String> getCollection = new Function<Book, String>() {

            public String apply(Book book) {
                if (book.ItemAttributes.Title.contains("Tome")) {
                    return book.ItemAttributes.Title.substring(0, book.ItemAttributes.Title.indexOf("Tome") - 1);
                } else if (book.ItemAttributes.Title.contains("tome")) {
                    return book.ItemAttributes.Title.substring(0, book.ItemAttributes.Title.indexOf("tome") - 1);
                }
                return "N/A";
            }
        };
        Collection<Book> filteredBook = Collections2.filter(books, new Predicate<Book>() {

            public boolean apply(Book book) {
                if (book.ItemAttributes.Title.contains("Tome") || book.ItemAttributes.Title.contains("tome")) {
                    return true;
                }
                return false;
            }
        });
        ImmutableListMultimap<String, Book> map = Multimaps.index(filteredBook, getCollection);

        LOGGER.debug(JSonMapper.generateJson(map.asMap()));
        return JSonMapper.generateJson(map.asMap());
    }
}