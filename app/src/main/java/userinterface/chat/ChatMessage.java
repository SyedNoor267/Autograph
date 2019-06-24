package userinterface.chat;

class ChatMessage {
    String body;
    long time;
    Type rec;

    public ChatMessage(String bodys, long times, Type p2) {
        body = bodys;
        time = times;
        rec= p2;


    }

    enum Type {
        RECEIVED,
        SENT
    }
}
