package plo.dbbd.server.helpers;

import plo.dbbd.server.model.Book;
import plo.dbbd.server.model.Serie;
import plo.dbbd.server.model.Book.ItemAttributes;
import plo.dbbd.server.model.Book.SmallImage;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;
import com.mongodb.Mongo;

public class MongoConnect {

    private final static MongoConnect instance = new MongoConnect();
    private Datastore ds;

    public Datastore getDs() {
        return ds;
    }

    public void setDs(Datastore ds) {
        this.ds = ds;
    }

    private MongoConnect() {
        super();
    }

    public static MongoConnect getInstance() {
        return instance;
    }

    static {
        Mongo mongo;
        try {
            mongo = new Mongo("localhost", 27017);

            MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);

            Morphia morphia = new Morphia();
            morphia.map(Book.class).map(SmallImage.class).map(ItemAttributes.class).map(Serie.class);
            MongoConnect.getInstance().setDs(morphia.createDatastore(mongo, "dbbd"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
