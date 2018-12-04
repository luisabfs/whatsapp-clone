package br.com.whatsapp.cursoandroid.whatsapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.whatsapp.cursoandroid.whatsapp.R;
import br.com.whatsapp.cursoandroid.whatsapp.configuration.FirebaseConfigurations;
import br.com.whatsapp.cursoandroid.whatsapp.helper.Base64Custom;
import br.com.whatsapp.cursoandroid.whatsapp.helper.Preferences;
import br.com.whatsapp.cursoandroid.whatsapp.model.User;

public class LoginActivity extends AppCompatActivity {
    //layout
    private EditText edit_email;
    private EditText edit_password;
    private Button bt_login;

    //variables
    private String userId;

    //instances
    private User user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference = FirebaseConfigurations.getFirebaseDatabase();
    private DatabaseReference userReference = databaseReference.child("users");
    private ValueEventListener valueEventListenerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        isUserSignedIn();

        edit_email = findViewById(R.id.edit_email);
        edit_password = findViewById(R.id.edit_password);
        bt_login = findViewById(R.id.bt_login);

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields(edit_email, edit_password)){
                    user = new User();
                    user.setEmail(edit_email.getText().toString());
                    user.setPassword(edit_password.getText().toString());
                    validateLogin();
                }
            }
        });

        databaseReference = FirebaseConfigurations.getFirebaseDatabase();
    }

    private void isUserSignedIn(){
        firebaseAuth = FirebaseConfigurations.getFirebaseAuth();

        if(firebaseAuth.getCurrentUser() != null){
            openMainActivity();
        }
    }

    private void validateLogin() {
        firebaseAuth = FirebaseConfigurations.getFirebaseAuth();
        firebaseAuth.signInWithEmailAndPassword(
          user.getEmail(),
          user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    userId = Base64Custom.encodeBase64(user.getEmail());

                    valueEventListenerUser = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);

                            Preferences preferences = new Preferences(LoginActivity.this);
                            preferences.saveUserData(userId, user.getName());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };

                    userReference.child(userId).addListenerForSingleValueEvent(valueEventListenerUser);

                    openMainActivity();
                    Toast.makeText(LoginActivity.this, "User successfully logged!", Toast.LENGTH_LONG).show();
                }else{
                    String exception = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        exception = "The provided e-mail does not exist or has been disabled.";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        exception = "Invalid password. Please, try again.";
                    }catch (Exception e){
                        exception = "Error logging user.";
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this, exception, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void openMainActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void openUserRegister(View view){
        Intent intent = new Intent(LoginActivity.this, UserRegisterActivity.class);
        startActivity(intent);
    }

    //checking if edit texts are filled
    public boolean validateFields(EditText email, EditText password) {
        if (email.getText() == null || password.getText() == null) {
            Toast.makeText(LoginActivity.this ,"Please complete all fields.", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}
