package br.com.whatsapp.cursoandroid.whatsapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import br.com.whatsapp.cursoandroid.whatsapp.R;
import br.com.whatsapp.cursoandroid.whatsapp.configuration.FirebaseConfigurations;
import br.com.whatsapp.cursoandroid.whatsapp.helper.Base64Custom;
import br.com.whatsapp.cursoandroid.whatsapp.helper.Preferences;
import br.com.whatsapp.cursoandroid.whatsapp.model.User;

public class UserRegisterActivity extends AppCompatActivity {
    //layout
    private EditText edit_name;
    private EditText edit_email;
    private EditText edit_password;
    private Button bt_register;

    //instances
    private User user;
    private FirebaseAuth firebaseAuth;

    //constants
    private static final String TAG = "UserRegisterActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        edit_name = findViewById(R.id.edit_name_register);
        edit_email = findViewById(R.id.edit_email_register);
        edit_password = findViewById(R.id.edit_password_register);
        bt_register = findViewById(R.id.bt_register);

        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields(edit_name, edit_email, edit_password)){
                    user = new User();

                    user.setName(edit_name.getText().toString());
                    user.setEmail(edit_email.getText().toString());
                    user.setPassword(edit_password.getText().toString());
                    registerUser();
                }
            }
        });

    }

    private void registerUser() {
        firebaseAuth = FirebaseConfigurations.getFirebaseAuth();
        firebaseAuth.createUserWithEmailAndPassword(
          user.getEmail(),
          user.getPassword()
        ).addOnCompleteListener(UserRegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(UserRegisterActivity.this, "User successfully registered!", Toast.LENGTH_LONG).show();

                    String userId = Base64Custom.encodeBase64(user.getEmail());
                    user.setId(userId);
                    user.save();

                    Preferences preferences = new Preferences(UserRegisterActivity.this);
                    preferences.saveUserData(userId, user.getName());

                    openUserLogin();
                }else{
                    String exception = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        exception = "Weak password. Try using more characters, with letters and numbers.";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        exception = "Invalid e-mail. Please, try another one.";
                    }catch (FirebaseAuthUserCollisionException e){
                        exception = "E-mail already in use.";
                    }catch (Exception e){
                        exception = "Error registering user.";
                        e.printStackTrace();
                    }

                    Toast.makeText(UserRegisterActivity.this, exception, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void openUserLogin() {
        Intent intent = new Intent(UserRegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //checking if edit texts are filled
    public boolean validateFields(EditText name, EditText email, EditText password) {
        if (name.getText() == null || email.getText() == null || password.getText() == null) {
            Toast.makeText(UserRegisterActivity.this ,"Please complete all fields.", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}
