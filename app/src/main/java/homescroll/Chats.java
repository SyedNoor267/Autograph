package homescroll;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appiapi.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import chatbox.ChatLocalStorage;
import chatbox.ChatObject;
import ibeacon.detection.ChatContact;
import ibeacon.detection.ChatListAdapter;
import ibeacon.detection.ContactsAdapter;

//import com.example.beacon.R;

public class Chats extends Fragment {

    ArrayList<ChatContact> chat;
    RecyclerView chatList;
    View view;

    public static Chats newInstance(){
        return new Chats();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chats, container, false);
        viewDeclaration();
        return view;
    }

    /*
    * Content declaration*/
    private void viewDeclaration(){
        chatList = view.findViewById(R.id.chat_list);

        //if(chat == null) {
            chat = new ArrayList<>();
            chat = getChatList();
        //}

        chatList.setAdapter(new ChatListAdapter(getContext(), chat));
        chatList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public ArrayList<ChatContact> getChatList(){
        ChatLocalStorage chatLocalStorage = new ChatLocalStorage(getContext());
        chatLocalStorage.createTableChatList();
        ArrayList<String> chatList = chatLocalStorage.getDistinctNickname();
        ArrayList<ChatContact> chatContactList = new ArrayList<>();
        for(int index = 0; index<chatList.size(); index++ ) {
            chatContactList.add(chatLocalStorage.getChatByNickname(chatList.get(index)));
            //ArrayList<ChatContact> chatContactList = (ArrayList<ChatContact>) chatList.get("CHAT_CONTACTS");
        }
        return chatContactList;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewDeclaration();
    }
}
