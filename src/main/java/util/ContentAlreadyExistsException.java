package util;

public class ContentAlreadyExistsException extends RuntimeException{
    public ContentAlreadyExistsException(String message) {
        super(message);
    }
}
