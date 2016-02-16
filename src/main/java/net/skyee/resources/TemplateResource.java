package net.skyee.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import io.dropwizard.jersey.caching.CacheControl;
import net.skyee.api.Saying;
import net.skyee.bean.Template;
import net.skyee.dao.StocksDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Path("/hello")
@Produces(MediaType.APPLICATION_JSON)
public class TemplateResource {

    private static final Logger log = LoggerFactory.getLogger(TemplateResource.class);

    private final Template template;
    private StocksDAO stocksDAO;
    private final AtomicLong counter;

    public TemplateResource(Template template, StocksDAO stocksDAO) {
        this.stocksDAO = stocksDAO;
        this.template = template;
        this.counter = new AtomicLong();
    }

    @GET
    @Timed(name = "get-requests")
    @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.DAYS)
    public Saying sayHello(@QueryParam("name") Optional<String> name) {
        return new Saying(counter.incrementAndGet(), template.render(name));
    }

    @POST
    public void receiveHello(@Valid Saying saying) {
        log.info("Received a saying: {}", saying);
    }
}
