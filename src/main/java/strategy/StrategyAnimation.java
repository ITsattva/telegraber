package strategy;

import handler.ResultHandler;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class StrategyAnimation implements Strategy{
    public static final Logger log = LoggerFactory.getLogger(StrategyAnimation.class);


    @Override
    public void sendBatch(long chatId, ArrayList<TdApi.UpdateNewMessage> arrayContent) throws IOException {
        log.info("Sending a batch");
        TdApi.InputMessageContent[] messageContents = new TdApi.InputMessageContent[arrayContent.size()];
        for (int i = 0; i < arrayContent.size(); i++) {
            messageContents[i] = getInputMessageContent(arrayContent.get(i));
        }

        client.send(new TdApi.SendMessageAlbum(chatId, 0, 0, null, messageContents, false), new ResultHandler());
    }

    @Override
    public void send(long chatId, TdApi.UpdateNewMessage update) throws IOException {
        log.info("Sending a content");
        client.send(new TdApi.SendMessage(chatId, 0, 0, null, null, getInputMessageContent(update)), new ResultHandler());
    }

    @Override
    public TdApi.InputMessageContent getInputMessageContent(TdApi.UpdateNewMessage message) throws IOException {
        log.info("Forming Input message content");
        var content = (TdApi.MessageAnimation) message.message.content;
        TdApi.FormattedText text = content.caption;
        int animationId = content.animation.animation.id;
        int duration = content.animation.duration;
        int width = content.animation.width;
        int height = content.animation.height;

        log.info("Returning Input message content");
        return new TdApi.InputMessageAnimation(new TdApi.InputFileId(animationId), null, null, duration, width, height, text);
    }

    @Override
    public TdApi.File getContentFile(TdApi.UpdateNewMessage content) throws IOException {
        log.info("Getting a TdApi.File from content");
        var animation = (TdApi.MessageAnimation) content.message.content;

        return animation.animation.animation;
    }

    @Override
    public String getUniqueNumber(TdApi.UpdateNewMessage content) throws IOException {
        log.info("Converting animation size into the unique number");
        TdApi.File file = getContentFile(content);

        return String.valueOf(file.size);
    }

    @Override
    public String getTextOfContent(TdApi.UpdateNewMessage content) throws IOException {
        log.info("Extracting text from content");
        var animation = (TdApi.MessageAnimation) content.message.content;

        return animation.caption.text;
    }

}
