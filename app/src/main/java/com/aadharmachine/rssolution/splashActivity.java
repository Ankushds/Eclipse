package com.aadharmachine.rssolution;

import java.util.Timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import com.aadharmachine.rssolution.models.LoadingTask;
import com.aadharmachine.rssolutions.Gps.GPSTracker;
import com.access.aadharapp220.MainActivity;
import com.access.aadharapp220.R;
import com.mantra.fm220.CommonMethod;
import com.mantra.fm220.Constants;
import com.mantra.fm220.MasterActivity;


public class splashActivity extends Activity implements LoadingTask.LoadingTaskFinishedListener {

	private Context mContext;
	//private GoogleApiClient googleApiClient;

	final Context context = this;


	GPSTracker gps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		mContext = splashActivity.this;


	}


	private void checkGpsStatus() {

		SeekBar sp = (SeekBar) findViewById(R.id.seekBar1);
		new LoadingTask(sp, this).execute("");

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		checkGpsStatus();
//        gpscheckHardware();


	}


	// This is the callback for when your async task has finished
	@Override
	public void onTaskFinished() {
		completeSplash();
	}

	private void completeSplash() {
		try {
			startApp();
			// Don't forget to finish this Splash Activity so the user
			// can't return to it!
		}catch (Exception err){
			err.printStackTrace();
			Toast.makeText(mContext, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
		}
	}

	private void startApp(){

		String domain = CommonMethod.getPrefsData(mContext, Constants.DOMAIN_NAME, "");

		try{

			if (TextUtils.isEmpty(domain)){
				Intent intent = new Intent(splashActivity.this, DomainActivity.class);
				startActivity(intent);
				finish();
			}else {
				Intent intent = new Intent(splashActivity.this, AttendanceActivity.class);
				startActivity(intent);
				this.finish();
			}
		}catch(Exception e){
			e.printStackTrace();
		}


	}

	/*private void startApp() {
		gps = new GPSTracker(mContext);

		String strLoginType = CommonMethod.getPrefsData(mContext,
				Constants.PREF_USER_TYPE, "");

		if (strLoginType.equalsIgnoreCase("admin")) {

			Intent intent = new Intent(mContext,
					AdminBoardActivity.class);
			startActivity(intent);
			finish();

		}
		else if (strLoginType.equalsIgnoreCase("Emp")) {

			if (gps.canGetLocation()) {

				Intent intent = new Intent(mContext, UserBoardActivity.class);
				startActivity(intent);
				finish();

			} else {

//                gps.showSettingsAlert();
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);

				// set title
				alertDialogBuilder.setTitle("GPS Setting!");

				// set dialog message
				alertDialogBuilder
						.setMessage("GPS is not enabled, Go to setting enable GPS.")
						.setCancelable(false)
						.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity
								Intent intent = new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								mContext.startActivity(intent);


							}
						}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						Intent intent = new Intent(mContext, UserBoardActivity.class);
						startActivity(intent);
						Toast.makeText(mContext, "Please enable GPS to use mark attendance features.", Toast.LENGTH_SHORT).show();

						finish();

					}
				});

//           create alert iocn
				alertDialogBuilder.setIcon(R.drawable.icon_36);
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
				// show it
				alertDialog.show();
			}

		} else {

			Intent intent = new Intent(mContext, SelectCountryActivity.class);
			startActivity(intent);
			finish();

		}

	}

*/


}
