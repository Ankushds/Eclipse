package com.access.aadharapp220;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.access.aadharapp220.utility.Designation;
import com.access.aadharapp220.utility.RealmHelper;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;
import io.realm.internal.Context;

/**
 * Created by SonuShaikh on 05/10/2017.
 */

public class DesignationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Realm realm;
    ArrayList<String> designationRealmResults;
    ArrayAdapter adapter;
    ListView lv;
    EditText nameEdiText, emailEditText;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designation);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        lv= (ListView) findViewById(R.id.lvPersonNameList);

        //REALM CONFIGURATION
       /* RealmConfiguration config=new RealmConfiguration.Builder(this).build();
        realm=Realm.getInstance(config);
*/
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();

        try {
            realm=Realm.getInstance(realmConfiguration);
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
        designationRealmResults=helper.recive();


        //BIND
        adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,designationRealmResults);
        lv.setAdapter(adapter);

        //ITEM CLICKS
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(DesignationActivity.this,designationRealmResults.get(position),Toast.LENGTH_SHORT).show();
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayInputDialog();
            }
        });
    }

    private void displayInputDialog()

    {
        Dialog d=new Dialog(this);
        d.setTitle("Save Designation Data");
        d.setContentView(R.layout.prompt_designation);

        nameEdiText= (EditText) d.findViewById(R.id.etDesignation);
        emailEditText= (EditText)  d.findViewById(R.id.etDesignationEmail);

        //retrieve
        RealmHelper helper=new RealmHelper(realm);
        designationRealmResults=helper.recive();


        adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,designationRealmResults);
        lv.setAdapter(adapter);

        //ONCLICK
        lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(DesignationActivity.this,designationRealmResults.get(position),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button saveBtn= (Button) d.findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Designation d=new Designation();
                d.setName(nameEdiText.getText().toString());
                d.setEmail(emailEditText.getText().toString());

                //save
                RealmHelper helper=new RealmHelper(realm);
                helper.saveDes(d);

                nameEdiText.setText("");
                emailEditText.setText("");
                //RETRIEVE
                designationRealmResults=helper.recive();
                adapter=new ArrayAdapter(DesignationActivity.this,android.R.layout.simple_list_item_1,designationRealmResults);

                lv.setAdapter(adapter);

            }
        });
        d.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
