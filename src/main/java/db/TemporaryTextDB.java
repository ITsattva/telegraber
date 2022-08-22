package db;

import it.tdlight.jni.TdApi;
import util.ContentAlreadyExistsException;
import util.PlagiarismHandler;

import java.util.HashSet;
import java.util.Set;

public class TemporaryTextDB implements DB{
    private Set<String> textDB = new HashSet<>();
    private double powerOfChange = 0.5;

    @Override
    public void putIntoDB(String content) {
        textDB.add(content);
    }

    @Override
    public boolean checkRepetition(String currentNews) {
        if(!currentNews.trim().equals("")){
            for(String oldNews : textDB) {
                if(PlagiarismHandler.isTextAlreadyInTheChannel(currentNews, oldNews, powerOfChange)) {
                    throw new ContentAlreadyExistsException("Content(TEXT) already in the channel");
                }
            }
        }
        return false;
    }

    public double getPowerOfChange() {
        return powerOfChange;
    }

    public void setPowerOfChange(double powerOfChange) {
        this.powerOfChange = powerOfChange;
    }
}
