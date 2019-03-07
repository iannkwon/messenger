package deo.com.signaldemo.item;

public class Chat {
    private String friend;
    private String chat;
    private int who;
    private int profile;
    private String email;
    private String time;
    private String emoticon;

    public Chat(String friend, String chat, int who, int profile) {
        this.friend = friend;
        this.chat = chat;
        this.who = who;
        this.profile = profile;
    }
    public Chat(String friend, int profile, String emoticon,int who) {
        this.friend = friend;
        this.emoticon = emoticon;
        this.who = who;
        this.profile = profile;
    }

    public Chat(String friend, String chat, int who, int profile, String email, String time) {
        this.friend = friend;
        this.chat = chat;
        this.who = who;
        this.profile = profile;
        this.email = email;
        this.time = time;
    }

    public Chat(String friend, String chat,String emoticon, int who, int profile, String email, String time) {
        this.friend = friend;
        this.chat = chat;
        this.emoticon = emoticon;
        this.who = who;
        this.profile = profile;
        this.email = email;
        this.time = time;

    }

    public Chat(){
    }

    public int getProfile() {
        return profile;
    }

    public String getFriend() {
        return friend;
    }

    public String getChat() {
        return chat;
    }

    public String getEmoticon() {
        return emoticon;
    }

    public int getWho() {
        return who;
    }

    public String getEmail() {
        return email;
    }

    public String getTime() {
        return time;
    }
}
