package br.com.whatsapp.cursoandroid.whatsapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.whatsapp.cursoandroid.whatsapp.R;
import br.com.whatsapp.cursoandroid.whatsapp.helper.Preferences;
import br.com.whatsapp.cursoandroid.whatsapp.model.Message;

public class MessageAdapter extends ArrayAdapter<Message> {
    private Context context;
    private ArrayList<Message> messages;

    public MessageAdapter(@NonNull Context context, @NonNull ArrayList<Message> objects) {
        super(context, 0, objects);
        this.context = context;
        this.messages = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        if(messages != null){
            Preferences preferences = new Preferences(context);
            String idSender = preferences.getUserId();

            //initializing object for building the view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //retrieving message
            Message message = messages.get(position);

            //building view from XML
            if(idSender.equals(message.getUserId())){
                view = inflater.inflate(R.layout.item_message_right, parent, false);
            }else{
                view = inflater.inflate(R.layout.item_message_left, parent, false);
            }


            //retrieving element for exibition
            TextView messageText = view.findViewById(R.id.tv_message);
            messageText.setText(message.getMessage());

        }

        return view;
    }
}
