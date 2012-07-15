package plo.dbbd.server.resource;

import javax.ws.rs.*;

import plo.dbbd.server.helpers.JSonMapper;
import plo.dbbd.server.model.Book;
import plo.dbbd.server.utils.Caches;

import java.util.Collection;

@Path("/book")
public class BookResource {

    
    @GET
    @Path("/isbn/{isbn}")
    @Produces("application/json")
    public String getBookByIsbn(@PathParam("isbn") String isbn) {
        Book book = Caches.loadBook(isbn);
        Caches.cacheBooks.put(isbn, book);
        return JSonMapper.generateJson(book);
    }

    @PUT
    @Path("/isbn/{isbn}")
    @Produces("application/json")
    public String saveBookByIsbn(@PathParam("isbn") String isbn) {
        Book book = Caches.loadBook(isbn);
        book.save();
        Caches.cacheBooks.put(isbn, book);
        return JSonMapper.generateJson(book);
    }

    @DELETE
    @Path("/isbn/{isbn}")
    @Produces("application/json")
    public String deleteBookByIsbn(@PathParam("isbn") String isbn) {
        Book book = Caches.loadBook(isbn);
        book.delete();
        return JSonMapper.generateJson(book);
    }

    @GET
    @Path("/owned/")
    @Produces("application/json")
    public String getAllOwnedBooks() {
        Collection<Book> books = Book.findAll();
        return JSonMapper.generateJson(books);
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