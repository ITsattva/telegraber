package db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class TemporaryMediaHashDB implements DB<String>{
    public static final Logger log = LoggerFactory.getLogger(TemporaryMediaHashDB.class);
    Set<String> mediaDB = new HashSet<>();

    @Override
    public void putIntoDB(String content) {
        log.info("Putting into DB has been started");
        mediaDB.add(content);
    }

    @Override
    public boolean checkRepetition(String content) {
        log.info("Checking repetition in the MediaDB");
        if(mediaDB.contains(content)) {
            log.error("Content doesn't pass repetition check by media");
            return false;
        }
        return true;
    }
}
