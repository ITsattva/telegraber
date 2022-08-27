package strategy;

import handler.ResultHandler;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.File;
import it.tdlight.jni.TdApi.FormattedText;
import it.tdlight.jni.TdApi.InputFileId;
import it.tdlight.jni.TdApi.InputMessageAudio;
import it.tdlight.jni.TdApi.MessageAudio;
import it.tdlight.jni.TdApi.SendMessage;
import it.tdlight.jni.TdApi.UpdateNewMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class StrategyAudio implements Strategy{
	public static final Logger log = LoggerFactory.getLogger(StrategyAudio.class);

	@Override
	public void send(long chatId, UpdateNewMessage update) throws IOException {
		log.info("Sending a content");
		client.send(new TdApi.SendMessage(chatId, 0, 0, null, null, getInputMessageContent(update)), new ResultHandler());
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
		var content = (MessageAudio) message.message.content;
		FormattedText text = content.caption;
		int id = content.audio.audio.id;
		int duration = content.audio.duration;
		String title= content.audio.title;
		String performer = content.audio.performer;

		return new TdApi.InputMessageAudio(new InputFileId(id), null, duration, title, performer, text);
	}

	@Override
	public File getContentFile(UpdateNewMessage content) throws IOException {
		log.info("Getting a TdApi.File from content");
		var audio = (MessageAudio) content.message.content;

		return audio.audio.audio;
	}

	@Override
	public String getUniqueNumber(UpdateNewMessage content) throws IOException {
		log.info("Converting audio size into the unique number");
		TdApi.File file = getContentFile(content);

		return String.valueOf(file.size);
	}

	@Override
	public String getTextOfContent(UpdateNewMessage content) throws IOException {
		log.info("Extracting text from content");
		var audio = (TdApi.MessageAudio) content.message.content;

		return audio.caption.text;
	}
}
