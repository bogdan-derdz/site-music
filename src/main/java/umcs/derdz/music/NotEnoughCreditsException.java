package umcs.derdz.music;

public class NotEnoughCreditsException extends Exception {
    public NotEnoughCreditsException(String message) {
        super(message);
    }
}
