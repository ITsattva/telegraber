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
import java.io.IOException;
import java.util.ArrayList;

public class StrategyAudio implements Strategy{

	@Override
	public void send(long chatId, UpdateNewMessage update) throws IOException {
		var content = (MessageAudio) update.message.content;
		FormattedText text = content.caption;

		int id = content.audio.audio.id;
		int duration = content.audio.duration;
		String title= content.audio.title;
		String performer = content.audio.performer;

		client.send(new TdApi.SendMessage(chatId, 0, 0, null, null, new TdApi.InputMessageAudio(new InputFileId(id), null, duration, title, performer, text)), new ResultHandler());
	}

	public void sendBatch(long chatId, ArrayList<TdApi.UpdateNewMessage> arrayContent) {
		TdApi.InputMessageContent[] messageContents = new TdApi.InputMessageContent[arrayContent.size()];
		for (int i = 0; i < arrayContent.size(); i++) {
			var content = (MessageAudio) arrayContent.get(i).message.content;
			FormattedText text = content.caption;
			int id = content.audio.audio.id;
			int duration = content.audio.duration;
			String title= content.audio.title;
			String performer = content.audio.performer;

			messageContents[i] = new TdApi.InputMessageAudio(new InputFileId(id), null, duration, title, performer, text);
		}

		client.send(new TdApi.SendMessageAlbum(chatId, 0, 0, null, messageContents, false), new ResultHandler());
	}

	@Override
	public TdApi.InputMessageContent getInputMessageContent(TdApi.UpdateNewMessage message) throws IOException {
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
		var audio = (MessageAudio) content.message.content;
		TdApi.File file = audio.audio.audio;

		return file;
	}

	@Override
	public String getUniqueNumber(UpdateNewMessage content) throws IOException {
		TdApi.File file = getContentFile(content);

		return String.valueOf(file.size);
	}

	@Override
	public String getTextOfContent(UpdateNewMessage content) throws IOException {
		var audio = (TdApi.MessageAudio) content.message.content;
		String text = audio.caption.text;

		return text;
	}
}
