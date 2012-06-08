package plo.dbbd.server.resource;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import plo.dbbd.server.helpers.JSonMapper;
import plo.dbbd.server.model.Book;
import plo.dbbd.server.model.Counter;

import com.sun.jersey.spi.resource.Singleton;

@Path("/counter")
@Singleton
public class CounterResource {

    @GET
    @Produces("application/json")
    public String getCounters() {
        Counter counter = new Counter();
        counter.counterName = Counter.BOOKS_OWNED;
        counter.count = Book.count();
        
        List<Counter> counters =  new ArrayList<Counter>();
        counters.add(counter);

        return JSonMapper.generateJson(counters);
    }
}