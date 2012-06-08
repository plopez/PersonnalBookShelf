package plo.dbbd.server.resource;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import plo.dbbd.server.helpers.JSonMapper;
import plo.dbbd.server.model.Book;
import plo.dbbd.server.utils.Caches;

@Path("/book")
public class BookResource {

    
    @GET
    @Produces("application/json")
    public String getBookByIsbn(@QueryParam("isbn") String isbn) {
        Book book = Caches.loadBook(isbn);
        Caches.cacheBooks.put(isbn, book);
        return JSonMapper.generateJson(book);
    }

    @POST
    @Produces("application/json")
    public String saveBookByIsbn(@QueryParam("isbn") String isbn) {
        Book book = Caches.loadBook(isbn);
        book.save();
        Caches.cacheBooks.put(isbn, book);
        return JSonMapper.generateJson(book);
    }

    @DELETE
    @Produces("application/json")
    public String deleteBookByIsbn(@QueryParam("isbn") String isbn) {
        Book book = Caches.loadBook(isbn);
        book.delete();
        return JSonMapper.generateJson(book);
    }

    

    // @GET
    // @Path("/fullCollection")
    // @Produces("application/json")
    // public String getCollectionByIsbn(@PathParam("isbn") String isbn) {
    // Book book = loadBook(isbn);
    // if ((book.ItemAttributes.authors == null ||
    // book.ItemAttributes.authors.size() == 0) && book.isPresentInDb()) {
    // updateAuthor(book);
    // }
    // Collection<Book> books =
    // ItemLookup.getInstance().lookupCollectionByTitleAndPublisher(book.ItemAttributes.Title,
    // book.ItemAttributes.Manufacturer);
    // return JSonMapper.generateJson(books);
    // }

    // V2
    // @GET
    // @Path("/fullSeries")
    // @Produces("application/json")
    // public String getSeriesByIsbn(@PathParam("isbn") String isbn) {
    // Book book = loadBook(isbn);
    // if ((book.ItemAttributes.authors == null ||
    // book.ItemAttributes.authors.size() == 0) && book.isPresentInDb()) {
    // updateAuthor(book);
    // }
    // Collection<Book> books =
    // ItemLookup.getInstance().lookupCollectionByTitleAndPublisher(book.ItemAttributes.Title,
    // book.ItemAttributes.Manufacturer);
    // Series series = new Series();
    // series.authors=book.ItemAttributes.authors;
    // series.title=book.ItemAttributes.Title.substring(0,
    // book.ItemAttributes.Title.indexOf(","));
    // series.books=(ArrayList<Book>) books;
    // cacheSeries.put(series.title, series);
    // return JSonMapper.generateJson(series);
    // }

    

    // Update author
    //private void updateAuthor(Book book) {
    //    Book updatedBook = ItemLookup.getInstance().lookupItemByIsbn(book.ItemAttributes.ISBN);
    //    book.ItemAttributes.authors = updatedBook.ItemAttributes.authors;
    //    book.updateAuthors();
    //}
}