package dat3.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dat3.parallelization.Tester;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import javax.ws.rs.core.Context;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Lam
 */
enum Strategy {
  SEQUENTAL,
  PARALLEL_EXPENSIVE,
  PARALLEL_CHEAP
}

class ResultDTO {
    String  time;
    List<String> randomStrings;
    public ResultDTO(long time, List<String> randomStrings) {
        this.time = "" +((time) / 1_000_000) + " ms.";
        this.randomStrings = randomStrings;
    }
}

@Path("demos")
public class DemoEndpoints {
   
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    
    public DemoEndpoints() {
    }
    
    private ResultDTO getTaskAndTimeUsed(Strategy s) throws InterruptedException, TimeoutException, ExecutionException{
       long startTime = System.nanoTime();
       List<String> data = new ArrayList<>();
       switch(s){
           case SEQUENTAL: data = Tester.runSequental();break;
           case PARALLEL_EXPENSIVE: data = Tester.runParallel();break;
           case PARALLEL_CHEAP: data = Tester.runParallelWithCallables(threadPool);break;
       } 
       long timeUsed = System.nanoTime() - startTime;
       return new ResultDTO(timeUsed, data);
 
    }

    @Path("slow")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String verySlowEndpoint() throws InterruptedException, TimeoutException, ExecutionException {       
        return GSON.toJson(getTaskAndTimeUsed(Strategy.SEQUENTAL));
    }
    @Path("fast-but-expensive")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String fastButExpensive() throws InterruptedException, TimeoutException, ExecutionException {       
        return GSON.toJson(getTaskAndTimeUsed(Strategy.PARALLEL_EXPENSIVE));

    }
    @Path("fast-and-cheap")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String fastAndCheap() throws InterruptedException, TimeoutException, ExecutionException {       
        return GSON.toJson(getTaskAndTimeUsed(Strategy.PARALLEL_CHEAP));
    }
}
