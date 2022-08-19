package it.tdlight.client;

import it.tdlight.common.Init;
import it.tdlight.common.utils.CantLoadLibrary;
import it.tdlight.jni.TdApi;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import strategy.Sender;

/**
 * Example class for TDLight Java
 * <p>
 * The documentation of the TDLight functions can be found here: https://tdlight-team.github.io/tdlight-docs
 */
public final class Client {

    /**
     * Admin user id, used by the stop command example
     */
    private static final TdApi.MessageSender ADMIN_ID = new TdApi.MessageSenderUser(769557804);

    private static final Sender sender = new Sender();
    private static SimpleTelegramClient client;

    public static SimpleTelegramClient getClient() {
        return client;
    }

    private static final Map<String, Long> channelsFromWar = new HashMap<>();
    private static final Map<String, Long> channelsFromProgramming = new HashMap<>();
    private static final Map<String, Long> testChannelForDebuggingFROM = new HashMap<>();
    private static final Map<String, Long> testChannelForDebuggingTO = new HashMap<>();
    private static final Map<String, Long> channelsTo = new HashMap<>();
    private static Long lastMessageAlbumId;
    private static final ArrayList<TdApi.UpdateNewMessage> bufferList = new ArrayList<>();


    static {
        channelsFromWar.put("УНИАН", -1001105313000L);
        channelsFromWar.put("Perepichka NEWS", -1001278252976L);
        channelsFromWar.put("NEXTA Live", -1001413275904L);
        channelsFromWar.put("AlarmMap", -1001505028797L);

        channelsFromProgramming.put("Senior SQL Developer", -1001468396285L);
        channelsFromProgramming.put("Senior Java Developer", -1001529589156L);
        channelsFromProgramming.put("IT CHEF: Новости, кибербезопасность", -1001172380872L);
        channelsFromProgramming.put("Библиотека джависта | Java, Spring, Maven, Hibernate", -1001215056194L);

        testChannelForDebuggingFROM.put("Kate", 890534772L);
        testChannelForDebuggingTO.put("Test", -1001611624929L);

        channelsTo.put("War test", -1001549135330L);
        channelsTo.put("Programming test", -1001761551525L);
    }

    public static void main(String[] args) throws CantLoadLibrary, InterruptedException {

        // Initialize TDLight native libraries
        Init.start();

        // Obtain the API token
        var apiToken = new APIToken(19661841, "54511397e605d7787ca1537ebc5212bb");

        // Configure the client
        var settings = TDLibSettings.create(apiToken);

        // Configure the session directory
        var sessionPath = Paths.get("current-session");
        settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
        settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));

        // Create a client
        client = new SimpleTelegramClient(settings);

        // Configure the authentication info
        var authenticationData = AuthenticationData.consoleLogin();

        // Add an example update handler that prints when the bot is started
        client.addUpdateHandler(TdApi.UpdateAuthorizationState.class, Client::onUpdateAuthorizationState);

        // Add an example update handler that prints every received message
        client.addUpdateHandler(TdApi.UpdateNewMessage.class, Client::onUpdateNewMessage);

        // Add an example command handler that stops the bot
        client.addCommandHandler("stop", new StopCommandHandler());

        // Start the client
        client.start(authenticationData);

        // Wait for exit
        client.waitForExit();
    }

    /**
     * Print new messages received via updateNewMessage
     */
    private static void onUpdateNewMessage(TdApi.UpdateNewMessage update) {
        // Get the message content
        var messageContent = update.message.content;
        long fromChatId = update.message.chatId;

        //Main Logic
//		if(channelsFromWar.containsValue(fromChatId)) {
//			try {
//				sender.send(channelsTo.get("War test"), update);
//			} catch (IOException e) {
//				System.out.println("IOException occurred");
//			}
//		}
//		if(channelsFromProgramming.containsValue(fromChatId)) {
//			try {
//				sender.send(channelsTo.get("Programming test"), update);
//			} catch (IOException e) {
//				System.out.println("IOException occurred");
//			}
//		}

        /////////DEBUG AREA////////////////
        /////////Testing batch sending/////
        if (testChannelForDebuggingFROM.containsValue(fromChatId)) {
            try {
                //Если пришло не пачкой, то отправляем всё
                if (update.message.mediaAlbumId == 0) {
                    sender.sendBatch(testChannelForDebuggingTO.get("Test"), bufferList);
                    bufferList.clear();
                    sender.send(testChannelForDebuggingTO.get("Test"), update);
                //Если пришло пачкой, то накапливаем до тех пор, пока одинаковый альбом_айди
                } else if (bufferList.isEmpty() || lastMessageAlbumId == update.message.mediaAlbumId) {
                    bufferList.add(update);
                    lastMessageAlbumId = update.message.mediaAlbumId;
                //Айди разный - отправляем
                } else {
                    sender.sendBatch(testChannelForDebuggingTO.get("Test"), bufferList);
                    bufferList.clear();
                    bufferList.add(update);
                    lastMessageAlbumId = update.message.mediaAlbumId;
                }
            } catch (Exception e) {
                System.out.println("IOException occurred");
            }
        }
        /////////DEBUG AREA////////////////


        // Get the message text
        String text;
        if (messageContent instanceof TdApi.MessageText messageText) {
            // Get the text of the text message
            text = messageText.text.text;
        } else {
            // We handle only text messages, the other messages will be printed as their type
            text = String.format("(%s)", messageContent.getClass().getSimpleName());
        }

        // Get the chat title
        client.send(new TdApi.GetChat(update.message.chatId), chatIdResult -> {
            // Get the chat response
            var chat = chatIdResult.get();
            // Get the chat name
            var chatName = chat.title;

            // Print the message
            System.out.printf("Received new message from chat %s: %s%n", chatName, text);
        });
    }


    /**
     * Close the bot if the /stop command is sent by the administrator
     */
    private static class StopCommandHandler implements CommandHandler {

        @Override
        public void onCommand(TdApi.Chat chat, TdApi.MessageSender commandSender, String arguments) {
            // Check if the sender is the admin
            if (isAdmin(commandSender)) {
                // Stop the client
                System.out.println("Received stop command. closing...");
                client.sendClose();
            }
        }
    }

    /**
     * Print the bot status
     */
    private static void onUpdateAuthorizationState(TdApi.UpdateAuthorizationState update) {
        var authorizationState = update.authorizationState;
        if (authorizationState instanceof TdApi.AuthorizationStateReady) {
            System.out.println("Logged in");
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosing) {
            System.out.println("Closing...");
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosed) {
            System.out.println("Closed");
        } else if (authorizationState instanceof TdApi.AuthorizationStateLoggingOut) {
            System.out.println("Logging out...");
        }
    }

    /**
     * Check if the command sender is admin
     */
    private static boolean isAdmin(TdApi.MessageSender sender) {
        return sender.equals(ADMIN_ID);
    }

}