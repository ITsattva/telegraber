package strategy;

import db.TemporaryDB;
import db.TemporaryMediaHashDB;
import db.TemporaryTextDB;
import handler.ResultHandler;
import it.tdlight.client.Client;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.MessageAudio;
import it.tdlight.jni.TdApi.MessagePhoto;
import it.tdlight.jni.TdApi.MessageText;
import it.tdlight.jni.TdApi.MessageVideo;
import util.ContentAlreadyExistsException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Sender {
    private Strategy strategy;
    private TemporaryDB tempDB = new TemporaryDB();

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
        } else if (content instanceof TdApi.MessageDocument) {
            setStrategy(new StrategyDocument());
        } else if (content instanceof TdApi.MessageAnimation) {
            setStrategy(new StrategyAnimation());
        }
    }

    public void send(long chatId, TdApi.UpdateNewMessage update) throws IOException {//todo remake on String
        tuneUpStrategy(update.message.content);
        tempDB.checkContent(update);
        tempDB.putIntoDB(update);

        strategy.send(chatId, update);
    }

    public void sendBatch(long chatId, ArrayList<TdApi.UpdateNewMessage> bufferList) throws IOException {
        if (bufferList.size() > 0) {
            Client.getClient().send(new TdApi.SendMessageAlbum(chatId, 0, 0, null, formBatch(bufferList), false), new ResultHandler());
        } else {
            System.out.println("Batch is empty!");
        }

    }

    public TdApi.InputMessageContent[] formBatch(ArrayList<TdApi.UpdateNewMessage> content) throws IOException {
        TdApi.InputMessageContent[] messageContents = new TdApi.InputMessageContent[content.size()];

        for (int i = 0; i < content.size(); i++) {
            tuneUpStrategy(content.get(i).message.content);
            tempDB.checkContent(content.get(i));
            tempDB.putIntoDB(content.get(i));

            messageContents[i] = strategy.getInputMessageContent(content.get(i));
        }

        return messageContents;
    }

    public void sendExceptionToAuthor(Exception exception, String from) throws IOException {
        long debugChatId = -1001611624929L;
        LocalDateTime time = LocalDateTime.now().plusHours(3);
        String timeFormatted = time.getHour() + ":" + time.getMinute() + ":" + time.getSecond() + ": ";

        TdApi.FormattedText text = new TdApi.FormattedText(timeFormatted + from + "\n" + exception.getMessage(), null);
        Client.getClient().send(new TdApi.SendMessage(debugChatId, 0, 0, null, null, new TdApi.InputMessageText(text, true, true)), new ResultHandler());
    }

}
