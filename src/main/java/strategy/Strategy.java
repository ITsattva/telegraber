package strategy;

import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.client.Client;
import it.tdlight.jni.TdApi;
import java.io.IOException;
import java.util.ArrayList;

public interface Strategy {
	SimpleTelegramClient client = Client.getClient();
	void send(long chatId, TdApi.UpdateNewMessage update) throws IOException;
	void sendBatch(long chatId, ArrayList<TdApi.UpdateNewMessage> content) throws IOException;
}
