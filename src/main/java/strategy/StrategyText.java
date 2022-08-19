package strategy;

import handler.ResultHandler;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.FormattedText;
import it.tdlight.jni.TdApi.MessageText;
import it.tdlight.jni.TdApi.UpdateNewMessage;

import java.util.ArrayList;

public class StrategyText implements Strategy{

	@Override
	public void send(long chatId, UpdateNewMessage update) {
		FormattedText text = ((MessageText) update.message.content).text;
		client.send(new TdApi.SendMessage(chatId, 0, 0, null, null, new TdApi.InputMessageText(text, true, true)), new ResultHandler());
	}

	public void sendBatch(long chatId, ArrayList<TdApi.UpdateNewMessage> bufferList) {
		System.out.println("Void");
	}
}
