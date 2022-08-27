package strategy;

import handler.ResultHandler;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.FormattedText;
import it.tdlight.jni.TdApi.UpdateNewMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class StrategyDocument implements Strategy{
	public static final Logger log = LoggerFactory.getLogger(StrategyDocument.class);

	@Override
	public void send(long chatId, UpdateNewMessage update) throws IOException {
		log.info("Sending a content");
		var content = (TdApi.MessageDocument) update.message.content;
		FormattedText text = content.caption;
		int documentId = content.document.document.id;

		client.send(new TdApi.SendMessage(chatId, 0, 0, null, null, new TdApi.InputMessageDocument(new TdApi.InputFileId(documentId), null, true, text)), new ResultHandler());
	}

	public void sendBatch(long chatId, ArrayList<TdApi.UpdateNewMessage> arrayContent) throws IOException {
		log.info("Sending a batch");
		TdApi.InputMessageContent[] messageContents = new TdApi.InputMessageContent[arrayContent.size()];
		for (int i = 0; i < arrayContent.size(); i++) {
			messageContents[i] = getInputMessageContent(arrayContent.get(i));
		}

		client.send(new TdApi.SendMessageAlbum(chatId, 0, 0, null, messageContents, false), new ResultHandler());
	}

	@Override
	public TdApi.InputMessageContent getInputMessageContent(TdApi.UpdateNewMessage message) throws IOException {
		log.info("Forming Input message content");
		var content = (TdApi.MessageDocument) message.message.content;
		FormattedText text = content.caption;
		int documentId = content.document.document.id;

		log.info("Returning Input message content");
		return new TdApi.InputMessageDocument(new TdApi.InputFileId(documentId), null, true, text);
	}

	@Override
	public TdApi.File getContentFile(TdApi.UpdateNewMessage content) throws IOException {
		log.info("Getting a TdApi.File from content");
		var document = (TdApi.MessageDocument) content.message.content;

		return document.document.document;
	}

	@Override
	public String getUniqueNumber(UpdateNewMessage content) throws IOException {
		log.info("Converting document size into the unique number");
		TdApi.File file = getContentFile(content);

		return String.valueOf(file.size);
	}

	@Override
	public String getTextOfContent(UpdateNewMessage content) throws IOException {
		log.info("Extracting text from content");
		var document = (TdApi.MessageDocument) content.message.content;

		return document.caption.text;
	}


}
