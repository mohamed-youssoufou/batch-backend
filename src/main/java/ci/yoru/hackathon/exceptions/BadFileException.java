package ci.yoru.hackathon.exceptions;

public class BadFileException extends RuntimeException {
    public BadFileException(String message) {
        super(message);
    }
}
