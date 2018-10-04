package com.aadharmachine.rssolution;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.access.aadharapp220.MainActivity;
import com.access.aadharapp220.R;
import com.mantra.fm220.CommonMethod;
import com.mantra.fm220.Constants;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class LoginActivity extends Activity implements View.OnClickListener {


	private EditText etDomain;
	private EditText etLoginName;
	private EditText etUserName;
	private EditText etPassword;

	private String loginUser;
	private String username;
	private String password;

	private Context mContext;
	private SoapObject response;
	private ProgressDialog pDialog;
	private String results;
	private Loadlogindata mLoadlogindata;
	EditText etIp;
	private final String LoginNameText = "9760200500";
	Spinner spin;
	String userIp;

	ProgressDialog progressDialog = null;

	private String file="mydata";
	private String data;

	private String EV;
	private String MV;
	String[] Option = {"Realtime Attendance"};
	String[] Option2 = {"RealtimeDubai","Other"};
	private String flag = " ";
	// String flag1 = "false";

	// MyService service;
	public String URL = "";
	private final String NAMESPACE = "http://tempuri.org/";
	private final String SOAP_ACTION = "http://tempuri.org/Login";
	private final String USER_LOGIN_METHOD_NAME = "Login";
	String str;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		mContext = LoginActivity.this;

		getView();

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// Do something after 20 seconds
				try {
					//updateapp("com.RealtimeBiometrics.realtime", mContext);
					// handler.postDelayed(this, 500000);
				} catch (Exception eee) {
					eee.printStackTrace();

				}
			}
		}, 30000);


		/**
		 * Spinner
		 * */

		try {
			spin = (Spinner) findViewById(R.id.spinner1);
			// Creating adapter for spinner
			ArrayAdapter dataAdapter = new ArrayAdapter(this, R.layout.my_spinner, Option);

			// Drop down layout style - list view with radio button
			dataAdapter.setDropDownViewResource(R.layout.my_drop_down_list);

			ArrayAdapter aa = new ArrayAdapter(this,
					R.layout.my_spinner, Option2);

			// Drop down layout style - list view with radio button
			aa.setDropDownViewResource(R.layout.my_drop_down_list);

			// attaching data adapter to spinner
			str = getIntent().getExtras().getString("value").toString();
			if (str.equalsIgnoreCase("1")) {
				spin.setAdapter(dataAdapter);
				spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent, View arg1,
											   int arg2, long arg3) {
						// TODO Auto-generated method stub

						etDomain = (EditText) findViewById(R.id.et_Domain);
						etLoginName = (EditText) findViewById(R.id.et_login_name);
						etDomain.setVisibility(View.GONE);
						etLoginName.setVisibility(View.VISIBLE);
						if (arg2 == 1) {
							//  etDomain.setText("220.226.210.120");
							// etLoginName.getText().clear();
							//flag = "freeonline";
							etDomain.getText().clear();
							etDomain.setVisibility(View.VISIBLE);
							etLoginName.setVisibility(View.GONE);
							etLoginName.setText(LoginNameText);
							flag = "Other";


						} else if (arg2 == 1) {
							etDomain.getText().clear();
							etDomain.setVisibility(View.VISIBLE);
							etLoginName.setVisibility(View.GONE);
							etLoginName.setText(LoginNameText);
							flag = "Other";
						} else {

							etLoginName.getText().clear();
							flag = "online";
						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});
			}
			if (str.equalsIgnoreCase("2")) {
				spin.setAdapter(aa);
				spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						etDomain = (EditText) findViewById(R.id.et_Domain);
						etLoginName = (EditText) findViewById(R.id.et_login_name);
						etDomain.setVisibility(View.GONE);
						etLoginName.setVisibility(View.VISIBLE);

						if (position == 1) {
							etDomain.getText().clear();
							etDomain.setVisibility(View.VISIBLE);
							etLoginName.setVisibility(View.GONE);
							etLoginName.setText(LoginNameText);
							flag = "Other";
						} else {
							//etDomain.setText("220.226.210.148");
							etLoginName.getText().clear();
							flag = "realtimedubai";
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});
			}
		}catch (NullPointerException npe){
			npe.printStackTrace();
		}
	}

	/**
	 * Update Application Method
	 *
	 * //@param AppID
	 * //@param C
	 *//*
    private void updateapp(String AppID, Context C) {
        // TODO Auto-generated method stub

        try {
            final String FAppID = AppID;
            final Context FC = C;
            String Html = GetHtml1("https://play.google.com/store/apps/details?id="
                    + AppID);
//            Log.w("My HTML TAG", Html);
            Pattern pattern = Pattern
                    .compile("softwareVersion\"> ([^ <]*) +</div");
            Matcher matcher = pattern.matcher(Html);

            matcher.find();

            String MarketVersionName = matcher.group(0).substring(
                    matcher.group(0).indexOf(">") + 1,
                    matcher.group(0).indexOf("<"));

            MV = MarketVersionName.toString().trim();
//            Log.w("My TAG MV Version", MV);
            String ExistingVersionName = C.getPackageManager().getPackageInfo(
                    C.getPackageName(), 0).versionName;
            EV = ExistingVersionName.toString().trim();
//            Log.w("My TAG EV Version E", EV);

            if (!MV.equals(EV)) {

                try {
                    displayNotification();
                }catch(Exception eer){
                    eer.printStackTrace();

                }
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(C);
                alertDialog.setTitle("Update Available");
                alertDialog.setIcon(R.drawable.icon_36);
                alertDialog.setMessage("You are using " + EV + " version\n"
                        + MV
                        + " version is available\nDo you want to Update it?");
                alertDialog.setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                try {
                                    FC.startActivity(new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("market://details?id="
                                                    + FAppID)));
                                } catch (android.content.ActivityNotFoundException anfe) {

                                    FC.startActivity(new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("http://play.google.com/store/apps/details?id="
                                                    + FAppID)));
                                }
                            }
                        });
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                            }
                        });
                alertDialog.show();
            } else {
                // Toast.makeText(mContext, "i m else",
                // Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
        }
    }*/

    /*
     * Notification ALert for update Application
     */
    /*@SuppressLint("NewApi")
    protected void displayNotification() {
        Intent intent;
        try {
            try {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri
                        .parse("market://details?id=com.RealtimeBiometrics.realtime"));

            } catch (Exception e) { // google play app is not installed
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri
                        .parse("https://play.google.com/store/apps/details?id=com.RealtimeBiometrics.realtime"));

            }
            // use System.currentTimeMillis() to have a unique ID for the pending
            // intent
            PendingIntent pIntent = PendingIntent.getActivity(mContext,
                    (int) System.currentTimeMillis(), intent, 0);

            // build notification
            // the addAction re-use the same intent to keep the example short
            Notification n = new Notification.Builder(mContext)
                    .setContentTitle("New Update Alert !")
                    .setContentText(
                            "New Version " + MV + " Realtime Attendance Available")
                    .setSmallIcon(R.drawable.icon_36).setContentIntent(pIntent)
                    .setAutoCancel(true).build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            notificationManager.notify(0, n);


        }catch(Exception ee) {
            ee.printStackTrace();
            Log.i("MY Tag","Exception cause");

        }
    }
*/
   /* @SuppressLint("NewApi")
    private String GetHtml1(String url1) {
        // TODO Auto-generated method stub
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String str = "";
        try {
            URL url = new URL(url1);
            System.out.println(url);
            URLConnection spoof = url.openConnection();
            spoof.setRequestProperty("Employee-Agent",
                    "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    spoof.getInputStream()));
            String strLine = "";
            // Loop through every line in the source
            while ((strLine = in.readLine()) != null) {
                str = str + strLine;

            }
        } catch (Exception e) {
            // Log.w("eeee", e);

        }
        // Log.w("eeee4444", str);
        return str;
    }*/

	private void getView() {

		etLoginName = (EditText) findViewById(R.id.et_login_name);
		etDomain = (EditText) findViewById(R.id.et_Domain);
		etUserName = (EditText) findViewById(R.id.et_user_name);
		etPassword = (EditText) findViewById(R.id.et_password);

		findViewById(R.id.btn_login).setOnClickListener(this);
		;

		etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
										  KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					// do your stuff here
					if (CommonMethod.isOnline(mContext)) {
						mLoadlogindata = new Loadlogindata();
						mLoadlogindata.execute("");

						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(
								etPassword.getWindowToken(), 0);
					} else {
						Toast.makeText(
								mContext,
								"No network connection. Please connect with internet",
								Toast.LENGTH_LONG).show();
					}
				}
				return false;
			}
		});

	}

	@SuppressLint("InlinedApi")
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
           /* case R.id.tv_signup:

                Intent inSignup = new Intent(mContext, SignupActivity.class);
                inSignup.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(inSignup);
                finish();

                break;


            case R.id.tv_demo_login:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                alertDialog.setTitle(" Demo Account");
                alertDialog.setIcon(R.drawable.icon_36);
                alertDialog.setMessage("Login Name: Komal\nUser Name: admin\nPassword: admin");
                alertDialog.setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                            }
                        });
                alertDialog.show();


                break;*/
			case R.id.btn_login:

//            Get the Button String
				loginUser = etLoginName.getText().toString().trim();
				username = etUserName.getText().toString().trim();
				password = etPassword.getText().toString().trim();


				/*CommonMethod.setPrefsData(mContext, Constants.PREF_USER_IP, "");
				CommonMethod.setPrefsData(mContext, Constants.PREF_LOGIN_NAME, "");
				CommonMethod.setPrefsData(mContext, Constants.PREF_USER_NAME, "");
				CommonMethod.setPrefsData(mContext, Constants.PREF_PASSWORD, "");
				CommonMethod.setPrefsData(mContext, Constants.PREF_USER_TYPE, "");
				CommonMethod.setPrefsData(mContext, Constants.PREF_ACCOUNT_STATUS,
						"");
*/
                /*System.out.println("clean url ::  "
                        + "http://"
                        + CommonMethod.getPrefsData(getBaseContext(),
                        Constants.PREF_USER_IP, "") + "/android.asmx?op=Login");
*/
				// if (spin.getSelectedItemPosition() == 0) {
				if (flag == "online") {
					userIp = "unioncloud.ubm.hk";
					//CommonMethod.setPrefsData(mContext, Constants.PREF_USER_IP, userIp);

					/*System.out.println("Online url ::  "
							+ "http://"
							+ CommonMethod.getPrefsData(getBaseContext(), Constants.PREF_USER_IP, "") + "/android.asmx?op=Login");*/
					//
				}
				else if(flag.equals("freeonline")){
					userIp = "220.226.210.120";
					//CommonMethod.setPrefsData(mContext, Constants.PREF_USER_IP, userIp);


					Toast.makeText(getApplicationContext(),
							userIp.toLowerCase(),
							Toast.LENGTH_LONG).show();


					/*System.out.println("Online url ::  "
							+ "http://"
							+ CommonMethod.getPrefsData(getBaseContext(),
							Constants.PREF_USER_IP, "")
							+ "/android.asmx?op=Login");*/
					//
				}
				else  if (flag == "realtimedubai") {
					userIp = "realtimedubai.com";
					/*CommonMethod.setPrefsData(mContext, Constants.PREF_USER_IP,
							userIp);
					// Toast.makeText(mContext,"user ",Toast.LE   NGTH_SHORT).show();
					//  Toast.makeText(mContext,Constants.PREF_USER_IP,Toast.LENGTH_SHORT).show();

					System.out.println("Online url ::  "
							+ "http://"
							+ CommonMethod.getPrefsData(getBaseContext(),
							Constants.PREF_USER_IP, "")
							+ "/android.asmx?op=Login");*/
					//
				}

				// else {
				if (flag.equalsIgnoreCase("other")) {

					etDomain = (EditText) findViewById(R.id.et_Domain);
					etLoginName.setText(LoginNameText);
					userIp = etDomain.getText().toString().trim();
					/*CommonMethod.setPrefsData(mContext, Constants.PREF_USER_IP,
							userIp);*/

					/*System.out.println("Static url ::  "
							+ "http://"
							+ CommonMethod.getPrefsData(getBaseContext(),
							Constants.PREF_USER_IP, "")
							+ "/android.asmx?op=Login");*/
					//
				}

				if (isValidate()) {

					if (CommonMethod.isOnline(mContext)) {
						mLoadlogindata = new Loadlogindata();
						mLoadlogindata.execute("");

						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);

					} else {
						Toast.makeText(
								getApplicationContext(),
								"No network connection. Please connect with internet",
								Toast.LENGTH_LONG).show();
					}

				}

				break;
/*
			case R.id.btn_changecountry:

				data="sonu";

				CommonMethod.setPrefsData(mContext,
						Constants.PREF_COUNTRY_DATA, data);
                *//*try {
                    FileOutputStream fOut = openFileOutput(file, MODE_WORLD_READABLE);
                    fOut.write(data.getBytes());
                    fOut.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }*//*


				//CommonMethod.setPrefsData(mContext, Constants.PREF_ACCOUNT_STATUS, null);
				//CommonMethod.setPrefsData(mContext, Constants.PREF_LOGIN_NAME, null);
				//CommonMethod.setPrefsData(mContext, Constants.PREF_USER_NAME, null);
				//CommonMethod.setPrefsData(mContext, Constants.PREF_PASSWORD, null);
				//CommonMethod.setPrefsData(mContext, Constants.PREF_USER_TYPE, null);
				//CommonMethod.setPrefsData(mContext, Constants.PREF_USER_MANUAL, null);

				Intent inLogin2 = new Intent(LoginActivity.this, SelectCountryActivity.class);
				startActivity(inLogin2);
				this.finish();
				break;*/
		}

	}

	private Boolean isValidate() {

		if (etLoginName.getText().toString().trim().length() == 0) {

			Toast.makeText(mContext, "Please Enter Login Name !",
					Toast.LENGTH_SHORT).show();
			return false;
		} else {

		}
		if (etUserName.getText().toString().trim().length() == 0) {

			Toast.makeText(mContext, "Please Enter Employee Name !",
					Toast.LENGTH_SHORT).show();
			return false;
		} else {

		}
		if (etPassword.getText().toString().trim().length() == 0) {

			Toast.makeText(mContext, "Please Enter Password !",
					Toast.LENGTH_SHORT).show();
			return false;
		} else {

		}
		return true;

	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		this.finish();
		// finish();
	}


	@SuppressLint("InlinedApi")
	private class Loadlogindata extends AsyncTask<String, String, String> {

		/*String URL = "http://"
				+ CommonMethod.getPrefsData(mContext, Constants.PREF_USER_IP,
				"") + "/android.asmx?op=Login";
*/
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(LoginActivity.this,
					AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
			pDialog.setMessage("Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			try {

				// All Values

				Log.i("strUserName", loginUser);
				Log.i("strPassword", password);
				Log.i("Employee", username);
				Log.i("URL", URL);
				Log.i("userIp", userIp);

				SoapObject request = new SoapObject(NAMESPACE,
						USER_LOGIN_METHOD_NAME);
				PropertyInfo fromProp = new PropertyInfo();
				fromProp.setName("loginname");
				fromProp.setValue(loginUser);
				fromProp.setType(String.class);
				request.addProperty(fromProp);

				PropertyInfo toProp = new PropertyInfo();
				toProp.setName("username");
				toProp.setValue(username);
				toProp.setType(String.class);
				request.addProperty(toProp);

				PropertyInfo user1 = new PropertyInfo();
				user1.setName("pwd");
				user1.setValue(password);
				user1.setType(String.class);
				request.addProperty(user1);

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.dotNet = true;
				envelope.setOutputSoapObject(request);
				/*HttpTransportSE androidHttpTransport = new HttpTransportSE(
						//"http://" + CommonMethod.getPrefsData(mContext, Constants.PREF_USER_IP, "") + "/android.asmx?op=Login");
				androidHttpTransport.debug = true;
				androidHttpTransport.call(SOAP_ACTION, envelope);
				System.out.println("SOAP_ACTION"
						+ SOAP_ACTION);*/
//                 SoapObject result = (SoapObject)envelope.bodyIn;

				response = (SoapObject) envelope.getResponse();
				System.out.println("string"
						+ response.getProperty(0).toString());
				System.out.println("logintype"
						+ response.getProperty(1).toString());
				System.out.println("strign"
						+ response.getProperty(2).toString());

				// code Here For Display Name
				results = response.toString();

			} catch (Exception e) {
				e.printStackTrace();

			}
			return results;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			try {
				pDialog.dismiss();
				System.out.println("RESULTS..... " + result);


				if (!response.toString().equalsIgnoreCase("")) {

					String strStatus = response.getProperty(0).toString();
					String strType = response.getProperty(1).toString();
					String strSt = response.getProperty(2).toString();
					String StrManulPunch = response.getProperty(3).toString();
//String StrManulPunch = "0";
					String strAccount = "0";
/*
					CommonMethod
							.setPrefsData(mContext, Constants.PREF_DIsplayname,
									strSt.toString().trim());




					CommonMethod
							.setPrefsData(mContext, Constants.PREF_DIsplayname,
									strSt.toString().trim());*/

					if (strType.equalsIgnoreCase("admin")) {

						Intent inLogin = new Intent(mContext, MainActivity.class);

						inLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

						/*CommonMethod.setPrefsData(mContext,
								Constants.PREF_USER_TYPE, strType);
						CommonMethod.setPrefsData(mContext,
								Constants.PREF_LOGIN_NAME, etLoginName
										.getText().toString().trim());
						CommonMethod.setPrefsData(mContext,
								Constants.PREF_USER_NAME, etUserName.getText()
										.toString().trim());
						CommonMethod.setPrefsData(mContext,
								Constants.PREF_PASSWORD, etPassword.getText()
										.toString().trim());
						CommonMethod.setPrefsData(mContext,
								Constants.PREF_USER_MANUAL, StrManulPunch);*/

						startActivity(inLogin);
						finish();

					} else if (strType.equalsIgnoreCase("Emp")) {
						Intent inLogin = new Intent(mContext,
								MainActivity.class);

						inLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

						/*CommonMethod.setPrefsData(mContext,
								Constants.PREF_USER_TYPE, strType);

						CommonMethod.setPrefsData(mContext,
								Constants.PREF_LOGIN_NAME, etLoginName
										.getText().toString().trim());
						CommonMethod.setPrefsData(mContext,
								Constants.PREF_USER_NAME, etUserName.getText()
										.toString().trim());
						CommonMethod.setPrefsData(mContext,
								Constants.PREF_PASSWORD, etPassword.getText()
										.toString().trim());*/
						startActivity(inLogin);
						finish();
					} else if (strType.equalsIgnoreCase("no")) {

						Toast.makeText(
								mContext,
								"Invalid LoginName or UserName or Password ! Try Another",
								Toast.LENGTH_SHORT).show();
					}

				} else {

					Toast.makeText(
							mContext,
							"Invalid LoginName or UserName or Password ! Try Another",
							Toast.LENGTH_SHORT).show();
				}

			} catch (NullPointerException e) {

				Toast.makeText(getApplicationContext(),
						"Something went wrong. Please try again.",
						Toast.LENGTH_LONG).show();

			}catch (Exception ff){
				ff.printStackTrace();
				Toast.makeText(getApplicationContext(),
						"Please try again..",
						Toast.LENGTH_LONG).show();
			}


		}

	}

}

