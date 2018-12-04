package br.com.whatsapp.cursoandroid.whatsapp.helper;

import android.util.Base64;

public class Base64Custom {

    public static String encodeBase64(String text){
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");
    }

    public static  String decodeBase64(String encondedText){
        return new String(Base64.decode(encondedText, Base64.DEFAULT));
    }
}
