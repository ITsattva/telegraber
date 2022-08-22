package strategy;

import handler.ResultHandler;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.FormattedText;
import it.tdlight.jni.TdApi.UpdateNewMessage;

import java.io.IOException;
import java.util.ArrayList;

public class StrategyVideo implements Strategy{

	@Override
	public void send(long chatId, UpdateNewMessage update) {
		var content = (TdApi.MessageVideo) update.message.content;
		FormattedText text = content.caption;
		int videoId = content.video.video.id;
		int width = content.video.width;
		int height = content.video.height;
		int duration = content.video.duration;

		client.send(new TdApi.SendMessage(chatId, 0, 0, null, null, new TdApi.InputMessageVideo(new TdApi.InputFileId(videoId), null, null, duration, width, height, false, text, 0)), new ResultHandler());
	}

	public void sendBatch(long chatId, ArrayList<TdApi.UpdateNewMessage> arrayContent) {
		TdApi.InputMessageContent[] messageContents = new TdApi.InputMessageContent[arrayContent.size()];
		for (int i = 0; i < arrayContent.size(); i++) {
			var content = (TdApi.MessageVideo) arrayContent.get(i).message.content;
			FormattedText text = content.caption;
			int videoId = content.video.video.id;
			int width = content.video.width;
			int height = content.video.height;
			int duration = content.video.duration;
			
			messageContents[i] = new TdApi.InputMessageVideo(new TdApi.InputFileId(videoId), null, null, duration, width, height, false, text, 0);
		}
		client.send(new TdApi.SendMessageAlbum(chatId, 0, 0, null, messageContents, false), new ResultHandler());
	}

	@Override
	public TdApi.InputMessageContent getInputMessageContent(TdApi.UpdateNewMessage message) throws IOException {
		var content = (TdApi.MessageVideo) message.message.content;
		FormattedText text = content.caption;
		int videoId = content.video.video.id;
		int width = content.video.width;
		int height = content.video.height;
		int duration = content.video.duration;

		return new TdApi.InputMessageVideo(new TdApi.InputFileId(videoId), null, null, duration, width, height, false, text, 0);
	}

	@Override
	public TdApi.File getContentFile(TdApi.UpdateNewMessage content) throws IOException {
		var video = (TdApi.MessageVideo) content.message.content;
		TdApi.File file = video.video.video;
		return file;
	}

	@Override
	public String getUniqueNumber(UpdateNewMessage content) throws IOException {
		TdApi.File file = getContentFile(content);

		return String.valueOf(file.size);
	}

	@Override
	public String getTextOfContent(UpdateNewMessage content) throws IOException {
		var video = (TdApi.MessageVideo) content.message.content;
		String text = video.caption.text;
		return text;
	}
}
