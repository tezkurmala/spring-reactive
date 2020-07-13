package tez.reactive.spring.lernreactive.fluxmono;

public class CustomException extends Throwable {
    private String message;
    public CustomException(Throwable e) {
        message = e.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
