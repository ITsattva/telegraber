package db;

import it.tdlight.jni.TdApi;
import util.ContentAlreadyExistsException;
import util.PlagiarismHandler;

import java.io.IOException;

public class TemporaryDB {
    private PlagiarismHandler plagiarismHandler = new PlagiarismHandler();
    private DB mediaDB = new TemporaryMediaHashDB();
    private DB textDB = new TemporaryTextDB();

    public void putIntoDB(TdApi.UpdateNewMessage content) throws IOException {
        plagiarismHandler.tuneUpStrategy(content.message.content);

        String textOfContent = plagiarismHandler.textTaker(content);

        System.out.println("Uploading database");
        if(!plagiarismHandler.isText()){
            String uniqueMediaNumber = plagiarismHandler.hashMaker(content);
            System.out.println(uniqueMediaNumber + " :unique number");
            mediaDB.putIntoDB(uniqueMediaNumber);
        }
        System.out.println(textOfContent + " :content's text");
        textDB.putIntoDB(textOfContent);
    }

    public boolean checkContent(TdApi.UpdateNewMessage content) throws IOException {
        plagiarismHandler.tuneUpStrategy(content.message.content);

        String textOfContent = plagiarismHandler.textTaker(content);
        System.out.println("Check database");
        if(!plagiarismHandler.isText()){
            String uniqueMediaNumber = plagiarismHandler.hashMaker(content);
            System.out.println(uniqueMediaNumber + " :unique number");
            if(mediaDB.checkRepetition(uniqueMediaNumber)){
                throw new ContentAlreadyExistsException("Content doesn't pass repetition check by media");
            }
        }

        System.out.println(textOfContent + " :content's text");
        if (textDB.checkRepetition(textOfContent)) {
            throw new ContentAlreadyExistsException("Content doesn't pass repetition check by text");
        }

        return false;
    }
}
