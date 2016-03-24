package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by tage on 3/23/16.
 */
public class Message implements Serializable{

    private boolean single = false;
    private Type type;
    private String username;
    private String message;
    private String password;
    private Date date;
    private String[] userOnline;

    public boolean isSingle() {
        return single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String[] getUserOnline() {
        return userOnline;
    }

    public void setUserOnline(String[] userOnline) {
        this.userOnline = userOnline;
    }

    public String toString() {
        return "Date: " + date + "\n" + username +": " + message;
    }


}


