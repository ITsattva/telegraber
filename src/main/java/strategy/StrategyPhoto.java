package strategy;

import handler.ResultHandler;
import it.tdlight.client.Client;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.FormattedText;
import it.tdlight.jni.TdApi.Photo;
import it.tdlight.jni.TdApi.UpdateNewMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class StrategyPhoto implements Strategy{
	public static final Logger log = LoggerFactory.getLogger(StrategyPhoto.class);

	@Override
	public void send(long chatId, UpdateNewMessage update) throws IOException {
		log.info("Sending a photo");
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
	public TdApi.InputMessageContent getInputMessageContent(TdApi.UpdateNewMessage message) {
		log.info("Forming Input message content");

		var content = (TdApi.MessagePhoto) message.message.content;
		FormattedText text = content.caption;
		int photoId = 0;
		int width = 0;
		int height = 0;
		for (var photo : content.photo.sizes) {
			photoId = photo.photo.id;
			width = photo.width;
			height = photo.height;
		}
		return new TdApi.InputMessagePhoto(new TdApi.InputFileId(photoId), null, null, width, height, text, 0);
	}

	@Override
	public TdApi.File getContentFile(TdApi.UpdateNewMessage content) {
		log.info("Getting a TdApi.File from content");
		var photo = (TdApi.MessagePhoto) content.message.content;
		int countOfDifferentSizes = photo.photo.sizes.length;

		return photo.photo.sizes[countOfDifferentSizes].photo;
	}

	@Override
	public String getUniqueNumber(UpdateNewMessage update) {
		log.info("Converting photo size, width and height into the unique number");

		var content = (TdApi.MessagePhoto) update.message.content;
		int width = 0;
		int height = 0;
		TdApi.File file = null;
		for (var photo : content.photo.sizes) {
			width = photo.width;
			height = photo.height;
			file = photo.photo;
		}

		return file.size + "" + width + "" +height;
	}

	@Override
	public String getTextOfContent(UpdateNewMessage content) {
		log.info("Extracting text from content");
		var photo = (TdApi.MessagePhoto) content.message.content;

		return photo.caption.text;
	}
}
