package ag.act.exception;

@SuppressWarnings("unused")
public class ImageProcessingException extends ActException {

    public ImageProcessingException(String message) {
        super(message);
    }

    public ImageProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageProcessingException(Integer errorCode, String message) {
        super(errorCode, message);
    }

    public ImageProcessingException(Integer errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
