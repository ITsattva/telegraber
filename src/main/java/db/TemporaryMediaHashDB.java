package db;

import it.tdlight.jni.TdApi;
import util.ContentAlreadyExistsException;

import java.util.HashSet;
import java.util.Set;

public class TemporaryMediaHashDB implements DB{
    Set<String> mediaDB = new HashSet<>();

    @Override
    public void putIntoDB(String content) {
        mediaDB.add(content);
    }

    @Override
    public boolean checkRepetition(String content) {
        if(mediaDB.contains(content)) {
            throw new ContentAlreadyExistsException("This media is already in the DataBase!");
        }
        return false;
    }
}
