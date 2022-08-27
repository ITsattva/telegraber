package db;

import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ContentAlreadyExistsException;
import util.PlagiarismHandler;

import java.util.HashSet;
import java.util.Set;

public class TemporaryTextDB implements DB<String>{
    public static final Logger log = LoggerFactory.getLogger(TemporaryTextDB.class);
    private Set<String> textDB = new HashSet<>();
    private double powerOfChange = 0.5;

    @Override
    public void putIntoDB(String content) {
        log.info("Putting into DB has been started");
        textDB.add(content);
    }

    @Override
    public boolean checkRepetition(String currentNews) {
        log.info("Checking text on repetition");
        if(!currentNews.trim().equals("")){
            for(String oldNews : textDB) {
                if(PlagiarismHandler.isTextAlreadyInTheChannel(currentNews, oldNews, powerOfChange)) {
                    log.error("Text doesn't pass repetition");
                    return false;
                }
            }
        }
        putIntoDB(currentNews);

        log.info("Content checking successfully passed");
        return true;
    }

    public double getPowerOfChange() {
        return powerOfChange;
    }

    public void setPowerOfChange(double powerOfChange) {
        this.powerOfChange = powerOfChange;
    }
}
