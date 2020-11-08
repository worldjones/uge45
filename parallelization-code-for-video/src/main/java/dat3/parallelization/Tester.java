package dat3.parallelization;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lam
 */
public class Tester {

    public static List<RandomString> getRandomStrings() {
        List<RandomString> randomStrings = new ArrayList<>();
        randomStrings.add(new RandomString(4));
        randomStrings.add(new RandomString(6));
        randomStrings.add(new RandomString(8));
        randomStrings.add(new RandomString(10));
        randomStrings.add(new RandomString(12));
        randomStrings.add(new RandomString(14));
        return randomStrings;
    }

    public static List<String> runSequental() {
        List<RandomString> randomStrings = getRandomStrings();

        List<String> results = new ArrayList<>();
        randomStrings.forEach(str -> {
            str.doWork();
            results.add(str.getGeneratedString());
        });
        return results;
    }

    public static List<String> runParallel() throws InterruptedException {
      List<RandomString> randomStrings = getRandomStrings();

        ExecutorService threadPool = Executors.newCachedThreadPool();
        randomStrings.forEach(rs -> {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    rs.doWork();
                }
            };
            threadPool.execute(task);
        });
        threadPool.shutdown();
        threadPool.awaitTermination(15, TimeUnit.SECONDS);
        List<String> results = new ArrayList<>();
         randomStrings.forEach(str -> {
            results.add(str.getGeneratedString());
        });
        return results;
    }
    
    public static List<String> runParallelWithCallables( ExecutorService threadPool) throws TimeoutException, InterruptedException, ExecutionException  {
        List<RandomString> randomStrings = getRandomStrings();
        
        List<Future<String>> futures = new ArrayList<>();

        for (RandomString rs : randomStrings) {
            Callable<String> task = new Callable<String>() {
                @Override
                public String call() {
                    return rs.makeARandomString();
                }
            };
            futures.add(threadPool.submit(task));
        }
        List<String> results = new ArrayList<>();
        for (Future<String> fut : futures) {
            results.add(fut.get(2000, TimeUnit.MILLISECONDS));
        }
        return results;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        
        long start = System.nanoTime();

        List<String> fetchedData = Tester.runSequental();

        long timeSequental = System.nanoTime() - start;
        System.out.println("Time Sequential ---> " + ((timeSequental) / 1_000_000) + " ms.");

        fetchedData.forEach(rs -> {
            System.out.println(rs);
        });
        System.out.println("-----------------------------------------");
        
        
        start = System.nanoTime();
        List<String> resultsFromParallel = Tester.runParallel();
        resultsFromParallel.forEach(str -> {
            System.out.println(str);
        });
        System.out.println("-----------------------------------------");
        long timeParallel = System.nanoTime() - start;
        System.out.println("Time Parallel: " + ((timeParallel) / 1_000_000) + " ms.");
        System.out.println("Parallel was " + timeSequental / timeParallel + " times faster");
        
        System.out.println("--------   Callable/Future  ------------------");
        start = System.nanoTime();
        List<String> results = null;
        try {
            
        ExecutorService threadPool = Executors.newCachedThreadPool();
            results = Tester.runParallelWithCallables(threadPool);

            long time  = System.nanoTime() - start;
            System.out.println("Time Parallel (Future/Callables) ---> " + ((time) / 1_000_000) + " ms.");

            results.forEach(rs -> {
                System.out.println(rs);
            });
        } catch (TimeoutException ex) {
            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("-----------------------------------------");
        
    }
   
}
