package userinterface.chat;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appiapi.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;


import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.forward.packet.Forwarded;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.mam.element.MamElements;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.rsm.packet.RSMSet;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;


import chatbox.ChatAdapter;
import chatbox.ChatLocalStorage;
import chatbox.ChatObject;
import networklayer.ServiceLayer;
import networklayer.interfaces.ServiceLayerCallback;
import userinterface.MainActivity;
import userinterface.responsemodel.UserImage;
import userinterface.responsemodel.UserResponse;
import userinterface.helperconfiguration.ConfigurationConstant;
import userinterface.helperconfiguration.PreferenceConnector;
import userinterface.helperconfiguration.ProfilePicDecoder;

public class ChattingStart extends Activity implements View.OnClickListener {

    private String usersession,pass,intentimage,nickname,name;
    private RecyclerView recyclerView;
    private EditText e_msg;
    private Button backButton;
    private ArrayList<ChatObject> arrayList;
    private Bitmap chatImage;
    private ChatLocalStorage chatLocalStorage;
    private int totalChat = 0;
    private Thread chatThread;

    private ChatAdapter mChatadapter;
    private AbstractXMPPConnection mConnection;
    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chattingstart);
        Intent intent = getIntent();
        TextView namedisplay = findViewById(R.id.tv1);

        nickname = intent.getStringExtra(ConfigurationConstant.NICKNAME);
        name = intent.getStringExtra(ConfigurationConstant.NAME);

        try {
            if(!intent.getStringExtra("name").equals(null)) {
                intentimage = intent.getStringExtra("image");
            }else{
                String imagedb = "";
                String[] names = name.trim().split(" ");
                for(int count = 0; count<names.length; count++){
                    imagedb = imagedb+names[count].charAt(0)+"";
                    if(count==1){
                        break;
                    }
                }
                String initials = imagedb.toUpperCase();
                chatImage = UserImage.getBitmapFromString(initials, 32.0f, getApplicationContext());
            }

        } catch (Exception e) {
            e.getStackTrace();
        }

        if( arrayList == null ){
            arrayList = new ArrayList<>();
            chatLocalStorage = new ChatLocalStorage(getApplicationContext());
            chatLocalStorage.createTable();
            HashMap chat = chatLocalStorage.loadChat(nickname);
            totalChat = (int) chat.get("TOTALMESSAGES");
            arrayList = (ArrayList<ChatObject>) chat.get("CHATMESSAGES");
        }
        mChatadapter = new ChatAdapter(getApplicationContext(), arrayList);
        namedisplay.setText(name);
        recyclerView = findViewById(R.id.recyclerView);
        Button btnSend = findViewById(R.id.send_btn1);
        e_msg = findViewById(R.id.edit1);
        backButton = findViewById(R.id.back_button);
        ImageView imageView = findViewById(R.id.iv1);
        try {
//            image=PreferenceConnector.readString(this,"img_link",null);
            if (!intentimage.equals(null)) {
                Picasso.with(getApplicationContext()).load(intentimage).into(imageView);
            }else{
                imageView.setImageBitmap( chatImage );
            }

        } catch (Exception e) {
            e.getStackTrace();
        }

        usersession = PreferenceConnector.readString(getApplicationContext(), ConfigurationConstant.nickname, null);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        mLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mChatadapter);
        btnSend.setOnClickListener(this);
        backButton.setOnClickListener(this);

        if( mConnection == null ) {
            ServiceLayer.GetUserByNickname( usersession, getApplicationContext(), new ServiceLayerCallback() {
                @Override
                public void onSuccess(String obj) {
                    Gson gson = new Gson();
                    UserResponse name = gson.fromJson(obj, UserResponse.class);
                    if (name != null) {
                        pass = name.getPassword();
                        setConnection(name.getNickname(), pass);
                    }
                }

                @Override
                public void onFailure(String obj) {

                }
            });
        }

    }



    private Boolean sendMessage(String sender,String messageSend, String entityBareId) {
        Boolean operationStatus = false;
        EntityBareJid jid = null;
        try {
            jid = JidCreate.entityBareFrom(entityBareId);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            operationStatus = false;
        }

        Boolean alive = chatThread.isAlive();

        if( mConnection.isConnected() && chatThread != null ) {

            ChatManager chatManager = ChatManager.getInstanceFor(mConnection);
            Chat chat = chatManager.chatWith(jid);
            Message newMessage = new Message();
            newMessage.setBody(messageSend);
            newMessage.setTo(sender+ ConfigurationConstant.NIPSDB_02_DYNDNS_ORG1);
            //newMessage.setFrom(jid);
            newMessage.setType(Message.Type.chat);
            try {
//                mConnection.sendPacket(newMessage);
                chat.send(newMessage);
                operationStatus = true;
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                operationStatus = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
                operationStatus = false;
            }
        }
        return operationStatus;
    }

    private void setConnection(String user,String pass){
        // Create the configuration for this new connection

        //this function or code given in official documentation give an error in openfire run locally to solve this error
        //first turn off firewall
        //then follow my steps
        if(chatThread == null) {
            chatThread = new Thread() {
                @Override
                public void run() {

                    InetAddress addr = null;
                    try {
                        // inter your ip4address now checking it
                        addr = InetAddress.getByName(ConfigurationConstant.NIPSDB_02_DYNDNS_ORG);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    HostnameVerifier verifier = (hostname, session) -> false;
                    DomainBareJid serviceName = null;
                    try {
                        serviceName = JidCreate.domainBareFrom(ConfigurationConstant.NIPSDB_02_DYNDNS_ORG);
                    } catch (XmppStringprepException e) {
                        e.printStackTrace();
                    }
                    XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()

                            .setUsernameAndPassword(user, pass)
                            .setPort(5222)
                            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                            .setXmppDomain(serviceName).setSendPresence(true)
                            .setHostnameVerifier(verifier)
                            .setHostAddress(addr)
                            .setDebuggerEnabled(true)
                            .build();
                    mConnection = new XMPPTCPConnection(config);


                    try {
                        mConnection.connect();
                        // all this procedure also throws an error if you do not seperate this thread now we seperate thread create
                        mConnection.login();
                        OfflineMessageManager mOfflineMessageManager =
                                new OfflineMessageManager(mConnection);

                        // Get the message size
                        int size = mOfflineMessageManager.getMessageCount();

                        // Load all messages from the storage
                        List<Message> messages = mOfflineMessageManager.getMessages();
                        Presence presence = new Presence(Presence.Type.available);
                        presence.setStatus(ConfigurationConstant.AVAILABLE);
                        mConnection.sendStanza(presence);


                        if (mConnection.isAuthenticated() && mConnection.isConnected()) {
                            //now send message and receive message code here

                            Log.e(TAG, ConfigurationConstant.RUN_AUTH_DONE_AND_CONNECTED_SUCCESSFULLY);
                            ChatManager chatManager = ChatManager.getInstanceFor(mConnection);
                            chatManager.addListener((IncomingChatMessageListener) (from, message, chat) -> {
                                Log.e(TAG, ConfigurationConstant.NEW_MESSAGE_FROM + from + ": " + message.getBody());
                                //Receive message

                                /// Toast.makeText(getApplicationContext(),from.toString()+"@"+message.getBody().toString()+"@"+chat.toString(),Toast.LENGTH_LONG).show();
                                Log.e("Dekho", from.toString() + "@" + message.getBody() + "@" + chat.toString());
                                //                        MessagesData data = new MessagesData("received",message.getBody().toString());
                                //                        mChatView.receive(message.getBody());
                                //                        mMessagesData.add(data);

                                //now update recyler view
                                runOnUiThread(() -> {

                                    //Toast.makeText(getApplicationContext(),chat.toString()+"|"+message.getBody().toString(),Toast.LENGTH_LONG).show();

                                    //this ui thread important otherwise error occur
                                    //message.getBody() yha p recieve hura h
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String dateTime = simpleDateFormat.format(new Date());
                                    chatLocalStorage.storeChat(nickname, message.getBody(), from.toString(), dateTime);

                                    totalChat += 1;
                                    ChatObject object = new ChatObject(message.getBody(), from.toString(), 1);
                                    arrayList.add(object);
                                    mChatadapter.notifyDataSetChanged();
                                    recyclerView.scrollToPosition(mChatadapter.getItemCount() - 1);
                                    saveLastOnline();
                                });
                            });
                            getChatHistoryWithJID(ConfigurationConstant.G_109597334773898103719_NIPSDB_02_DYNDNS_ORG, 50);

                        }

                    } catch (SmackException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            chatThread.start();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_btn1:
                String txt_msg=e_msg.getText().toString();
                if(txt_msg.length() > 0 ){

                    //Entitiy bare jid consits of username to whom you want to send message and domain of server
                    //to check domain of server follow me
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateTime = simpleDateFormat.format(new Date());

                    String sender=nickname+ ConfigurationConstant.NIPSDB_02_DYNDNS_ORG1;
                    Boolean status = sendMessage(usersession,txt_msg,sender);
                    if(status == true) {
                       /* chatLocalStorage = new ChatLocalStorage(getApplicationContext());
                        chatLocalStorage.createTable();*/
                        chatLocalStorage.storeChat( nickname, txt_msg, ConfigurationConstant.ME, dateTime);

                        ChatObject object = new ChatObject(txt_msg, ConfigurationConstant.ME, 0);
                        arrayList.add(object);
                        mChatadapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(mChatadapter.getItemCount() - 1);
                        e_msg.setText("");
                    }else{
                        Toast.makeText(getApplicationContext(), "Connection lost", Toast.LENGTH_SHORT).show();
                    }
                }
                //Toast.makeText(getApplicationContext(),"send",Toast.LENGTH_LONG).show();
                break;
            case R.id.back_button:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        saveLastOnline();
        Presence presence ;
        try {
            Roster roster1 = Roster.getInstanceFor(mConnection);
            Collection<RosterEntry> entries = roster1.getEntries();
            for (RosterEntry entry : entries) {
                presence = new Presence(Presence.Type.unavailable);
                // presence.setPacketID(Packet.ID_NOT_AVAILABLE);
                presence.setFrom(mConnection.getUser());
                presence.setTo(entry.getUser());
                mConnection.sendStanza(presence);
                System.out.println(presence.toXML());
            }
            // To the other client sends the same user stealth
            presence = new Presence(Presence.Type.unavailable);
            // presence.setPacketID(Packet.ID_NOT_AVAILABLE);
            presence.setFrom(mConnection.getUser());
            //presence.setTo(StringUtils.parseBareAddress(Config.conn1.getUser()));
            mConnection.sendPacket(presence);
            Log.e(ConfigurationConstant.STATE, ConfigurationConstant.SET_UP_STEALTH);

        }
        catch (Exception e)
        {
            e.getStackTrace();
        }

    }

    protected void saveLastOnline() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = simpleDateFormat.format(new Date());
        ChatLocalStorage chatLocalStorage = new ChatLocalStorage(getApplicationContext());
        chatLocalStorage.createTableChatList();
        int total = chatLocalStorage.checkChatExists(nickname);
        if(total>0){
            chatLocalStorage.update(nickname, name, intentimage, dateTime, totalChat);
        }else{
            chatLocalStorage.storeToChatList(nickname, name, intentimage, dateTime, totalChat);
        }
    }

    private MamManager.MamQueryResult getArchivedMessages(String jid, int maxResults) {

        MamManager mamManager = MamManager.getInstanceFor(mConnection);
        try {
            DataForm form = new DataForm(DataForm.Type.submit);
            FormField field = new FormField(FormField.FORM_TYPE);
            field.setType(FormField.Type.hidden);
            field.addValue(MamElements.NAMESPACE);
            form.addField(field);

            FormField formField = new FormField(ConfigurationConstant.NIPSDB_02_DYNDNS_ORG);
            formField.addValue(jid);
            form.addField(formField);

            // "" empty string for before
            RSMSet rsmSet = new RSMSet(maxResults, "", RSMSet.PageDirection.before);

            return mamManager.page(form, rsmSet);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private List<ChatMessage> getChatHistoryWithJID(String jid, int maxResults) {
        List<ChatMessage> chatMessageList = new ArrayList<>();
        MamManager.MamQueryResult mamQueryResult = getArchivedMessages(jid, maxResults);
        String userSendTo = ProfilePicDecoder.parseNameFromJID(jid);

        try {
            if (mamQueryResult != null && userSendTo != null) {
                for (Forwarded forwarded : mamQueryResult.forwardedMessages) {
                    if (forwarded.getForwardedStanza() instanceof Message) {
                        Message msg = (Message) forwarded.getForwardedStanza();
                        Log.d(TAG, ConfigurationConstant.ON_CREATE + msg.toString());
//                        Log.d(TAG, "processStanza: " + msg.getFrom() + " Say：" + msg.getBody() + " String length：" + (msg.getBody() != null ? msg.getBody().length() : ""));
                        ChatMessage chatMessage;
                        if (ProfilePicDecoder.parseNameFromJID(msg.getFrom().toString()).equalsIgnoreCase(userSendTo)) {
                            chatMessage = new ChatMessage(msg.getBody(), forwarded.getDelayInformation().getStamp().getTime(), ChatMessage.Type.RECEIVED);
                            Log.d(TAG, ConfigurationConstant.ON_RECIEVED + msg.getBody()+"=="+ forwarded.getDelayInformation().getStamp().getTime()+"=="+ ChatMessage.Type.RECEIVED);

                        } else {
                            chatMessage = new ChatMessage(msg.getBody(), forwarded.getDelayInformation().getStamp().getTime(), ChatMessage.Type.SENT);
                            Log.d(TAG, ConfigurationConstant.ON_SENT + msg.getBody()+"=="+ forwarded.getDelayInformation().getStamp().getTime()+"=="+ ChatMessage.Type.SENT);

                        }
                        chatMessageList.add(chatMessage);

                    }
                }
            } else {
                return chatMessageList;
            }

            return chatMessageList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessageList;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveLastOnline();
    }
}
