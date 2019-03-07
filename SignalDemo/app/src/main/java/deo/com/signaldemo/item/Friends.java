package deo.com.signaldemo.item;

public class Friends {
    private String email;
    private String nickname;
    private String statusMsg;
    private int profilePic;

    public Friends(String email, String nickname, String statusMsg, int profilePic){
        this.email = email;
        this.nickname = nickname;
        this.statusMsg = statusMsg;
        this.profilePic = profilePic;
    }

    public Friends(String email, String nickname){
        this.email = email;
        this.nickname = nickname;
    }

    public Friends(){}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public int getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(int profilePic) {
        this.profilePic = profilePic;
    }
}
