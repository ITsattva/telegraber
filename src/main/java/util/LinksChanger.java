package util;

import it.tdlight.jni.TdApi;

public class LinksChanger {

    public static TdApi.FormattedText changeInnerLink(TdApi.FormattedText messageToChange, String newLink){
        TdApi.TextEntity entity = new TdApi.TextEntity(0, newLink.length(), new TdApi.TextEntityTypeTextUrl(newLink));
        TdApi.FormattedText text = new TdApi.FormattedText(messageToChange.text, new TdApi.TextEntity[]{entity}) ;

        return text;
    }
}
