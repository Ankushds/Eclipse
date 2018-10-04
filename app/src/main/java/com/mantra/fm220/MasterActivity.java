package com.mantra.fm220;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.access.aadharapp220.DepartmentActivity;
import com.access.aadharapp220.DesignationActivity;
import com.access.aadharapp220.MainActivity;
import com.access.aadharapp220.R;
import com.aadharmachine.rssolution.AttendanceActivity;
import com.aadharmachine.rssolution.HelpActivity;
import com.aadharmachine.rssolution.ReportActivity;
import com.aadharmachine.rssolution.UserDetailActivity;

public class MasterActivity extends Activity implements OnClickListener {

Button designation, batchManager, userDetails,department,
		enrolluser, attendance, help, report;

	@Override

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_master);

		designation = (Button) findViewById(R.id.designation);
		department = (Button) findViewById(R.id.department);
		userDetails = (Button) findViewById(R.id.empdetails);
		attendance = (Button) findViewById(R.id.attendence);
		help = (Button) findViewById(R.id.helpview);
		report = (Button) findViewById(R.id.report);
		batchManager = (Button) findViewById(R.id.employee);

		designation.setOnClickListener(this);
		department.setOnClickListener(this);
		userDetails.setOnClickListener(this);
		attendance.setOnClickListener(this);
		help.setOnClickListener(this);
		report.setOnClickListener(this);
		batchManager.setOnClickListener(this);


	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

			case R.id.department:

				Intent intent = new Intent(MasterActivity.this, DepartmentActivity.class);
				startActivity(intent);

				break;
			case R.id.designation:

				Intent intent11 = new Intent(MasterActivity.this, DesignationActivity.class);
				startActivity(intent11);
				break;


		case R.id.employee:

			Intent intent1 = new Intent(MasterActivity.this, MainActivity.class);
			startActivity(intent1);

			break;

		case R.id.empdetails:

			Intent intent2 = new Intent (MasterActivity.this, UserDetailActivity.class);
			startActivity(intent2);
			break;

		case R.id.attendence: {

			startActivity(new Intent(MasterActivity.this, AttendanceActivity.class));
		}
			break;
		case R.id.helpview: {
			startActivity(new Intent(MasterActivity.this, HelpActivity.class));

		}
			break;
		case R.id.report: {
			startActivity(new Intent(MasterActivity.this, ReportActivity.class));
		}
			break;

		}

	}

}
