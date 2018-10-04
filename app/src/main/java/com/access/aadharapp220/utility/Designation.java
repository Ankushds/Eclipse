package com.access.aadharapp220.utility;

import io.realm.RealmObject;

/**
 * Created by SonuShaikh on 05/10/2017.
 */

public class Designation extends RealmObject {

    private String name, email;

    public String getName() {
        return name;
    }
    public String getEmail(){return email;}
    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email){ this.email = email;}
}
