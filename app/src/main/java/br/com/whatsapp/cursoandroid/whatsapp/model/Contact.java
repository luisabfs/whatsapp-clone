package br.com.whatsapp.cursoandroid.whatsapp.model;

public class Contact {
    private String userId;
    private String name;
    private String email;

    public Contact(){
        // Required empty public constructor
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
