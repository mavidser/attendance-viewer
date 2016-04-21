package com.juet.attendance;


import android.graphics.Color;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuItem;


import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.TargetApi;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class attendance extends ActionBarActivity {
    Menu menu;
	Intent starter;
	ListView listview;
    CustomListAdapter listAdapter;
    View v;
	ArrayList<AttendanceValues> results;
	ArrayList<AttendanceValues> attendance_list;
	SharedPreferences sharedPref;
	String WebUser, WebPass, Institute;
	WebExtractor web;
	ProgressDialog pd;
	static int refreshtype;
	TextView upDate;
    Boolean isUpdating;
	
	//@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        starter=this.getIntent();

        attendance_list = getListData();
        listview = (ListView) findViewById(R.id.custom_list);
        listAdapter = new CustomListAdapter(attendance.this, attendance_list);
        listview.setAdapter(listAdapter);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(attendance.this);

        isUpdating = false;
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                if (refreshtype==10) {
                    Subject sub=new Subject();
                    Bundle values=new Bundle();

                    AttendanceValues subj=results.get(position);
                    values.putString("A", subj.getAttendance());
                    values.putString("l",subj.lec );
                    values.putString("t",subj.tut );
                    values.putString("p",subj.p );
                    values.putString("a",subj.a );
                    values.putString("lC",subj.lastClass );
                    values.putString("lA",subj.lastAbsent );
                    values.putString("T", sharedPref.getString("threshold", "90"));
                    sub.setArguments(values);
                    sub.show(getSupportFragmentManager(), "subject_details");
                } else {
                    Toast.makeText(attendance.this, "Not available when using Quick Refresh", Toast.LENGTH_LONG).show();
                }
            }
        });

        listview.setClickable(false);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("2768CD77652A1C69AB37BE496F436536")
                .build();
        AdView mAdView = (AdView) findViewById(R.id.adView);
        mAdView.loadAd(adRequest);

	}

    private void startRefreshAnimation() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView imageView = (ImageView) inflater.inflate(R.layout.action_refresh, null);

        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        imageView.startAnimation(rotation);

        MenuItem item = menu.findItem(R.id.action_refresh);
        MenuItemCompat.setActionView(item,imageView);

    }

    public void stopRefreshAnimation() {
        MenuItem item = menu.findItem(R.id.action_refresh);
        MenuItemCompat.getActionView(item).clearAnimation();
        MenuItemCompat.setActionView(item,null);
    }

	@Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.attendance, menu);
        this.menu = menu;
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {

		case R.id.action_refresh:
			try {
				setCredentials();
				if(WebUser.equals("n/a"))
					Toast.makeText(this, "Enter Credentials in Settings", Toast.LENGTH_LONG).show();
				else {
					web=new WebExtractor();
					web.execute(WebUser,WebPass,"10",Institute);
                }

			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;

		case R.id.action_quickrefresh:
			try {

				setCredentials();
				if(WebUser.equals("n/a"))
					Toast.makeText(this, "Enter Credentials in Settings", Toast.LENGTH_LONG).show();
				else {
					web=new WebExtractor();
					web.execute(WebUser,WebPass,"20",Institute);

					}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;

		case R.id.action_settings:
			try {
				Intent settingsActivity = new Intent(attendance.this,SettingsView.class);
				startActivity(settingsActivity);
				//new WebExtractor().execute("121342".toString(),"Homerun".toString());

			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}


	public void setCredentials()
	{
		sharedPref = PreferenceManager.getDefaultSharedPreferences(attendance.this);
		WebUser=sharedPref.getString("username","n/a");
        WebPass=sharedPref.getString("password","n/a");
        Institute=sharedPref.getString("institute","2");

	}

	private ArrayList<AttendanceValues> getListData() {
		results = new ArrayList<AttendanceValues>();
		AttendanceValues attendanceData;
		try{
			File file = attendance.this.getFileStreamPath("hello_file");
			if(file.exists()){
				FileInputStream fos;
				fos=this.openFileInput("hello_file");
				BufferedReader br=new BufferedReader(new InputStreamReader(fos));
				String line;String split[]=new String[9];
				line=br.readLine();

				DateFormat dateFormat = new SimpleDateFormat("dd MMM");
			    Calendar cal = Calendar.getInstance();
			    String today=""+dateFormat.format(cal.getTime());
			    cal.add(Calendar.DATE, -1);
			    String yesterday=""+dateFormat.format(cal.getTime());
			    String textDate=line.split("~")[0];
				if(today.equals(textDate))
					textDate="Today";
				else if(yesterday.equals(textDate))
					textDate="Yesterday";
				line=line.split("~")[1];
                this.getSupportActionBar().setTitle("My Attendance");
                this.getSupportActionBar().setSubtitle("Last Updated: "+textDate);
                this.getSupportActionBar().setDisplayShowHomeEnabled(false);

				if(line.equals("notsoquick"))
					refreshtype=10;
				else
					refreshtype=20;
				while ((line = br.readLine()) != null) {
			        split=line.split("~");

			        attendanceData = new AttendanceValues();
			        attendanceData.subject=(split[0].substring(0, split[0].lastIndexOf('-')));
					if(!split[2].equals("N/A")) {
						attendanceData.attendance=split[1];
					}
					else {
						attendanceData.attendance="--";
					}
					attendanceData.lec=split[2];
					attendanceData.tut=split[3];
					attendanceData.p=split[7];
					attendanceData.a=split[8];
					attendanceData.lastClass=split[5];
					attendanceData.lastAbsent=split[6];
					results.add(attendanceData);

			    }
			}
			else {
				setCredentials();
				if(WebUser.equals("n/a"))
					Toast.makeText(this, "Enter Credentials in Settings", Toast.LENGTH_LONG).show();
				else {
					web=new WebExtractor();
					web.execute(WebUser,WebPass,"10",Institute);

				}
			}
			} catch(Exception e) {
				e.printStackTrace();
			}
		return results;
	}

	private class WebExtractor extends AsyncTask <String, Integer, Void> {
		String data[][]=new String[15][10];
		private HttpsURLConnection conn;
		private final String USER_AGENT = "Mozilla/5.0";
		String user,pwd;

		int got=0;

        public Void doInBackground(String... cred) {
            String inst="";
            user=cred[0];pwd=cred[1];refreshtype=Integer.parseInt(cred[2]);

            try {
                user = java.net.URLEncoder.encode(user, "UTF-8");
                pwd = java.net.URLEncoder.encode(pwd, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            String postParams="";

            if(cred[3].equals("1")) {
                inst="jiit";
                postParams="x=&txtInst=Institute&InstCode=J128&txtuType=Member+Type&UserType=S&txtCode=Enrollment+No&MemberCode="+user+"&DOB=DOB&DATE1=2-4-2014&txtPin=Password%2FPin&Password="+pwd+"&BTNSubmit=Submit";

            }
            else if(cred[3].equals("2")) {
                inst="juet";
                postParams="txtInst=Institute&InstCode=JUET&txtuType=Member+Type&UserType=S&txtCode=Enrollment+No&MemberCode="+user+"&txtPin=Password%2FPin&Password="+pwd+"&BTNSubmit=Submit";

            }
            String url = "https://webkiosk."+inst+".ac.in/CommonFiles/UserAction.jsp";
            String attendance = "https://webkiosk."+inst+".ac.in/StudentFiles/Academic/StudentAttendanceList.jsp";
            WebExtractor http = new WebExtractor();
            try {
                CookieHandler.setDefault(new CookieManager());
                user=cred[0];pwd=cred[1];refreshtype=Integer.parseInt(cred[2]);
                http.sendPost(url, postParams);

                String result = http.GetPageContent(attendance);

                http.trimmer(result);
                http.recreateList();

                int num=0;
                for(int i=0;i<15;i++) {
                    if(http.data[i][0]!=null)
                        num++;
                }
                num=(int)Math.floor(80/(num));
                if(refreshtype==10){
                    for(int i=0;i<15;i++) {


                        if(http.data[i][5]!=null && http.data[i][5]!="N/A") {
                            attendance="https://webkiosk."+inst+".ac.in/StudentFiles/Academic/"+http.data[i][5];
                            result = http.GetPageContent(attendance);
                            result=result.substring(result.indexOf("y>")+2,result.indexOf("</tb"));
                            int present,absent,lastClassIndex,lastAbsentIndex;
                            String lastClass,lastAbsent;
                            present=result.split("Present").length-1;
                            absent=result.split("Absent").length-1;
                            lastClassIndex=result.lastIndexOf("am</")>result.lastIndexOf("pm</")?result.lastIndexOf("am</"):result.lastIndexOf("pm</");
                            if(lastClassIndex>=0) {
                                lastClass=result.substring(lastClassIndex-17,lastClassIndex-7);
                            }
                            else
                                lastClass="None";

                            lastAbsentIndex=result.lastIndexOf("Absent");
                            if(lastAbsentIndex>=0) {
                                result=result.substring(0,lastAbsentIndex);
                                lastAbsentIndex=result.lastIndexOf("am</")>result.lastIndexOf("pm</")?result.lastIndexOf("am</"):result.lastIndexOf("pm</");
                                    lastAbsent=result.substring(lastAbsentIndex-17,lastAbsentIndex-7);
                            }
                            else
                                lastAbsent="None";
                                http.data[i][6]=lastClass;
                                http.data[i][7]=lastAbsent;
                                http.data[i][8]=present+"";
                                http.data[i][9]=absent+"";

                        }
                        else {
                            http.data[i][6]="None";
                            http.data[i][7]="None";
                            http.data[i][8]="0";
                            http.data[i][9]="0";
                        }

                        final int i_final = i;
                        final String[][] data_final = http.data;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    attendance_list.get(i_final).lec = data_final[i_final][2];
                                    attendance_list.get(i_final).tut = data_final[i_final][3];
                                    attendance_list.get(i_final).lastClass = data_final[i_final][6];
                                    attendance_list.get(i_final).lastAbsent = data_final[i_final][7];
                                    attendance_list.get(i_final).p = data_final[i_final][8];
                                    attendance_list.get(i_final).a = data_final[i_final][9];

                                    listAdapter.notifyDataSetChanged();

                                    listview.getChildAt(i_final).setBackgroundColor(Color.TRANSPARENT);
                                    listview.getChildAt(i_final).setAlpha((float) 1);
                                    listview.getChildAt(i_final).setClickable(false);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
                got=1;
                http.printData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getSupportActionBar().setSubtitle("Last Updated: Today");
                    }
                });
            }
            catch(Exception e) {
                e.printStackTrace();
                if(e.getMessage().equals("https://webkiosk.juet.ac.in/StudentFiles/Academic/null")) {
                    got=1;
                    http.printData();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getSupportActionBar().setSubtitle("Last Updated: Today");
                        }
                    });
                }
            }
            return null;
        }

        private void recreateList() {
            AttendanceValues temp;
            int i = 0;
            attendance_list.clear();
            while(data[i][0]!=null) {
                Log.d("Data", data[i][0]);
                temp = new AttendanceValues();
                temp.a="0";
                temp.p="0";
                temp.subject=(data[i][0].substring(0, data[i][0].lastIndexOf('-')));
                if(!data[i][2].equals("N/A")) {
                    temp.attendance=data[i][1];
                }
                else {
                    temp.attendance="--";
                }
                temp.lec="0";
                temp.tut="0";
                temp.lastAbsent="None";
                temp.lastClass="None";
                attendance_list.add(temp);
                final int i_final = i;
                runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          listAdapter.notifyDataSetChanged();
                          try {
                              if (refreshtype == 10)
                                  listview.getChildAt(i_final).setAlpha((float) 0.5);
                              else {
                                  listview.getChildAt(i_final).setAlpha((float) 1);
                                  listview.getChildAt(i_final).setBackgroundColor(Color.TRANSPARENT);
                                  listview.getChildAt(i_final).setClickable(false);
                                  getSupportActionBar().setSubtitle("Last Updated: Today");
                              }
                          }catch (Exception e) {
                              e.printStackTrace();
                          }
                      }
                  });

                i++;
            }
        }


        private void printData() {
            DateFormat dateFormat = new SimpleDateFormat("dd MMM");
            Calendar cal = Calendar.getInstance();
            String date=""+dateFormat.format(cal.getTime());
            String FILENAME = "hello_file";
            FileOutputStream fos;
            try {
                fos = attendance.this.openFileOutput(FILENAME, Context.MODE_PRIVATE);
                int i=0;String s;
                if(refreshtype==10)
                    fos.write((date+"~"+"notsoquick\n").getBytes());
                else if(refreshtype==20)
                    fos.write((date+"~"+"reallyquick\n").getBytes());
                while(!data[i][0].isEmpty())
                {
                    try {
                        data[i][1]=((int)Double.parseDouble(data[i][1]))+"";
                    } catch (Exception e) {
//                        e.printStackTrace();
                    }
                    try {
                        data[i][2]=((int)Double.parseDouble(data[i][2]))+"";
                    } catch (Exception e) {
//                        e.printStackTrace();
                    }
                    try {
                        data[i][3]=((int)Double.parseDouble(data[i][3]))+"";
                    } catch (Exception e) {
//                        e.printStackTrace();
                    }
                    try {
                        data[i][4]=((int)Double.parseDouble(data[i][4]))+"";
                    } catch (Exception e) {
//                        e.printStackTrace();
                    }
                    s=data[i][0]+"~"+data[i][1]+"~"+data[i][2]+"~"+data[i][3]+"~"+data[i][4]+"~"+data[i][6]+"~"+data[i][7]+"~"+data[i][8]+"~"+data[i][9]+"\n";
                    fos.write(s.getBytes());
                    i++;
                }
                fos.close();
            } catch(Exception e){
                e.printStackTrace();
            }

        }
        private void trimmer(String url)
        {
            String result=url.substring(url.indexOf("<tbody>")+8,url.indexOf("</tbody>"));
            String part="",part2="";
            for(int i=0;i<15;i++) {
                try{
                    part=result.substring(result.indexOf("<tr")+16,result.indexOf("</tr>"));
                }
                catch(Exception e){
                    break;
                }

                for(int j=0;j<5;j++) {
                    part2=part.substring(part.indexOf("<td")+4,part.indexOf("</td>"));

                    if(part2.equals("&nbsp;")) {
                        part2="N/A";
                    }
                    else if(part2.startsWith("alig")) {
                        part2=part.substring(part.indexOf(">",50)+1,part.indexOf("</"));
                        if(part2.startsWith("<fo")) {
                            part2=part2.substring(part2.indexOf(">")+1,part2.length());
                        }

                        if(j==1 || j==4) {
                            if((j==1 && !part2.equals("N/A")) || (j==4 && (data[i][1].equals("N/A") && !part2.equals("N/A")))) {
                                data[i][5]=part.substring(part.indexOf("\'")+1,part.indexOf("\'>"));
                            }
                            else{
                                data[i][5]="N/A";
                            }
                        }

                    }
                    data[i][j]=part2;
                    part=part.substring(part.indexOf("</td")+5,part.length());
                }
                result=result.substring(result.indexOf("</tr>")+5,result.length());
                if(data[i][1].equals("N/A")&&data[i][4].equals("N/A"))
                    data[i][5]="N/A";
                data[i][5]=data[i][5].replace("amp;", "");
            }
        }

        private void sendPost(String url, String postParams) throws Exception {
            URL obj = new URL(url);

            conn = (HttpsURLConnection) obj.openConnection();
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Host", "webkiosk.juet.ac.in");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            conn.setRequestProperty("Connection", "close");
            conn.setRequestProperty("Referer", "webkiosk.juet.ac.in");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));
            conn.setDoOutput(true);
            conn.setDoInput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }

        private String GetPageContent(String url) throws Exception {
            URL obj = new URL(url);
            conn = (HttpsURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }
		public void onPreExecute() {
            isUpdating = true;
            startRefreshAnimation();
            for(int i=0;i<listview.getCount();i++) {
                listview.getChildAt(i).setBackgroundColor(Color.LTGRAY);
                listview.getChildAt(i).setAlpha((float)0.25);
                listview.getChildAt(i).setClickable(true);
            }

        }
		public void onPostExecute(Void params) {
            isUpdating = false;
            stopRefreshAnimation();

			try{
                if(got==0)
                    Toast.makeText(attendance.this, "Connection Error/Wrong Credentials", Toast.LENGTH_LONG).show();
                else if(android.os.Build.VERSION.SDK_INT>10)
                    reCreate();
                else {
                    finish();
                    startActivity(starter);
                    finish();
			    }
            } catch(Exception e){
                e.printStackTrace();
			}
        }

		@TargetApi(11)
		public void reCreate() {
//            recreate();
        }
	}
}
