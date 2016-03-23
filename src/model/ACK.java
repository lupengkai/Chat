package model;

import java.io.Serializable;

/**
 * Created by tage on 3/23/16.
 */
public class ACK implements Serializable {
    private boolean permit;
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isPermit() {
        return permit;
    }

    public void setPermit(boolean permit) {
        this.permit = permit;
    }
}
