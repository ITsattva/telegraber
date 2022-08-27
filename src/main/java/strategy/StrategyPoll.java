package strategy;

import handler.ResultHandler;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class StrategyPoll implements Strategy{
    public static final Logger log = LoggerFactory.getLogger(StrategyPoll.class);

    @Override
    public void send(long chatId, TdApi.UpdateNewMessage update) throws IOException {
        log.info("Sending a content");
        client.send(new TdApi.SendMessage(chatId, 0, 0, null, null, getInputMessageContent(update)), new ResultHandler());
    }

    @Override
    public void sendBatch(long chatId, ArrayList<TdApi.UpdateNewMessage> arrayContent) throws IOException {
        log.info("Sending a batch");
        TdApi.InputMessageContent[] messageContents = new TdApi.InputMessageContent[arrayContent.size()];
        for (int i = 0; i < arrayContent.size(); i++) {
            messageContents[i] = getInputMessageContent(arrayContent.get(i));
        }

        client.send(new TdApi.SendMessageAlbum(chatId, 0, 0, null, messageContents, false), new ResultHandler());
        //todo it has the same logic almost in the all strategies
    }

    @Override
    public TdApi.InputMessageContent getInputMessageContent(TdApi.UpdateNewMessage message) throws IOException {
        log.info("Forming Input message content");
        TdApi.Poll poll = ((TdApi.MessagePoll) message.message.content).poll;
        String question = poll.question;
        String[] options = Arrays.stream(poll.options).map(Objects::toString).toArray(x -> new String[poll.options.length]);//todo i'm not sure that this is wright

        return new TdApi.InputMessagePoll(question, options, poll.isAnonymous, poll.type, poll.openPeriod, poll.closeDate, poll.isClosed);
    }

    @Override
    public TdApi.File getContentFile(TdApi.UpdateNewMessage content) throws IOException {
        log.info("Getting a TdApi.File from content");
        var poll = (TdApi.MessagePoll) content.message.content;

        return null;//todo need to handle in proper way
    }

    @Override
    public String getUniqueNumber(TdApi.UpdateNewMessage content) throws IOException {
        TdApi.Poll poll = ((TdApi.MessagePoll) content.message.content).poll;
        String uniqueNumber = poll.question + " " + poll.options.length;

        return uniqueNumber;
    }

    @Override
    public String getTextOfContent(TdApi.UpdateNewMessage content) throws IOException {
        log.info("Extracting text from content");
        var poll = (TdApi.MessagePoll) content.message.content;

        return poll.poll.question;//todo need to handle in proper way
    }
}
