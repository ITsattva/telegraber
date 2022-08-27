package debug;

import handler.ResultHandler;
import it.tdlight.client.Client;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoggerHandler {
    public static final Logger log = LoggerFactory.getLogger(LoggerHandler.class);


    //todo need refactor this "god" method
    public static void sendLogs(long chatIdTo, String command) {
        if(!command.startsWith("error: ")) {
            log.info("Command \"" + command + "\" was sent");
            switch (command) {
                case "debug":
                    try {
                        File file = new File("log/debug_trace.log");
                        TdApi.LocalFile localFile = new TdApi.LocalFile("log/debug_trace.log", true, true, false, true, 0, 0, 1000);
                        Path pathToLog = Path.of(String.valueOf(file));
                        TdApi.File fileOfTrace = new TdApi.File(1, (int) Files.size(pathToLog), (int) Files.size(pathToLog), localFile, new TdApi.RemoteFile());
                        TdApi.Document document = new TdApi.Document("debug_trace.log", "text/plain", null, null, fileOfTrace);
                        TdApi.FormattedText formattedText = new TdApi.FormattedText("Document with a stack trace: ", null);
                        TdApi.MessageDocument report = new TdApi.MessageDocument(document, formattedText);
                        Client.getClient().send(new TdApi.SendMessage(chatIdTo, 0, 0, null, null, new TdApi.InputMessageDocument(new TdApi.InputFileLocal(pathToLog.toString()), null, true, report.caption)), new ResultHandler());
                        log.info("Log with debug information was sent");
                    } catch (IOException ioException) {
                        log.error(ioException.getMessage());
                    }
                    break;
                case "errors":
                    try {
                        File file = new File("log/error_trace.log");
                        TdApi.LocalFile localFile = new TdApi.LocalFile("log/error_trace.log", true, true, false, true, 0, 0, 1000);
                        Path pathToLog = Path.of(String.valueOf(file));
                        TdApi.File fileOfTrace = new TdApi.File(1, (int) Files.size(pathToLog), (int) Files.size(pathToLog), localFile, new TdApi.RemoteFile());
                        TdApi.Document document = new TdApi.Document("error_trace.log", "text/plain", null, null, fileOfTrace);
                        TdApi.FormattedText formattedText = new TdApi.FormattedText("Errors stack trace", null);
                        TdApi.MessageDocument report = new TdApi.MessageDocument(document, formattedText);
                        Client.getClient().send(new TdApi.SendMessage(chatIdTo, 0, 0, null, null, new TdApi.InputMessageDocument(new TdApi.InputFileLocal(pathToLog.toString()), null, true, report.caption)), new ResultHandler());
                        log.info("Log with information about errors was sent");
                    } catch (IOException ioException) {
                        log.error(ioException.getMessage());
                    }
                    break;
                case "Unknown command":
                    break;
                default:
                    log.info("Unknown command was received");
                    String message = "Unknown command";
                    Client.getClient().send(new TdApi.SendMessage(chatIdTo, 0, 0, null, null, new TdApi.InputMessageText(new TdApi.FormattedText(message, null), true, true)), new ResultHandler());
                    break;
            }
        } else {
            System.out.println("error was received in debug channel");
        }
    }

}
