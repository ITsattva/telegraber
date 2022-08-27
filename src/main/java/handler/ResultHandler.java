package handler;

import debug.LoggerHandler;
import it.tdlight.client.GenericResultHandler;
import it.tdlight.client.Result;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultHandler implements GenericResultHandler {
	public static final Logger log = LoggerFactory.getLogger(ResultHandler.class);

	@Override
	public void onResult(Result result) {
		log.info("Basic Result Handler got the result");
		//System.out.println(result);
	}
}
