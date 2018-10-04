package com.access.aadharapp220.utility;

import io.realm.RealmObject;

/**
 * Created by Oclemy on 6/14/2016 for ProgrammingWizards Channel and http://www.camposha.com.
 */
public class Spacecraft extends RealmObject {

    private String name, email, address;

    public String getName() {
        return name;
    }
    public String getEmail(){return email;}
    public String getAddress(){return address;}

    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email){ this.email = email;}
    public void setAddress(String address){ this.address = address;}
}
