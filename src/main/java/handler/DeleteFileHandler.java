package handler;

import it.tdlight.client.GenericResultHandler;
import it.tdlight.client.Result;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteFileHandler implements GenericResultHandler<TdApi.Ok> {
    public static final Logger log = LoggerFactory.getLogger(DeleteFileHandler.class);

    @Override
    public void onResult(Result<TdApi.Ok> result) {
        log.info("File was successfully deleted");
    }
}
