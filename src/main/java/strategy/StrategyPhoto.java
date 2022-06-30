package strategy;

import handler.ResultHandler;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.FormattedText;
import it.tdlight.jni.TdApi.Photo;
import it.tdlight.jni.TdApi.UpdateNewMessage;
import java.io.IOException;

public class StrategyPhoto implements Strategy{

	@Override
	public void send(long chatId, UpdateNewMessage update) {
		var content = (TdApi.MessagePhoto) update.message.content;
		FormattedText text = content.caption;
		int photoId = 0;
		int width = 0;
		int height = 0;
		for (var photo : content.photo.sizes) {
			photoId = photo.photo.id;
			width = photo.width;
			height = photo.height;
		}

		client.send(new TdApi.SendMessage(chatId, 0, 0, null, null, new TdApi.InputMessagePhoto(new TdApi.InputFileId(photoId), null, null, width, height, text, 0)), new ResultHandler());
	}
}
