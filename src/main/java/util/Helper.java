package util;

import handler.ResultChatHandler;
import handler.ResultHandler;
import handler.ResultMemberHandler;
import it.tdlight.client.Client;
import it.tdlight.jni.TdApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Helper {

    public static void showChatList() {
        try {
            //Client.getClient().send(new TdApi.GetChats(new TdApi.ChatListMain(), 200), new ResultChatHandler());
            System.out.println("---------------------------------------------------------------------");

        } catch (Exception exception) {
            System.out.println("exception has been occurred");
        }
    }

    public static void showMembersOfChat(long chatId) {
        Client.getClient().send(new TdApi.GetSupergroupMembers(chatId, null, 0, 200), new ResultMemberHandler());

        System.out.println("---------------------------------------------------------------------");
    }

    public static void getGroup(long groupId) {
        Client.getClient().send(new TdApi.GetBasicGroup(groupId), new ResultChatHandler());
    }



    public static boolean isStolen(String one, String two, double powerOfChange) {

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

        System.out.println(first.size() + ":size and same words:" + yes1);
        System.out.println(((double) first.size()) * powerOfChange);
        System.out.println(second.size() + ":size and same words:" + yes2);
        return ((((double) first.size()) * powerOfChange < yes1) || (((double) second.size()) * powerOfChange < yes2));
    }

}
