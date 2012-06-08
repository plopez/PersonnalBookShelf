package plo.dbbd.server.model;

import java.util.ArrayList;
import java.util.Collection;

import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSetter;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plo.dbbd.server.helpers.MongoConnect;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.mapping.Mapper;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(value = "booksOwned", noClassnameStored = true)
public class Book {

    private final static Logger LOGGER = LoggerFactory.getLogger(Book.class);

    @Id
    public ObjectId id;

    private Boolean wishList;

    @JsonSerialize
    public boolean isPresentInDb() {
        return id != null;
    }

    @JsonSerialize
    public boolean isOnWishlist() {
        if (wishList == null)
            return Boolean.FALSE;
        return wishList;
    }
    
    public boolean wishListIsNull() {
        return wishList == null;
    }

    public String DetailPageURL;
    public String ASIN;
    @Embedded
    public SmallImage SmallImage;
    @Embedded
    public ItemAttributes ItemAttributes;

    @Override
    public String toString() {
        return ItemAttributes.Title + " | " + ItemAttributes.authors.get(0);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Embedded
    public static class ItemAttributes {
        public ArrayList<String> authors = new ArrayList<String>();
        public String Manufacturer;
        public String Title;
        public String ISBN;

        @JsonSetter("Author")
        public void addAuthor(String author) {
            authors.add(author);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Embedded
    public static class SmallImage {
        public String URL;
    }

    // Persist
    public void save() {
        MongoConnect.getInstance().getDs().save(this);
    }

    private Query<Book> queryToFindMe() {
        return MongoConnect.getInstance().getDs().createQuery(Book.class).field(Mapper.ID_KEY).equal(id);
    }

    // Update author
    //public void updateAuthors() {
    //    LOGGER.debug(this.id + "");
    //    UpdateOperations<Book> ops = MongoConnect.getInstance().getDs().createUpdateOperations(Book.class).set("ItemAttributes.authors", this.ItemAttributes.authors);
    //    Book result = MongoConnect.getInstance().getDs().findAndModify(queryToFindMe(), ops);
    //    LOGGER.debug(result.ItemAttributes.authors.toString());
    //}

    // Update wishlist
    public void updateWishlist() {
        LOGGER.debug(this.id + "");
        UpdateOperations<Book> ops = MongoConnect.getInstance().getDs().createUpdateOperations(Book.class).set("wishlist", Boolean.FALSE);
        Book result = MongoConnect.getInstance().getDs().findAndModify(queryToFindMe(), ops);
        LOGGER.debug(result.wishList.toString());
    }

    // Load
    public static Book findByIsbn(String isbn) {
        ItemAttributes attributes = new ItemAttributes();
        attributes.ISBN = isbn;

        Query<Book> query = MongoConnect.getInstance().getDs().createQuery(Book.class).filter("ItemAttributes.ISBN = ", isbn);

        LOGGER.debug(query.toString());

        return query.get();
    }

    // Remove
    public void delete() {
        MongoConnect.getInstance().getDs().delete(Book.class, this.id);
        this.id=null;
    }

    // Load
    public static Collection<Book> findAll() {
        Query<Book> query = MongoConnect.getInstance().getDs().createQuery(Book.class);

        return query.asList();
    }

    // Count
    public static long count() {
        return MongoConnect.getInstance().getDs().getCount(Book.class);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass().equals(Book.class)){
            Book book = (Book) obj;
            if(book.ItemAttributes.ISBN.equals(this.ItemAttributes.ISBN)){
                return true;
            }
        }
        return super.equals(obj);
    }
    
    
}
