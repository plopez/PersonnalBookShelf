package plo.dbbd.server.amazon;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import plo.dbbd.server.model.Book;

public class ItemLookupTest {
    
    @Test
    public void testLookupCollectionByTitleAndAuthor(){
        Collection<Book> books = ItemLookup.getInstance().lookupCollectionByTitleAndPublisher("Pluto","Kana");
        Assert.assertEquals(9, books.size());
    }
    
    @Test
    public void testLookupBookByIsbn(){
        Book book =  ItemLookup.getInstance().lookupItemByIsbn("9782505011781");
        Assert.assertEquals("Pluto, tome 8", book.ItemAttributes.Title);
        Assert.assertTrue(book.ItemAttributes.authors.contains("Naoki Urasawa"));
    }

}
