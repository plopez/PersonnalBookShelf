package plo.dbbd.server.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plo.dbbd.server.helpers.MongoConnect;
import plo.dbbd.server.model.Book.ItemAttributes;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.mapping.Mapper;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.common.collect.Ordering;

@Entity(value = "seriesOwned", noClassnameStored = true)
public class Serie {
    private final static Logger LOGGER = LoggerFactory.getLogger(Serie.class);

    @Id
    private ObjectId id;
    
    public String title;

    @Embedded
    public ArrayList<String> authors = new ArrayList<String>();

    @Embedded
    public ArrayList<Book> books = new ArrayList<Book>();
    
    @JsonSerialize
    public boolean isPresentInDb() {
        return id != null;
    }

    // Persist
    public void save() {
        MongoConnect.getInstance().getDs().save(this);
    }

    public static Collection<Serie> findAll() {
        Query<Serie> query = MongoConnect.getInstance().getDs().createQuery(Serie.class).order("titre");
        
        return query.asList();
    }

    private Query<Serie> queryToFindMe() {
        return MongoConnect.getInstance().getDs().createQuery(Serie.class).field(Mapper.ID_KEY).equal(id);
    }

    public void updateBooks() {
        LOGGER.debug(this.id + "");
        UpdateOperations<Serie> ops = MongoConnect.getInstance().getDs().createUpdateOperations(Serie.class).set("books", this.books);
        Serie result = MongoConnect.getInstance().getDs().findAndModify(queryToFindMe(), ops);
        LOGGER.debug(result.books.size() + "");
    }

    public void sortList() {
        Comparator<Book> byTitre = new Comparator<Book>() {
            public int compare(final Book b1, final Book b2) {
                return Ordering.natural().compare(b1.ItemAttributes.Title.toLowerCase(),b2.ItemAttributes.Title.toLowerCase());
            }
        };
        Collections.sort(this.books, byTitre);
    }

    public static Serie findByTitle(String title) {
        Query<Serie> query = MongoConnect.getInstance().getDs().createQuery(Serie.class).filter("title = ", title);

        LOGGER.debug(query.toString());

        return query.get();
    }

    public void delete() {
        MongoConnect.getInstance().getDs().delete(Serie.class, this.id);
        this.id=null;
    }

}
