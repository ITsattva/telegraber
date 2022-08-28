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
import java.util.HashSet;
import java.util.Set;

public class ResultHandlerForBatch implements GenericResultHandler<TdApi.File> {
    private final Sender sender = new Sender();
    private static final Set<Hash> hashBuffer = new HashSet<>();
    public static final Logger log = LoggerFactory.getLogger(ResultHandlerForBatch.class);


    @Override
    public void onResult(Result<TdApi.File> result) {
        Sender.plusOneDownload();
        log.info("HandlerForBatch on Result method has been launched");
        try {
            TdApi.File file = result.get();
            Hash currentHash = ImageHashHandler.getImageHash(file);
            hashBuffer.add(currentHash);

            if(Sender.readyForPush()) {
                if(!TemporaryImageHashDB.isThereRepetitionBetweenMainAndTemp(hashBuffer)){
                    log.info("Batch checking has passed");
                    TemporaryImageHashDB.staticPutIntoDB(currentHash);
                    if(Sender.readyForPush()){
                        log.info("Trying to send a batch");
                        sender.sendBatch(Sender.getChatId(), Sender.getTempList());
                        CashCleaner.cleanPhotosCash();
                        Sender.restartCounters();
                    }
                } else {
                    CashCleaner.cleanPhotosCash();
                    log.error("Batch checking hasn't passed");
                }
            } else {
                log.warn("Sender is not ready for pushing");
            }
            log.info("HandlerForBatch has successfully finished");
        } catch (IOException e) {
            log.error("IOException has been occurred!\n" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
