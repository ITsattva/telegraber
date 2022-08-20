package db;

import it.tdlight.jni.TdApi;

import java.util.HashMap;
import java.util.Map;

public class TemporaryMediaDB {
    Map<TdApi.MessageContent, Integer> oldDB = new HashMap();
    Map<TdApi.MessageContent, Integer> newDB = new HashMap();

    public void putIntoDB(TdApi.MessageContent content) {
        newDB.put(content, content.hashCode());
    }

    public boolean checkContentForRepetition(TdApi.MessageContent content){
        return newDB.containsValue(content.hashCode())||oldDB.containsValue(content.hashCode());
    }


}
