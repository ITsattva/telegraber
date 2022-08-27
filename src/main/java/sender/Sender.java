package sender;

import db.MainDB;
import handler.ResultHandlerForBatch;
import handler.ResultHandler;
import handler.ResultImageHashHandler;
import it.tdlight.client.Client;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.MessageAudio;
import it.tdlight.jni.TdApi.MessagePhoto;
import it.tdlight.jni.TdApi.MessageText;
import it.tdlight.jni.TdApi.MessageVideo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import strategy.*;
import util.ContentAlreadyExistsException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Sender {
    private Strategy strategy;
    public static final Logger log = LoggerFactory.getLogger(Sender.class);
    private static final MainDB tempDB = new MainDB();
    private static final ArrayList<TdApi.UpdateNewMessage> tempList = new ArrayList<>();
    private static TdApi.UpdateNewMessage temp;
    private static long chatId;
    private static short currentDownloadsCount;
    private static short neededDownloadsCount = -1;

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public static ArrayList<TdApi.UpdateNewMessage> getTempList() {
        return tempList;
    }

    public static long getChatId() {
        return chatId;
    }

    public void tuneUpStrategy(TdApi.MessageContent content) {
        log.info("Tuning up a strategy");
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
        } else if (content instanceof TdApi.MessagePoll) {
            setStrategy(new StrategyPoll());
        }
    }

    public void send(long chatId, TdApi.UpdateNewMessage update) throws IOException {
        log.info("send method");
        tuneUpStrategy(update.message.content);
        if (isPicture()) {
            handlePicture(chatId, update);
        } else {
            if(tempDB.checkContent(update)){
                tempDB.putIntoDB(update);
                strategy.send(chatId, update);
            }
        }
    }

    public void sendBatch(long chatId, ArrayList<TdApi.UpdateNewMessage> bufferList) throws IOException {
        log.info("sendBatch method");
        tuneUpStrategy(bufferList.get(0).message.content);
        if(isPicture() && !readyForPush()){
            handlePictures(chatId, bufferList);
        } else {
            if (bufferList.size() > 0) {
                log.info("Sending the message album");
                Client.getClient().send(new TdApi.SendMessageAlbum(chatId, 0, 0, null, formBatch(bufferList), false), new ResultHandler());
            } else {
                log.info("Batch is empty!");
            }
        }

    }

    public TdApi.InputMessageContent[] formBatch(ArrayList<TdApi.UpdateNewMessage> content) throws IOException {
        log.info("Trying to form a batch of input messages");

        TdApi.InputMessageContent[] messageContents = new TdApi.InputMessageContent[content.size()];

        for (int i = 0; i < content.size(); i++) {
            tuneUpStrategy(content.get(i).message.content);
            tempDB.checkContent(content.get(i));
            tempDB.putIntoDB(content.get(i));

            messageContents[i] = strategy.getInputMessageContent(content.get(i));
        }

        return messageContents;
    }

    private void handlePicture(long chatId, TdApi.UpdateNewMessage update) throws IOException {
        log.info("handling picture method");
        var content = (TdApi.MessagePhoto) update.message.content;
        TdApi.FormattedText text = content.caption;
        if(!tempDB.checkContent(update)){
            log.error(ContentAlreadyExistsException.class.getSimpleName());
            throw new ContentAlreadyExistsException("Text already in the channel");
        }
        int photoId = 0;
        for (var photo : content.photo.sizes) {
            photoId = photo.photo.id;
        }

        Sender.chatId = chatId;
        temp = update;

        Client.getClient().send(new TdApi.DownloadFile(photoId, 32, 0, 0, true), new ResultImageHashHandler());
    }

    private void handlePictures(long chatId, ArrayList<TdApi.UpdateNewMessage> update) throws IOException {
        log.info("handlePictures method");

        log.info("put all of the updates into static temporary array list");
        tempList.addAll(update);

        log.info("increasing needed download count");
        neededDownloadsCount++;

        this.chatId = chatId;

        for (int i = 0; i < update.size(); i++) {
            var content = (TdApi.MessagePhoto) update.get(i).message.content;
            TdApi.FormattedText text = content.caption;
            if(!tempDB.checkContent(update.get(i))){
                log.error(ContentAlreadyExistsException.class.getSimpleName());
                throw new ContentAlreadyExistsException("Text already in the channel");
            }
            neededDownloadsCount++;

            int photoId = 0;
            for (var photo : content.photo.sizes) {
                photoId = photo.photo.id;
            }

            Client.getClient().send(new TdApi.DownloadFile(photoId, 32, 0, 0, true), new ResultHandlerForBatch());
        }

    }

    public void sendPicture() throws IOException {
        log.info("sendPicture command was received");
        tuneUpStrategy(temp.message.content);

        strategy.send(chatId, temp);
        temp = null;
    }

    public void sendPictures() throws IOException {
        log.info("sendPictures command was given");
        strategy.sendBatch(chatId, tempList);
        tempList.clear();
    }

    public static void push(){

    }
    public boolean isPicture() {
        log.info("check in the isPicture() method");
        boolean isPicture = strategy.getClass().getSimpleName().equals(StrategyPhoto.class.getSimpleName());
        System.out.println(isPicture ? "It's a picture" : "It's not a picture");
        return isPicture;
    }

    public void sendExceptionToAuthor(Exception exception, String from) throws IOException {
        log.error("An Exception has been occurred! Sending it to the debug channel");

        long debugChatId = -1001611624929L;
        LocalDateTime time = LocalDateTime.now().plusHours(3);
        String timeFormatted = "error: " + time.getHour() + ":" + time.getMinute() + ":" + time.getSecond() + ": ";

        TdApi.FormattedText text = new TdApi.FormattedText(timeFormatted + from + "\n" + exception.getMessage(), null);
        Client.getClient().send(new TdApi.SendMessage(debugChatId, 0, 0, null, null, new TdApi.InputMessageText(text, true, true)), new ResultHandler());
    }

    public static void plusOneDownload() {
        log.info("Increasing count of downloads: " + currentDownloadsCount + "+1");
        Sender.currentDownloadsCount += 1;
    }
    
    public static boolean readyForPush(){
        log.info("Checking downloads, current: " + currentDownloadsCount + ", needed: " + neededDownloadsCount);
        return currentDownloadsCount == neededDownloadsCount;
    }

    public static void restartCounters(){
        log.info("Restarting counters of downloads...");
        currentDownloadsCount = 0;
        neededDownloadsCount = -1;
    }

}
