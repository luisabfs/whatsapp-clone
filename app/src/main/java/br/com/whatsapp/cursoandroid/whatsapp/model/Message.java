package br.com.whatsapp.cursoandroid.whatsapp.model;

public class Message {
    private String userId;
    private String message;

    public Message(){
        // Required empty public constructor
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
