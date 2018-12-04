package br.com.whatsapp.cursoandroid.whatsapp.model;

public class Chat {
    private String idUser;
    private String name;
    private String message;

    public Chat(){
        // Required empty public constructor
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
