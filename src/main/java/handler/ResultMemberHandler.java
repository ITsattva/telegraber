package handler;

import it.tdlight.client.GenericResultHandler;
import it.tdlight.client.Result;
import it.tdlight.jni.TdApi;

public class ResultMemberHandler implements GenericResultHandler {

    @Override
    public void onResult(Result result) {
        TdApi.ChatMembers chatMembers = (TdApi.ChatMembers) result.get();
        TdApi.ChatMember[] members = chatMembers.members;
        for(TdApi.ChatMember member : members) {
            System.out.println(member.memberId);
        }
    }
}
