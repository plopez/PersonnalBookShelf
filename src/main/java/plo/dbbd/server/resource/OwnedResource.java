package plo.dbbd.server.resource;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plo.dbbd.server.helpers.JSonMapper;
import plo.dbbd.server.model.Book;
import plo.dbbd.server.model.Serie;
import plo.dbbd.server.utils.Caches;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.sun.jersey.spi.resource.Singleton;

@Path("/owned")
@Singleton
public class OwnedResource {

    private final static Logger LOGGER = LoggerFactory.getLogger(OwnedResource.class);

    @GET
    @Path("/books")
    @Produces("application/json")
    public String getAllBooks() {
        Collection<Book> books = Book.findAll();
        Function<Book, String> getIsbn = new Function<Book, String>() {
            public String apply(Book book) {
                return book.ItemAttributes.ISBN;
            }
        };

        if (!Caches.cacheBooksFullyLoaded) {
            Caches.cacheBooks.putAll(Maps.uniqueIndex(books, getIsbn));
            Caches.cacheBooksFullyLoaded = true;
        }

        LOGGER.debug("" + books.size());

        return JSonMapper.generateJson(books);
    }

    @GET
    @Path("/series")
    @Produces("application/json")
    public String getAllSeries() {
        Collection<Serie> series = Serie.findAll();
        Function<Serie, String> getTitle = new Function<Serie, String>() {
            public String apply(Serie series) {
                series.sortList();
                return series.title;
            }
        };

        if (!Caches.cacheSeriesFullyLoaded) {
            Caches.cacheSeries.putAll(Maps.uniqueIndex(series, getTitle));
            Caches.cacheSeriesFullyLoaded = true;
        }

        LOGGER.debug("" + series.size());

        return JSonMapper.generateJson(series);
    }
}