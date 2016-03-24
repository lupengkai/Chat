package model;

import java.io.Serializable;

/**
 * Created by tage on 3/24/16.
 */
public class PrivateMessage extends Message implements Serializable {
    private Meet meet;
    private String destinationName;
    private int pcount;
    private int count;

    public PrivateMessage() {
        setSingle(true);
    }

    public Meet getMeet() {
        return meet;
    }

    public void setMeet(Meet meet) {
        this.meet = meet;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPcount() {
        return pcount;
    }

    public void setPcount(int pcount) {
        this.pcount = pcount;
    }
}
