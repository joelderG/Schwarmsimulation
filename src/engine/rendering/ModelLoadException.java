package engine.rendering;

public class ModelLoadException extends Exception {
    public ModelLoadException(String message) {
        super(message);
    }

    public ModelLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}