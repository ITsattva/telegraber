package strategy;

import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.client.Client;
import it.tdlight.jni.TdApi;
import java.io.IOException;

public interface Strategy {
	final SimpleTelegramClient client = Client.getClient();
	void send(long chatId, TdApi.UpdateNewMessage update) throws IOException;
}
