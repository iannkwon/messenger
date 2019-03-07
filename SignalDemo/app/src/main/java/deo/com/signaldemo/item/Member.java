package deo.com.signaldemo.item;

public class Member {
    private String email;
    private String pw;
    private String nickname;
    private String date;
    private String serialNum;

    public Member(String email, String pw, String nickname, String date, String serialNum){
        this.email = email;
        this.pw = pw;
        this.nickname = nickname;
        this.date = date;
        this.serialNum = serialNum;
    }
    public Member(){ }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }
}
