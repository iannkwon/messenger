package deo.com.signaldemo.item;

public class Device {
    private String serial;
    private String email;
    private String nickname;

    public Device(){}

    public Device(String serial, String email, String nickname){
        this.serial = serial;
        this.email = email;
        this.nickname = nickname;
    }

    public String getSerial() {
        return serial;
    }


    public String getEmail() {
        return email;
    }


    public String getNickname() {
        return nickname;
    }
}
