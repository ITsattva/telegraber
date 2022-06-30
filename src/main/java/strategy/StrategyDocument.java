package strategy;

import handler.ResultHandler;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.FormattedText;
import it.tdlight.jni.TdApi.UpdateNewMessage;
import java.io.IOException;

public class StrategyDocument implements Strategy{

	@Override
	public void send(long chatId, UpdateNewMessage update) throws IOException {
		var content = (TdApi.MessageDocument) update.message.content;
		FormattedText text = content.caption;
		int documentId = content.document.document.id;

		client.send(new TdApi.SendMessage(chatId, 0, 0, null, null, new TdApi.InputMessageDocument(new TdApi.InputFileId(documentId), null, true, text)), new ResultHandler());
	}
}
