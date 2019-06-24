package chatbox;



public class ChatObject {
    String msg;
    private String user;
    private int check;

    public ChatObject(String msg, String user, int check) {
        this.msg = msg;
        this.user = user;
        this.check = check;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }
}
