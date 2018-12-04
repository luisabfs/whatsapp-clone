package br.com.whatsapp.cursoandroid.whatsapp.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import br.com.whatsapp.cursoandroid.whatsapp.configuration.FirebaseConfigurations;

public class User {
    private String id;
    private String name;
    private String email;
    private String password;

    public User(){
        // Required empty public constructor
    }

    public void save(){
        DatabaseReference databaseReference = FirebaseConfigurations.getFirebaseDatabase();
        databaseReference.child("users").child(getId()).setValue(this);
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
