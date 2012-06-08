package plo.dbbd.server.resource;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import plo.dbbd.server.amazon.ItemLookup;
import plo.dbbd.server.helpers.JSonMapper;
import plo.dbbd.server.model.Book;
import plo.dbbd.server.model.Serie;
import plo.dbbd.server.utils.Caches;

@Path("/serie")
public class SerieResource {

    @GET
    @Produces("application/json;charset=utf-8")
    public String buildSerieFromIsbn(@QueryParam("isbn") String isbn) {
        Book book = Caches.loadBook(isbn);

        Serie serie = new Serie();
        serie.authors = book.ItemAttributes.authors;
        serie.title = book.ItemAttributes.Title.substring(0, book.ItemAttributes.Title.indexOf(","));
        serie.books = new ArrayList<Book>();
        serie.books.add(book);

        return JSonMapper.generateJson(serie);
    }

    @POST
    @Produces("application/json;charset=utf-8")
    public String createSerieFromIsbn(@QueryParam("isbn") String isbn) {
        Book book = Caches.loadBook(isbn);
        
        String title = book.ItemAttributes.Title.substring(0, book.ItemAttributes.Title.indexOf(","));

        Serie serie = Caches.loadSerie(title);
        if (serie == null) {
            serie = new Serie();

            serie.authors = book.ItemAttributes.authors;
            serie.title = book.ItemAttributes.Title.substring(0, book.ItemAttributes.Title.indexOf(","));
            serie.books = new ArrayList<Book>();
            serie.books.add(book);
            serie.save();
            Caches.cacheSeries.put(serie.title, serie);
        }
        

        return JSonMapper.generateJson(serie);
    }

    @DELETE
    @Produces("application/json;charset=utf-8")
    public String deleteSerieFromTitle(@QueryParam("title") String title) {
        Serie serie = Caches.loadSerie(title);

        serie.delete();

        return JSonMapper.generateJson(serie);
    }

    // V3
    @GET
    @Path("/amazon/{title}")
    @Produces("application/json")
    public String getSeriesFirstBook(@PathParam("title") String title) {
        Serie series = Caches.cacheSeries.get(title);
        Book book = series.books.get(0);
        Collection<Book> books = ItemLookup.getInstance().lookupCollectionByTitleAndPublisher(book.ItemAttributes.Title, book.ItemAttributes.Manufacturer);
        // Remove already present in db
        Collection<Book> filteredBooks = new ArrayList<Book>();
        for (Book amazonBook : books) {
            if (!series.books.contains(amazonBook)) {
                filteredBooks.add(amazonBook);
            }
        }
        return JSonMapper.generateJson(filteredBooks);
    }

    @GET
    @Path("/save/{isbn}")
    @Produces("application/json")
    public String saveSeriesByIsbn(@PathParam("isbn") String isbn) {
        Book book = Caches.loadBook(isbn);
        Serie series = new Serie();
        series.authors = book.ItemAttributes.authors;
        series.title = book.ItemAttributes.Title.substring(0, book.ItemAttributes.Title.indexOf(","));
        series.books = new ArrayList<Book>();
        series.books.add(book);
        Caches.cacheSeries.put(series.title, series);
        series.save();
        book.delete();
        return JSonMapper.generateJson(series);
    }

    @GET
    @Path("/add/{title}/{isbn}")
    @Produces("application/json")
    public String addToSeriesByIsbn(@PathParam("isbn") String isbn, @PathParam("title") String title) {
        Book book = Caches.loadBook(isbn);
        Serie series = Caches.cacheSeries.get(title);
        series.books.add(book);
        series.updateBooks();
        return JSonMapper.generateJson(series);
    }

    @GET
    @Path("/temp")
    @Produces("application/json")
    public String remove() {
        Serie series = Caches.cacheSeries.get("Le Maître de jeu");
        series.books.remove(3);
        series.updateBooks();
        return JSonMapper.generateJson(series);
    }
}
