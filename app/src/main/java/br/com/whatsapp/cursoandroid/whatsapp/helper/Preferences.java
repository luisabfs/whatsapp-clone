package br.com.whatsapp.cursoandroid.whatsapp.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class Preferences {
    //variables
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    //constants
    private static final String FILE_NAME = "whatsapp.preferences";
    private static final int MODE = 0;

    /*private static final String NAME_KEY = "name";
    private static final String PHONE_KEY = "phone";
    private static final String TOKEN_KEY = "token";*/

    private static final String ID_KEY = "userId";
    private static final String ID_NAME = "userName";

    public Preferences(Context contextParameter){
        context = contextParameter;
        preferences = context.getSharedPreferences(FILE_NAME, MODE);
        editor = preferences.edit();
    }

    public void saveUserData(String userId, String userName) {
        editor.putString(ID_KEY, userId);
        editor.putString(ID_NAME, userName);
        editor.commit();

    }

    public String getUserId(){
        return preferences.getString(ID_KEY, null);
    }

    public String getUserName(){
        return preferences.getString(ID_NAME, null);
    }

   /* public void saveUserPreferences(String name, String phone, String token) {
       editor.putString(NAME_KEY, name);
        editor.putString(PHONE_KEY, phone);
        editor.putString(TOKEN_KEY, token);
        editor.commit();

    }

    public HashMap<String, String> getUserData(){
        HashMap<String, String> userData = new HashMap<>();

        userData.put(NAME_KEY, preferences.getString(NAME_KEY, null));
        userData.put(PHONE_KEY, preferences.getString(PHONE_KEY, null));
        userData.put(TOKEN_KEY, preferences.getString(TOKEN_KEY, null));

        return userData;
    }*/
}
