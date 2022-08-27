package strategy;

import handler.ResultHandler;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.FormattedText;
import it.tdlight.jni.TdApi.MessageText;
import it.tdlight.jni.TdApi.UpdateNewMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class StrategyText implements Strategy{
	public static final Logger log = LoggerFactory.getLogger(StrategyText.class);

	@Override
	public void send(long chatId, UpdateNewMessage update) {
		log.info("Sending a text content");
		FormattedText text = ((MessageText) update.message.content).text;
		client.send(new TdApi.SendMessage(chatId, 0, 0, null, null, new TdApi.InputMessageText(text, true, true)), new ResultHandler());
	}

	public void sendBatch(long chatId, ArrayList<TdApi.UpdateNewMessage> bufferList) {
		log.error("sendBatch() doesn't have an implementation in the TextStrategy");
	}

	@Override
	public TdApi.InputMessageContent getInputMessageContent(TdApi.UpdateNewMessage message) throws IOException {
		log.error("getInputMessageContent() doesn't have an implementation in the TextStrategy");
		return null;
	}

	@Override
	public TdApi.File getContentFile(TdApi.UpdateNewMessage content) throws IOException {
		log.error("getContentFile() doesn't have an implementation in the TextStrategy");
		return null;
	}

	@Override
	public String getUniqueNumber(UpdateNewMessage content) {
		log.error("getUniqueNumber() doesn't have an implementation in the TextStrategy");
		return null;
	}

	@Override
	public String getTextOfContent(UpdateNewMessage content) throws IOException {
		log.info("Extracting text from content");
		var text = (TdApi.MessageText) content.message.content;

		return text.text.text;
	}
}
