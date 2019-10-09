package team.lf.firebasestudentapp;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable{
    private String mUserName;
    private String mText;
    private Timestamp mTimestamp;

    public Message(String userName, String text) {
        mUserName = userName;
        mText = text;
        mTimestamp = new Timestamp(new Date());
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public Timestamp getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        mTimestamp = timestamp;
    }
}
