package plo.dbbd.server.utils;

import java.util.HashMap;
import java.util.Map;

import plo.dbbd.server.amazon.ItemLookup;
import plo.dbbd.server.model.Book;
import plo.dbbd.server.model.Serie;

public class Caches {
    
    public static Map<String, Book> cacheBooks = new HashMap<String, Book>();
    public static Map<String, Serie> cacheSeries = new HashMap<String, Serie>();
    public static boolean cacheBooksFullyLoaded = false;
    public static boolean cacheSeriesFullyLoaded = false;
    
    public static Book loadBook(String isbn) {
        Book book = Caches.cacheBooks.get(isbn);
        if (book == null) {
            book = Book.findByIsbn(isbn);
        }
        if (book == null) {
            book = ItemLookup.getInstance().lookupItemByIsbn(isbn);
        }
        // patch de mis à jour
        // if (book.isPresentInDb() && book.wishListIsNull()) {
        // book.updateWishlist();
        // cacheBooks.put(isbn, book);
        // }
        return book;
    }
    
    public static Serie loadSerie(String title) {
        Serie serie = Caches.cacheSeries.get(title);
        if (serie == null) {
            serie = Serie.findByTitle(title);
        }
        if (serie == null) {
            serie = ItemLookup.getInstance().lookupSerieByTitle(title);
        }
        
        return serie;
    }


}
