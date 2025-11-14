package ch.supsi.fscli.backend.business.command.business;

public class InvalidCommandException extends RuntimeException {
    public InvalidCommandException(String message) {
        super(message);
    }
}
