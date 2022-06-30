package strategy;

import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.MessageAudio;
import it.tdlight.jni.TdApi.MessagePhoto;
import it.tdlight.jni.TdApi.MessageText;
import it.tdlight.jni.TdApi.MessageVideo;
import java.io.IOException;

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
		strategy.send(chatId, update);
	}

}
