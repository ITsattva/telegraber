package strategy;

import handler.ResultHandler;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.FormattedText;
import it.tdlight.jni.TdApi.UpdateNewMessage;

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
}
