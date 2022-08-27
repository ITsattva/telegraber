package util;

import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import strategy.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class PlagiarismHandler {
    public static final Logger log = LoggerFactory.getLogger(PlagiarismHandler.class);
    private Strategy strategy;

    public void tuneUpStrategy(TdApi.MessageContent content) {
        log.info("Tuning up a strategy");
        if (content instanceof TdApi.MessageText) {
            setStrategy(new StrategyText());
        } else if (content instanceof TdApi.MessageAudio) {
            setStrategy(new StrategyAudio());
        } else if (content instanceof TdApi.MessagePhoto) {
            setStrategy(new StrategyPhoto());
        } else if (content instanceof TdApi.MessageVideo) {
            setStrategy(new StrategyVideo());
        } else if (content instanceof TdApi.MessageDocument) {
            setStrategy(new StrategyDocument());
        } else if (content instanceof TdApi.MessageAnimation) {
            setStrategy(new StrategyAnimation());
        }
    }

    public void setStrategy(Strategy strategy) {
        log.info("Setup the strategy");
        this.strategy = strategy;
    }

    public static boolean isTextAlreadyInTheChannel(String one, String two, double powerOfChange) {
        log.info("Check: is text already in the channel?");
        if(one == null || two == null) {
            return false;
        }

        List<String> first = Arrays.stream(one.split(" ")).map(String::toLowerCase).toList();
        List<String> second = Arrays.stream(two.split(" ")).map(String::toLowerCase).toList();
        List<String> yesOrNo1 = new ArrayList<>();
        List<String> yesOrNo2 = new ArrayList<>();

        Iterator<String> iterator = first.listIterator();
        while (iterator.hasNext()) {
            String current = iterator.next();
            if (second.contains(current))
                yesOrNo1.add("yes");
        }

        Iterator<String> iterator2 = second.listIterator();
        while (iterator2.hasNext()) {
            String current = iterator2.next();
            if (first.contains(current))
                yesOrNo2.add("yes");
        }
        long yes1 = yesOrNo1.size();
        long yes2 = yesOrNo2.size();

        return ((((double) first.size()) * powerOfChange < yes1) || (((double) second.size()) * powerOfChange < yes2));
    }

    public String hashMaker(TdApi.UpdateNewMessage content) throws IOException {
        log.info("Generating unique id for media(not pictures)");
        return strategy.getUniqueNumber(content);
    }

    public String textTaker(TdApi.UpdateNewMessage content) throws IOException {
        log.info("Text extraction from the post");
        return strategy.getTextOfContent(content);
    }

    public boolean isText(){
        boolean isText = strategy.getClass().getSimpleName().equals(StrategyText.class.getSimpleName());
        log.info("Is it text? : " + isText);
        return isText;
    }

    public boolean isPicture(){
        boolean isPicture = strategy.getClass().getSimpleName().equals(StrategyPhoto.class.getSimpleName());
        log.info("Is it picture? : " + isPicture);
        return isPicture;
    }
}
