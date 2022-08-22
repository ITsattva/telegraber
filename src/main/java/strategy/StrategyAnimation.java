package strategy;

import handler.ResultHandler;
import it.tdlight.jni.TdApi;

import java.io.IOException;
import java.util.ArrayList;

public class StrategyAnimation implements Strategy{
    @Override
    public void send(long chatId, TdApi.UpdateNewMessage update) throws IOException {
        var content = (TdApi.MessageAnimation) update.message.content;
        TdApi.FormattedText text = content.caption;
        int animationId = content.animation.animation.id;
        int duration = content.animation.duration;
        int width = content.animation.width;
        int height = content.animation.height;

        client.send(new TdApi.SendMessage(chatId, 0, 0, null, null, new TdApi.InputMessageAnimation(new TdApi.InputFileId(animationId), null, null, duration, width, height, text)), new ResultHandler());

    }

    @Override
    public void sendBatch(long chatId, ArrayList<TdApi.UpdateNewMessage> arrayContent) throws IOException {
        TdApi.InputMessageContent[] messageContents = new TdApi.InputMessageContent[arrayContent.size()];
        for (int i = 0; i < arrayContent.size(); i++) {
            var content = (TdApi.MessageAnimation) arrayContent.get(i).message.content;
            TdApi.FormattedText text = content.caption;
            int animationId = content.animation.animation.id;
            int duration = content.animation.duration;
            int width = content.animation.width;
            int height = content.animation.height;
            messageContents[i] = new TdApi.InputMessageAnimation(new TdApi.InputFileId(animationId), null, null, duration, width, height, text);
        }

        client.send(new TdApi.SendMessageAlbum(chatId, 0, 0, null, messageContents, false), new ResultHandler());
    }

    @Override
    public TdApi.InputMessageContent getInputMessageContent(TdApi.UpdateNewMessage message) throws IOException {
        var content = (TdApi.MessageAnimation) message.message.content;
        TdApi.FormattedText text = content.caption;
        int animationId = content.animation.animation.id;
        int duration = content.animation.duration;
        int width = content.animation.width;
        int height = content.animation.height;

        return new TdApi.InputMessageAnimation(new TdApi.InputFileId(animationId), null, null, duration, width, height, text);
    }

    @Override
    public TdApi.File getContentFile(TdApi.UpdateNewMessage content) throws IOException {
        var animation = (TdApi.MessageAnimation) content.message.content;

        return animation.animation.animation;
    }

    @Override
    public String getUniqueNumber(TdApi.UpdateNewMessage content) {
        var animation = (TdApi.MessageAnimation) content.message.content;
        TdApi.File file = animation.animation.animation;

        return String.valueOf(file.size);
    }

    @Override
    public String getTextOfContent(TdApi.UpdateNewMessage content) throws IOException {
        var animation = (TdApi.MessageAnimation) content.message.content;
        String text = animation.caption.text;
        return text;
    }

}
