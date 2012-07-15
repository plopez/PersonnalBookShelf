package plo.dbbd.server.resource;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.*;

import plo.dbbd.server.amazon.ItemLookup;
import plo.dbbd.server.helpers.JSonMapper;
import plo.dbbd.server.model.Book;
import plo.dbbd.server.model.Serie;
import plo.dbbd.server.utils.Caches;

@Path("/serie")
public class SerieResource {

    @GET
    @Path("/isbn/{isbn}")
    @Produces("application/json;charset=utf-8")
    public String buildSerieByIsbn(@PathParam("isbn") String isbn) {
        Book book = Caches.loadBook(isbn);

        Serie serie = new Serie(book);


        return JSonMapper.generateJson(serie);
    }

    @PUT
    @Path("/isbn/{isbn}")
    @Produces("application/json;charset=utf-8")
    public String createSerieByIsbn(@PathParam("isbn") String isbn) {
        Book book = Caches.loadBook(isbn);
        
        String title = book.ItemAttributes.Title.substring(0, book.ItemAttributes.Title.indexOf(","));

        Serie serie = Caches.loadSerie(title);
        if (serie == null) {
            serie = new Serie(book);

        }
        if(!serie.isPresentInDb()){
            serie.save();
            Caches.cacheSeries.put(serie.title, serie);
        }
        

        return JSonMapper.generateJson(serie);
    }

    @DELETE
    @Path("/title/{title}")
    @Produces("application/json;charset=utf-8")
    public String deleteSerieByTitle(@PathParam("title") String title) {
        Serie serie = Caches.loadSerie(title);

        serie.delete();

        return JSonMapper.generateJson(serie);
    }

    @GET
    @Path("/title/{title}/all")
    @Produces("application/json")
    public String getAllBooksInSerieByTitle(@PathParam("title") String title) {
        Serie series = Caches.loadSerie(title);
        Collection<Book> books = ItemLookup.getInstance().lookupAllBooksInSerieByTitle(series.title);
        // Remove already present in db
        //Collection<Book> filteredBooks = new ArrayList<Book>();
        //for (Book amazonBook : books) {
        //    if (!series.books.contains(amazonBook)) {
        //        filteredBooks.add(amazonBook);
        //    }
        //}
        //return JSonMapper.generateJson(filteredBooks);
        return JSonMapper.generateJson(books);
    }

    @GET
    @Path("/save/{isbn}")
    @Produces("application/json")
    public String saveSeriesByIsbn(@PathParam("isbn") String isbn) {
        Book book = Caches.loadBook(isbn);
        Serie series = new Serie(book);
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
