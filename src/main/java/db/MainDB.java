package db;

import dev.brachtendorf.jimagehash.hash.Hash;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import exception.ContentAlreadyExistsException;
import util.PlagiarismHandler;

import java.io.IOException;

public class MainDB {
    private final PlagiarismHandler plagiarismHandler = new PlagiarismHandler();
    private static final DB<Hash> imageHashDB = new TemporaryImageHashDB();
    private final DB<String> mediaDB = new TemporaryMediaHashDB();
    private final DB<String> textDB = new TemporaryTextDB();
    public static final Logger log = LoggerFactory.getLogger(MainDB.class);

    public void putIntoDB(TdApi.UpdateNewMessage content) throws IOException {
        log.info("Putting into DB has been started");
        plagiarismHandler.tuneUpStrategy(content.message.content);

        String textOfContent = plagiarismHandler.textTaker(content);

        log.info("Is content a picture: " + plagiarismHandler.isPicture());
        if (!plagiarismHandler.isText()) {
            String uniqueMediaNumber = plagiarismHandler.hashMaker(content);
            log.info("Receiving unique content's number: " + uniqueMediaNumber);
            mediaDB.putIntoDB(uniqueMediaNumber);
        }

        log.info("Content's text: " + textOfContent);
        textDB.putIntoDB(textOfContent);
    }

    public boolean checkContent(TdApi.UpdateNewMessage content) throws IOException {
        plagiarismHandler.tuneUpStrategy(content.message.content);
        String textOfContent = plagiarismHandler.textTaker(content);

        log.info("Checking the database on content repetition");
        if (!plagiarismHandler.isText() && !plagiarismHandler.isPicture()) {
            String uniqueMediaNumber = plagiarismHandler.hashMaker(content);
            log.info(uniqueMediaNumber + " :unique number");
            if (!mediaDB.checkRepetition(uniqueMediaNumber)) {
                log.error("Content doesn't pass repetition check by media");
                throw new ContentAlreadyExistsException("Content doesn't pass repetition check by media");
            }
        }

        log.info(textOfContent + " :content's text");
        return textDB.checkRepetition(textOfContent);
    }

    public static void putIntoDB(Hash hash) {
        imageHashDB.putIntoDB(hash);
    }

    public static void checkContent(Hash hash) {
        imageHashDB.checkRepetition(hash);
    }
}
