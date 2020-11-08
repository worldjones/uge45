package rest;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import webscraper.TagCounter;
import webscraper.Tester;
import webscraper.TagDTO;

/**
 * REST Web Service
 *
 * @author lam
 */
@Path("scrape")
public class WebScraperResource {
    @Context
    private UriInfo context;
    private static ExecutorService es = Executors.newCachedThreadPool();
    
    @Path("sequental")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getTagsSequental() {
        long startTime = System.nanoTime();
        List<TagCounter> dataFetched = Tester.runSequental();
        long endTime = System.nanoTime()-startTime;
        return TagDTO.getTagsAsJson("Sequental fetching",dataFetched, endTime);
    }
    @Path("parrallel")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getTagsParrallel() throws InterruptedException, ExecutionException, TimeoutException {
        long startTime = System.nanoTime();
        List<TagDTO> dataFetched = Tester.runParrallel(es);
        long endTime = System.nanoTime()-startTime;
        return TagDTO.getTagDTOList("Parrallel fetching",dataFetched, endTime);
    }
    
    
}
