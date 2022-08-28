package handler;

import db.TemporaryImageHashDB;
import dev.brachtendorf.jimagehash.hash.Hash;
import it.tdlight.client.GenericResultHandler;
import it.tdlight.client.Result;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sender.Sender;
import util.CashCleaner;
import util.ImageHashHandler;

import java.io.IOException;

public class DownloadResultHandler implements GenericResultHandler<TdApi.File> {
    private final Sender sender = new Sender();
    public static final Logger log = LoggerFactory.getLogger(DownloadResultHandler.class);

    @Override
    public void onResult(Result<TdApi.File> result) {

        log.info("Result Image HashHandler has got the Result");
        try {
            TdApi.File file = result.get();
            Hash currentHash = ImageHashHandler.getImageHash(file);
            if(TemporaryImageHashDB.staticCheckRepetition(currentHash)){
                log.info("checkRepetition passed");
                TemporaryImageHashDB.staticPutIntoDB(currentHash);
                sender.sendPicture();
            } else {
                log.error("checkRepetition didn't pass");
            }
            CashCleaner.cleanPhotosCash();
            log.info("ResultImageHashHandler has successfully finished");
        } catch (IOException e) {
            CashCleaner.cleanPhotosCash();
            log.error("IOException has been occurred!\n" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
