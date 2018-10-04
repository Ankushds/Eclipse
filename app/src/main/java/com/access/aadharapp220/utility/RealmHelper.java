package com.access.aadharapp220.utility;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Oclemy on 6/14/2016 for ProgrammingWizards Channel and http://www.camposha.com.
 */
public class RealmHelper {

    Realm realm;


    public RealmHelper(Realm myRealm) {
        this.realm = myRealm;
    }

    //WRITE
    public void save(final Spacecraft spacecraft)
    {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Spacecraft s=realm.copyToRealm(spacecraft);

            }
        });
    }


    //READ/RETRIEVE
    public ArrayList<String> retrieve()
    {
        ArrayList<String> spacecraftNames=new ArrayList<>();
        RealmResults<Spacecraft> spacecrafts=realm.where(Spacecraft.class).findAll();

        for(Spacecraft s: spacecrafts)
        {
            spacecraftNames.add(s.getName());
        }

        return spacecraftNames;
    }

    //write

    public void saveDes(final Designation designation){

        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm){

                Designation d = realm.copyToRealm(designation);
            }
        });
    }

    // read

    public ArrayList<String> recive(){
        ArrayList<String> designationNames = new ArrayList<>();
        RealmResults<Designation> designationRealmResults = realm.where(Designation.class).findAll();

        for (Designation d: designationRealmResults)
        {
            designationNames.add(d.getName());
        }
        return designationNames;
    }
}













