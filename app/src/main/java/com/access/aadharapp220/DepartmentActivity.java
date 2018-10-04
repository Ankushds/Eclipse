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
import android.widget.Spinner;
import android.widget.Toast;

import com.access.aadharapp220.utility.RealmHelper;
import com.access.aadharapp220.utility.Spacecraft;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;
import io.realm.internal.Context;

/**
 * Created by SonuShaikh on 05/10/2017.
 */

public class DepartmentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Realm realm;
    ArrayList<String> spacecrafts;
    ArrayAdapter adapter;
    Spinner sp , spinner;
    ListView lv;
    EditText nameEdiText, emailEditText, addressEditText;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department);

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
        spacecrafts=helper.retrieve();


        //BIND
        adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,spacecrafts);
        lv.setAdapter(adapter);

        //ITEM CLICKS
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(DepartmentActivity.this,spacecrafts.get(position),Toast.LENGTH_SHORT).show();
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
        d.setTitle("Save Department Data");
        d.setContentView(R.layout.prompt_dialog);

        nameEdiText= (EditText) d.findViewById(R.id.etDepartmentName);
        emailEditText= (EditText)  d.findViewById(R.id.etDepartmentEmail);
        addressEditText= (EditText) d.findViewById(R.id.etDepartmentAddress);

        //retrieve
        RealmHelper helper=new RealmHelper(realm);
        spacecrafts=helper.retrieve();


        adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,spacecrafts);
        lv.setAdapter(adapter);

        //ONCLICK
        lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(DepartmentActivity.this,spacecrafts.get(position),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button saveBtn= (Button) d.findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Spacecraft s=new Spacecraft();
                s.setName(nameEdiText.getText().toString());
                s.setEmail(emailEditText.getText().toString());
                s.setAddress(addressEditText.getText().toString());

                //save
                RealmHelper helper=new RealmHelper(realm);
                helper.save(s);

                nameEdiText.setText("");
                emailEditText.setText("");
                addressEditText.setText("");
                //RETRIEVE
                spacecrafts=helper.retrieve();
                adapter=new ArrayAdapter(DepartmentActivity.this,android.R.layout.simple_list_item_1,spacecrafts);

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
