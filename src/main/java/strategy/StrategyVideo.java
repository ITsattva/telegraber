package strategy;

import handler.ResultHandler;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.FormattedText;
import it.tdlight.jni.TdApi.UpdateNewMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class StrategyVideo implements Strategy{
	public static final Logger log = LoggerFactory.getLogger(StrategyVideo.class);

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

		var content = (TdApi.MessageVideo) message.message.content;
		FormattedText text = content.caption;
		int videoId = content.video.video.id;
		int width = content.video.width;
		int height = content.video.height;
		int duration = content.video.duration;

		log.info("Returning Input message content");
		return new TdApi.InputMessageVideo(new TdApi.InputFileId(videoId), null, null, duration, width, height, false, text, 0);
	}

	@Override
	public TdApi.File getContentFile(TdApi.UpdateNewMessage content) throws IOException {
		log.info("Getting a TdApi.File from content");
		var video = (TdApi.MessageVideo) content.message.content;

		return video.video.video;
	}

	@Override
	public String getUniqueNumber(UpdateNewMessage content) throws IOException {
		log.info("Converting video size into the unique number");
		TdApi.File file = getContentFile(content);

		return String.valueOf(file.size);
	}

	@Override
	public String getTextOfContent(UpdateNewMessage content) throws IOException {
		log.info("Extracting text from content");
		var video = (TdApi.MessageVideo) content.message.content;
		String text = video.caption.text;

		return text;
	}
}
