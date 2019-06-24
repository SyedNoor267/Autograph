package ibeacon.detection;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appiapi.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import userinterface.chat.ChattingStart;
import userinterface.helperconfiguration.ConfigurationConstant;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.Holder> {

    private final Context context;
    private final List<ChatContact> mContacts;

    public ChatListAdapter(Context mcontext, List<ChatContact> contacts)
    {
        mContacts = contacts;
        context = mcontext;

    }

    @NonNull
    @Override
    public ChatListAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        LayoutInflater inflater = LayoutInflater.from(context);

        return new ChatListAdapter.Holder(inflater.inflate(R.layout.contact_list_item, parent, false));

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.Holder holder, int i) {
        ChatContact contact = mContacts.get(i);
        TextView textViewChatName = holder.tvChatName;
        TextView textViewLastOnline = holder.tvLastOnline;
        textViewChatName.setText(contact.name);
        String lastOnline = lastDateOnline(contact.date);
        textViewLastOnline.setText( lastOnline );
        ImageView im = holder.ivChatImage;
        Picasso.with(getApplicationContext()).load(contact.image).into(im);
        Button buttonViewChat = holder.buttonViewChat;
        buttonViewChat.setOnClickListener(v -> {
            Intent intent=new Intent(v.getContext(), ChattingStart.class);
            intent.putExtra(ConfigurationConstant.NICKNAME,contact.nickname);
            intent.putExtra(ConfigurationConstant.NAME,contact.name);
            intent.putExtra("image",contact.image);

            v.getContext().startActivity(intent);
        });

    }

    private String lastDateOnline(String dateOnline){
        String lastOnline = dateOnline;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateOnline);
            lastOnline = new SimpleDateFormat("dd-MMM HH:mm").format(date);
        }catch (Exception exc){
            exc.printStackTrace();
        }
        return lastOnline;
    }

    @Override
    public int getItemCount() {

        return mContacts.size();
    }

    public class Holder extends RecyclerView.ViewHolder{

        private final TextView tvChatName;
        private final TextView tvLastOnline;
        private final Button buttonViewChat;
        private final ImageView ivChatImage;

        private Holder(@NonNull View itemView) {

            super(itemView);

            tvChatName = itemView.findViewById(R.id.tv_chat_name);
            tvLastOnline = itemView.findViewById(R.id.tv_last_online);
            buttonViewChat = itemView.findViewById(R.id.button_view_chat);
            ivChatImage = itemView.findViewById(R.id.iv_Chat_Image);
        }
    }
}
