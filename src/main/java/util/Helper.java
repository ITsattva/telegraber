package util;

import handler.ResultChatHandler;
import handler.ResultHandler;
import handler.ResultMemberHandler;
import it.tdlight.client.Client;
import it.tdlight.jni.TdApi;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

    public static<K, V> K getKeyByValue(Map<K, V> map, V value) {
        return (K) map.entrySet().stream().filter(x -> x.getValue() == value).collect(Collectors.toList()).get(0);
    }



}
