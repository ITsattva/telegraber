package db;

import dev.brachtendorf.jimagehash.hash.Hash;
import dev.brachtendorf.jimagehash.hash.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ImageHashHandler;

import java.util.HashSet;
import java.util.Set;

public class TemporaryImageHashDB implements DB<Hash>{
    public static final Logger log = LoggerFactory.getLogger(TemporaryImageHashDB.class);
    private static Set<Hash> imageHashDB = new HashSet<>();
    private static double degreeOfDifference = 0.2;

    public static void staticPutIntoDB(Hash content) {
        log.info("Static putting into DB has been started");
        log.info("DB size before adding: " + imageHashDB.size());
        imageHashDB.add(content);
        log.info("DB size after adding: " + imageHashDB.size());
    }

    public static boolean staticCheckRepetition(Hash currentHash) {
        log.info("Checking hashes has been started");
        log.info("Current hashDB size: " + imageHashDB.size());
        for(Hash hash : imageHashDB){
            log.info("normalized HammingDistance between 2 hashes: " + currentHash.normalizedHammingDistance(hash) + "\nand current degree of difference is: " + degreeOfDifference);
            if(currentHash.normalizedHammingDistance(hash) < degreeOfDifference) {
                log.error("Hash is already in the DB");
                return false;
            }
        }
        log.info("Hash checking has successfully passed!");
        return true;

    }

    @Override
    public void putIntoDB(Hash content) {
        log.info("Basic putting into DB has been started");
        log.info("DB size before adding: " + imageHashDB.size());
        imageHashDB.add(content);
        log.info("DB size after adding: " + imageHashDB.size());
    }

    @Override
    public boolean checkRepetition(Hash currentHash) {
        log.info("Checking hashes...");
        for(Hash hash : imageHashDB){
            log.info("normalized HammingDistance between 2 hashes: " + currentHash.normalizedHammingDistance(hash) + "\nand current degree of difference is: " + degreeOfDifference);
            if(currentHash.normalizedHammingDistance(hash) < degreeOfDifference) {
                log.error("Checking didn't pass successfully!");
                return false;
            }
        }
        log.info("Checking has been successfully passed");
        return true;

    }

    public static Set<Hash> getImageHashDB() {
        return imageHashDB;
    }

    public static boolean isThereRepetitionBetweenMainAndTemp(Set<Hash> temp) {
        for (Hash hashTemp : temp) {
            for(Hash hashMain : imageHashDB) {
                if(ImageHashHandler.comparePictureHash(hashTemp, hashMain)){
                    return true;
                }
            }
        }
        return false;
    }
}
