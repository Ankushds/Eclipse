package com.aadharmachine.rssolution;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.access.aadharapp220.R;
import com.access.aadharapp220.models.DatabaseHandler;
import com.access.aadharapp220.utility.RealmHelper;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * Created by SonuShaikh on 14/10/2017.
 */

public class UpdateActivity extends AppCompatActivity {

    EditText etuid, etname, etemail, etcontact;
    Spinner spdept, spdes;
    Button updateRecord;
    private DatabaseHandler db;
    int eid;

    Realm realm;
    ArrayList<String> spacecrafts;
    ArrayList<String> designationRealmResults;
    ArrayAdapter adapter;
    ArrayAdapter arrayAdapter;
    String suid, sname, semail, scontact, sdept, sdes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        db=new DatabaseHandler(this);

        etuid = (EditText) findViewById(R.id.uid);
        etname = (EditText) findViewById(R.id.name);
        etemail = (EditText) findViewById(R.id.email);
        etcontact = (EditText) findViewById(R.id.contact);
        spdept = (Spinner) findViewById(R.id.dept);
        spdes = (Spinner) findViewById(R.id.des);
        updateRecord = (Button) findViewById(R.id.btnUpdate);

        loaddata();
        //UpdateData();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();

        try {
            realm= Realm.getInstance(realmConfiguration);
            //return realm;
        } catch (RealmMigrationNeededException e){
            try {
                Realm.deleteRealm(realmConfiguration);
                //Realm file has been deleted.
                //return Realm.getInstance(realmConfiguration);
                realm = Realm.getInstance(realmConfiguration);
            } catch (Exception ex){
                throw ex;
                //No Realm file to remove.
            }
        }

        //retrieve
        RealmHelper helper=new RealmHelper(realm);
        spacecrafts=helper.retrieve();


        //BIND
        adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,spacecrafts);
        spdept.setAdapter(adapter);

        //ITEM CLICKS
        spdept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(UpdateActivity.this,spacecrafts.get(position),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        //retrieve
        RealmHelper helper1=new RealmHelper(realm);
        designationRealmResults=helper1.recive();


        //BIND
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,designationRealmResults);
        spdes.setAdapter(arrayAdapter);

        //ITEM CLICKS
        spdes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(UpdateActivity.this,designationRealmResults.get(position),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }


    public void loaddata(){

        //Intent intent = new Intent();
        Bundle bundle = getIntent().getExtras();

        String uid =bundle.getString("uid");
        String name = bundle.getString("name");
        String email = bundle.getString("email");
        String contact = bundle.getString("contact");
        String department = bundle.getString("department");
        String designation = bundle.getString("designation");
        eid = bundle.getInt("eid");

        etuid.setText(uid);
        etname.setText(name);
        etemail.setText(email);
        etcontact.setText(contact);


        Toast.makeText(this, uid, Toast.LENGTH_SHORT).show();


    }

    /*public void UpdateData() {
        updateRecord.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        suid = etuid.getText().toString().trim();
                        sname = etname.getText().toString().trim();
                        semail = etemail.getText().toString().trim();
                        scontact = etcontact.getText().toString().trim();
                        sdept=spdept.getSelectedItem().toString().trim();
                        sdes = spdes.getSelectedItem().toString().trim();
                        boolean isUpdate = db.updateData(eid, suid, sname, semail,scontact,sdept,sdes);
                        if(isUpdate == true)
                            Toast.makeText(UpdateActivity.this,"Data Update",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(UpdateActivity.this,"Data not Updated",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }*/


}
