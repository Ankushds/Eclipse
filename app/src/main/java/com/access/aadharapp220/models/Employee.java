package com.access.aadharapp220.models;

/**
 * Created by SonuShaikh on 15/12/2017.
 */

public class Employee {

    String uid, name, finger1, finger2;

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getFinger1() {
        return finger1;
    }

    public String getFinger2() {
        return finger2;
    }

    public void setUid(String Uid) {
        this.uid = Uid;
    }

    public void setName(String Name) {
        this.name = Name;
    }

    public void setFinger1(String Finger1) {
        this.finger1 = Finger1;
    }

    public void setFinger2(String Finger2) {
        this.finger2 = Finger2;
    }
}
