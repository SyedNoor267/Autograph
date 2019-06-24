package chatbox;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import helpers.ChatStructure;
import ibeacon.detection.ChatContact;
import userinterface.helperconfiguration.ConfigurationConstant;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class ChatLocalStorage {
    private String DATABASE_TITLE = "AUTOGRAPH_DB.db";
    private String TABLE_NAME = "CHAT_STORAGE";
    private String CHAT_LIST_TABLE = "CHAT_LIST_TABLE";

    SQLiteDatabase chatDatabase;
    Context context;

    public ChatLocalStorage(Context context){
        this.context = context;
        String directory = Environment.getExternalStorageDirectory()+"/Autograph/Chats/";
        File databaseDirectory = new File(directory);
        if(databaseDirectory.exists() && databaseDirectory.isDirectory()) {
        }else{
            databaseDirectory.mkdirs();
        }
        chatDatabase = context.openOrCreateDatabase(DATABASE_TITLE, Context.MODE_PRIVATE, null);
    }

    public void createTable(){
        chatDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"(iD INTEGER PRIMARY KEY AUTOINCREMENT, nickname VARCHAR NOT NULL, message VARCHAR NOT NULL, messageType VARCHAR NOT NULL, messageTime VARCHAR );");
    }


    public void createTableChatList(){
        chatDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+CHAT_LIST_TABLE+"(iD INTEGER PRIMARY KEY AUTOINCREMENT, nickname VARCHAR NOT NULL, name VARCHAR NOT NULL, image VARCHAR NOT NULL, numberOfMessages VARCHAR, lastOnline VARCHAR );");
    }

    public void storeChat(String nickname , String message , String messageType, String messageTime){
        chatDatabase.execSQL("INSERT INTO "+TABLE_NAME+"( nickname , message , messageType, messageTime ) VALUES ( '"+nickname+"' , '"+message+"' , '"+messageType+"', '"+messageTime+"' )");
    }

    //public ArrayList<ChatObject> loadChat(String nickname){
    public HashMap<String, Object> loadChat(String nickname){
        HashMap<String, Object> chatMap = new HashMap<>();
        ArrayList<ChatObject> chatObjects = new ArrayList<>();
        try {
            Cursor cursor = chatDatabase.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE nickname = '"+nickname+"' ", null);
            int numberOfFields = cursor.getCount();
            cursor.moveToFirst();
            if(cursor.moveToFirst()) {
                do{
                    //String nameId      = cursor.getString(cursor.getColumnIndex("nickname"));
                    String message     = cursor.getString(cursor.getColumnIndex("message"));
                    String messageType = cursor.getString(cursor.getColumnIndex("messageType"));
                    String messageTime = cursor.getString(cursor.getColumnIndex("messageTime"));
                    if(messageType.equalsIgnoreCase(ConfigurationConstant.ME))
                        chatObjects.add(new ChatObject(message, messageType, 0));
                    else
                        chatObjects.add(new ChatObject(message, messageType, 1));
                    Log.e("TTT", message);
                }while ( cursor.moveToNext() );
            }
            chatMap.put("CHATMESSAGES", chatObjects);
            chatMap.put("TOTALMESSAGES", numberOfFields);
        }catch (Exception exc){
            Log.e("TTT", exc.getLocalizedMessage());
        }
        return chatMap;
    }

    public ArrayList<String> getDistinctNickname(){
        ArrayList<String> nicknameList = new ArrayList<>();
        try {
            Cursor cursor = chatDatabase.rawQuery("SELECT DISTINCT( nickname ) FROM "+TABLE_NAME+" WHERE messageType != '"+ConfigurationConstant.ME+"' ", null);
            int numberOfFields = cursor.getCount();
            cursor.moveToFirst();
            if(cursor.moveToFirst()) {
                do{
                    String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
                    nicknameList.add(nickname);
                }while ( cursor.moveToNext() );
            }
        }catch (Exception exc){
            Log.e("TTT", exc.getLocalizedMessage());
        }
        return nicknameList;
    }

    public void storeToChatList( String nickname , String name , String image, String lastOnline, int numberOfMessages ){
        chatDatabase.execSQL("INSERT INTO "+CHAT_LIST_TABLE+"( nickname , name , image, numberOfMessages, lastOnline ) VALUES ( '"+nickname+"' , '"+name+"' , '"+image+"', '"+numberOfMessages+"', '"+lastOnline+"' )");
    }

    public void update( String nickname , String name , String image, String lastOnline, int numberOfMessages ){
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("image", image);
        contentValues.put("numberOfMessages", numberOfMessages);
        contentValues.put("lastOnline", lastOnline);
        chatDatabase.update(CHAT_LIST_TABLE, contentValues, "nickname = '"+nickname+"'", null);

        //chatDatabase.execSQL("UPDATE "+CHAT_LIST_TABLE+" SET name = '"+name+"' AND image = '"+image+"' AND numberOfMessages = '"+numberOfMessages+"' lastOnline = '"+lastOnline+"' WHERE nickname = '"+nickname+"' ", null);
    }

    public int checkChatExists(String nickname){
        ArrayList<String> beaconId = new ArrayList<>();
        int total = 0;
        try {
            Cursor cursor = chatDatabase.rawQuery("SELECT * FROM " + CHAT_LIST_TABLE + " WHERE nickname = '" + nickname + "' ", null);
            cursor.moveToFirst();
            total = cursor.getCount();
        }catch(Exception exc){
            Log.e("TTT", exc.getLocalizedMessage());
        }
        return total;
    }

    public HashMap<String, Object> getChatList(){
        HashMap<String, Object> chatMap = new HashMap<>();
        ArrayList<ChatContact> chatContact = new ArrayList<>();
        try {
            Cursor cursor = chatDatabase.rawQuery("SELECT * FROM "+CHAT_LIST_TABLE, null);
            cursor.moveToFirst();
            int numberOfFields = cursor.getCount();
            if(cursor.moveToFirst()) {
                do{
                    String nickname   = cursor.getString(cursor.getColumnIndex("nickname"));
                    String name       = cursor.getString(cursor.getColumnIndex("name"));
                    String image      = cursor.getString(cursor.getColumnIndex("image"));
                    String lastOnline = cursor.getString(cursor.getColumnIndex("lastOnline"));
                    chatContact.add(new ChatContact(nickname, name, image, lastOnline));
                }while ( cursor.moveToNext() );
            }
            chatMap.put("CHAT_CONTACTS", chatContact);
            chatMap.put("TOTAL_CONTACTS", numberOfFields);
            cursor.close();
        }catch (Exception exc){
            Log.e("TTT", exc.getLocalizedMessage());
        }
        return chatMap;
    }

    public ChatContact getChatByNickname(String nickname){
        Cursor cursor;
        cursor = chatDatabase.rawQuery("SELECT * FROM " + CHAT_LIST_TABLE + " WHERE nickname = '" + nickname + "'", null);
        ChatContact chatContact = null;
        try {
            cursor.moveToFirst();
            int numberOfFields = cursor.getCount();
            String dbNickname = cursor.getString(cursor.getColumnIndex("nickname"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String image = cursor.getString(cursor.getColumnIndex("image"));
            String lastOnline = cursor.getString(cursor.getColumnIndex("lastOnline"));
            chatContact = new ChatContact(dbNickname, name, image, lastOnline);
        }catch (Exception exc){
            Log.e("TTT", exc.getLocalizedMessage());
        }
        cursor.close();

        return chatContact;
    }

    public long numberOfChats(){
        long count = DatabaseUtils.queryNumEntries(chatDatabase, CHAT_LIST_TABLE);
        chatDatabase.close();
        Log.e("TTT", count+" Is the total");
        return count;
    }

}
