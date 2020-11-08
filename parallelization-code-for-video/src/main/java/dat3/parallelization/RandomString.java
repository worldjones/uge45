package dat3.parallelization;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author Lam
 */
public class RandomString {
    
    private boolean isDone = false;
    private final int stringLength;
    private String generatedString;
   
    RandomString(int length) {
        this.stringLength = length;
    }
    
    public void doWork() {
        try {
            generatedString = RandomStringUtils.randomAlphabetic(stringLength);
            Thread.sleep(1000);
            isDone = true;
        } catch (InterruptedException ex) {
            Logger.getLogger(RandomString.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public String makeARandomString() {
        try {
            generatedString = RandomStringUtils.randomAlphabetic(stringLength);
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(RandomString.class.getName()).log(Level.SEVERE, null, ex);
        }
        return generatedString;
    }
    
    public boolean isIsDone() {
        return isDone;
    }
    
    public String getGeneratedString() {
        return generatedString;
    }
    
}
