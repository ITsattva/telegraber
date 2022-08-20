package strategy;

import handler.ResultHandler;
import it.tdlight.client.Client;
import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.MessageAudio;
import it.tdlight.jni.TdApi.MessagePhoto;
import it.tdlight.jni.TdApi.MessageText;
import it.tdlight.jni.TdApi.MessageVideo;
import java.io.IOException;
import java.util.ArrayList;

public class Sender {
	private Strategy strategy;

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public void tuneUpStrategy(TdApi.MessageContent content) {
		if (content instanceof MessageText) {
			setStrategy(new StrategyText());
		} else if (content instanceof MessageAudio) {
			setStrategy(new StrategyAudio());
		} else if (content instanceof MessagePhoto) {
			setStrategy(new StrategyPhoto());
		} else if (content instanceof MessageVideo) {
			setStrategy(new StrategyVideo());
		}
	}

	public void send(long chatId, TdApi.UpdateNewMessage update) throws IOException {
		tuneUpStrategy(update.message.content);
		strategy.send(chatId, update);
	}

	public void sendBatch(long chatId, ArrayList<TdApi.UpdateNewMessage> bufferList) throws IOException {
		if (bufferList.size() > 0) {
			tuneUpStrategy(bufferList.get(0).message.content);
		}
		strategy.sendBatch(chatId, bufferList);
	}

	public void sendExceptionToAuthor(Exception exception, String from) throws IOException {
		long debugChatId = -1001611624929L;

		TdApi.FormattedText text = new TdApi.FormattedText(from + "\n" + exception.getMessage(), null);
		Client.getClient().send(new TdApi.SendMessage(debugChatId, 0, 0, null, null, new TdApi.InputMessageText(text, true, true)), new ResultHandler());
	}
}
