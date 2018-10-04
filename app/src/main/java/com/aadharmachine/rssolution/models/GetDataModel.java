package com.aadharmachine.rssolution.models;

/**
 * Created by SonuShaikh on 19/12/2017.
 */

public class GetDataModel

    {

        //private variables
        private int _id;
        private String _fname;
        private String _uid;
        private byte[] _fp1;
        private byte[] _fp2;

    public GetDataModel(String holderUid, String holderName, byte[] finger1, byte[] finger2) {
        this._fname = holderName;
        this._uid = holderUid;
        this._fp1 = finger1;
        this._fp2 = finger2;
    }

    public GetDataModel(){

    }


        // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting first name
    public String get_fname(){
        return this._fname;
    }

    // setting first name
    public void setFName(String fname){
        this._fname =fname;
    }

    //getting uid
    public String get_uid(){ return this._uid;
    }

    //setting uid
    public void set_uid(String uid){
        this._uid = uid;
    }

    // getting fp1
    public byte[] get_fp1() { return  this._fp1;}
    //setting fp1
    public void set_fp1(byte[] fp){ this._fp1 = fp; }

    //getting fp2
    public byte[] get_fp2(){ return  this._fp2;}
    //setting fp2
    public  void set_fp2(byte[] fp2){ this._fp2 =fp2;}


}



