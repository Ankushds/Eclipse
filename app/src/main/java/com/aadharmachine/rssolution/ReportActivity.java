package com.aadharmachine.rssolution;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.access.aadharapp220.R;
import com.access.aadharapp220.models.Contact;
import com.access.aadharapp220.models.DatabaseHandler;
import com.access.aadharapp220.models.dataAdapter;


public class ReportActivity extends AppCompatActivity{

		private DatabaseHandler db;
		private ListView lv;
		private dataAdapter data;
		private Contact dataModel;
		private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		//Instantiate database handler
		db=new DatabaseHandler(this);

		//lv = (ListView) findViewById(R.id.list1);
		//ShowRecords();
	}



	//Retrieve data from the database and set to the list view
	/*private void ShowRecords(){
		final ArrayList<Contact> contacts = new ArrayList<>(db.getAllContacts());
		data=new dataAdapter(this, contacts);

		lv.setAdapter(data);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				dataModel = contacts.get(position);
				*//*String emp_id =String.valueOf(dataModel.getID());
				Intent intent=new Intent(ReportActivity.this, UserDetailActivity.class);
				Bundle extras = new Bundle();
				extras.putString("StringVariableName", emp_id.toString());
				intent.putExtras(extras);
				startActivity(intent);*//*
				CommonMethod.setPrefsData(mContext, Constants.PREF_USER_ID, String.valueOf(dataModel.getID()).trim());
				Toast.makeText(getApplicationContext(),String.valueOf(dataModel.getID()), Toast.LENGTH_SHORT).show();
			}
		});
	}
*/
}
