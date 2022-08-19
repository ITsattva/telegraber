package strategy;

import handler.ResultHandler;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.FormattedText;
import it.tdlight.jni.TdApi.UpdateNewMessage;
import java.io.IOException;
import java.util.ArrayList;

public class StrategyDocument implements Strategy{

	@Override
	public void send(long chatId, UpdateNewMessage update) throws IOException {
		var content = (TdApi.MessageDocument) update.message.content;
		FormattedText text = content.caption;
		int documentId = content.document.document.id;

		client.send(new TdApi.SendMessage(chatId, 0, 0, null, null, new TdApi.InputMessageDocument(new TdApi.InputFileId(documentId), null, true, text)), new ResultHandler());
	}

	public void sendBatch(long chatId, ArrayList<TdApi.UpdateNewMessage> arrayContent) {
		System.out.println("TRYING TO SEND BATCH OF PHOTO");
		TdApi.InputMessageContent[] messageContents = new TdApi.InputMessageContent[arrayContent.size()];
		for (int i = 0; i < arrayContent.size(); i++) {
			var content = (TdApi.MessageDocument) arrayContent.get(i).message.content;
			FormattedText text = content.caption;
			int documentId = content.document.document.id;

			messageContents[i] = new TdApi.InputMessageDocument(new TdApi.InputFileId(documentId), null, true, text);
		}
		System.out.println("PARSING WAS COMPLETED");
		client.send(new TdApi.SendMessageAlbum(chatId, 0, 0, null, messageContents, false), new ResultHandler());
	}
}
