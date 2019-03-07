package deo.com.signaldemo.item;

public class Profile {

    private int url;
    private String email;
    private String nickname;
    private String emoticon;
    private String msg;
    private String time;
    private String strUrl;

    public Profile(){ }

    public Profile(String strUrl, int url, String nickname, String email){
        this.strUrl = strUrl;
        this.url = url;
        this.nickname = nickname;
        this.email = email;
    }

    public Profile(int url, String nickname, String msg, String time){
        this.url = url;
        this.nickname = nickname;
        this.msg = msg;
        this.time = time;
    }
    public Profile(int url, String nickname, String msg, String time, String email){
        this.url = url;
        this.nickname = nickname;
        this.msg = msg;
        this.time = time;
        this.email = email;
    }

    public Profile( String nickname, String emoticon, String time, String email, int url){
        this.url = url;
        this.nickname = nickname;
        this.emoticon = emoticon;
        this.time = time;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public int getUrl() {
        return url;
    }

    public String getNickname() {
        return nickname;
    }

    public String getMsg() {
        return msg;
    }

    public String getTime() {
        return time;
    }

    public String getStrUrl() {
        return strUrl;
    }

    public String getEmoticon() {
        return emoticon;
    }
}
