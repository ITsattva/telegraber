package strategy;

import handler.ResultHandler;
import it.tdlight.client.Client;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.FormattedText;
import it.tdlight.jni.TdApi.Photo;
import it.tdlight.jni.TdApi.UpdateNewMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class StrategyPhoto implements Strategy{

	@Override
	public void send(long chatId, UpdateNewMessage update) {
		System.out.println("Strategy photo started");
		var content = (TdApi.MessagePhoto) update.message.content;
		FormattedText text = content.caption;
		int photoId = 0;
		int width = 0;
		int height = 0;
		TdApi.File file = null;
		for (var photo : content.photo.sizes) {
			photoId = photo.photo.id;
			width = photo.width;
			height = photo.height;
			file = photo.photo;
		}

		client.send(new TdApi.SendMessage(chatId, 0, 0, null, null, new TdApi.InputMessagePhoto(new TdApi.InputFileId(photoId), null, null, width, height, text, 0)), new ResultHandler());
	}

	public void sendBatch(long chatId, ArrayList<TdApi.UpdateNewMessage> arrayContent) {
		TdApi.InputMessageContent[] messageContents = new TdApi.InputMessageContent[arrayContent.size()];
		for (int i = 0; i < arrayContent.size(); i++) {
			var content = (TdApi.MessagePhoto)arrayContent.get(i).message.content;
			FormattedText text = content.caption;
			int photoId = 0;
			int width = 0;
			int height = 0;
			for (var photo : content.photo.sizes) {
				photoId = photo.photo.id;
				width = photo.width;
				height = photo.height;
			}
			messageContents[i] = new TdApi.InputMessagePhoto(new TdApi.InputFileId(photoId), null, null, width, height, text, 0);
		}
		client.send(new TdApi.SendMessageAlbum(chatId, 0, 0, null, messageContents, false), new ResultHandler());
	}

	@Override
	public TdApi.InputMessageContent getInputMessageContent(TdApi.UpdateNewMessage message) throws IOException {
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
	public TdApi.File getContentFile(TdApi.UpdateNewMessage content) throws IOException {
		var photo = (TdApi.MessagePhoto) content.message.content;
		TdApi.File file = null;
		for (var size : photo.photo.sizes) {
			file = size.photo;
		}
		return file;
	}

	@Override
	public String getUniqueNumber(UpdateNewMessage content) throws IOException {
		TdApi.File file = getContentFile(content);
		return file.remote.uniqueId;
	}

	@Override
	public String getTextOfContent(UpdateNewMessage content) throws IOException {
		var photo = (TdApi.MessagePhoto) content.message.content;
		String text = photo.caption.text;
		return text;
	}
}
