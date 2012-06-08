package plo.dbbd.server.model;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemLookupResponse {
    public Items Items;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        public ArrayList<Book> books = new ArrayList<Book>();

        @JsonSetter("Item")
        public void addItem(Book item) {
            books.add(item);
        }
    }

}
