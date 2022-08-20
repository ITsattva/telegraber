package handler;

import it.tdlight.client.Client;
import it.tdlight.client.GenericResultHandler;
import it.tdlight.client.Result;
import it.tdlight.jni.TdApi;

import java.io.IOException;

public class ResultChatHandler implements GenericResultHandler<TdApi.BasicGroup> {

    @Override
    public void onResult(Result result) {
        System.out.println("START HANDLING");
        TdApi.BasicGroup info = (TdApi.BasicGroup) result.get();
        System.out.println("SUCCUSSFULL!");
        System.out.println("Member count is " + info.memberCount);

    }
}
