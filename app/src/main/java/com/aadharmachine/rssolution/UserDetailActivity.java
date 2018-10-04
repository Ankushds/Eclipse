package com.aadharmachine.rssolution;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.access.aadharapp220.R;

import com.access.aadharapp220.models.AdapterDetails;
import com.access.aadharapp220.models.Contact;
import com.access.aadharapp220.models.DatabaseHandler;

public class UserDetailActivity extends AppCompatActivity {


	private DatabaseHandler db;
	private ListView lv;
	private AdapterDetails adapterDetails;
	private Contact dataModel;
	private Context mContext;


	ArrayList<String> spinlist;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_details);

		//Instantiate database handler
		db=new DatabaseHandler(this);
		//lv = (ListView) findViewById(R.id.list1);
		//ShowRecords();

	}
	/*//Retrieve data from the database and set to the list view
	private void ShowRecords(){
		final ArrayList<Contact> contacts = new ArrayList<>(db.getAllContact());
		adapterDetails=new AdapterDetails(this, contacts);

		lv.setAdapter(adapterDetails);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				try {

					dataModel = contacts.get(position);

					Intent intent = new Intent(parent.getContext(), UpdateActivity.class);
					intent.putExtra("eid", dataModel.getID());
					intent.putExtra("uid", dataModel.get_uid().toString());
					intent.putExtra("name", dataModel.get_fname().toString());
					intent.putExtra("email", dataModel.get_email().toString());
					intent.putExtra("contact", dataModel.get_mobile().toString());
					intent.putExtra("department", dataModel.get_dept().toString());
					intent.putExtra("designation", dataModel.get_des().toString());
					startActivity(intent);

					//CommonMethod.setPrefsData(mContext, Constants.PREF_USER_ID, String.valueOf(dataModel.getID()).trim());
					Toast.makeText(getApplicationContext(), dataModel.getID(), Toast.LENGTH_SHORT).show();


					Log.e("uid", dataModel.get_uid().toString());
					Log.e("name", dataModel.get_fname().toString());
					Log.e("email", dataModel.get_email().toString());
					Log.e("contact", dataModel.get_mobile().toString());
					Log.e("department", dataModel.get_dept().toString());
					Log.e("designation ", dataModel.get_des().toString());

				}catch (Exception ex){
					ex.printStackTrace();
				}

			}
		});
	}*/

}
