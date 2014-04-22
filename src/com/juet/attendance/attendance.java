package com.juet.attendance;


import android.support.v4.app.FragmentManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

import com.juet.attendance.R;
import com.juet.attendance.AttendanceValues;
import com.juet.attendance.CustomListAdapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockFragment;

import android.view.LayoutInflater;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.TargetApi;
public class attendance extends SherlockFragment {
	Intent starter;
	ListView lv1;
	View v;
	ArrayList<AttendanceValues> results;
	ArrayList<AttendanceValues> image_details;
	SharedPreferences sharedPref;
	String WebUser, WebPass, Institute;
	WebExtractor web;
	ProgressDialog pd;
	static int refreshtype;
	TextView upDate;
	
	//@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getActivity().setContentView(R.layout.activity_attendance);

		setHasOptionsMenu(true);
		

	}


	public void startList() {

		//text = (TextView) v.findViewById(R.id.text);
		image_details = getListData();
		lv1 = (ListView) v.findViewById(R.id.custom_list);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		starter=getActivity().getIntent();
		v= LayoutInflater.from(getActivity()).inflate(R.layout.activity_attendance,
				null);
		startList();
		lv1.setAdapter(new CustomListAdapter(getActivity().getApplicationContext(), image_details));
		if(refreshtype==10)
		lv1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
	
				FragmentManager fm = getFragmentManager();
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
				sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
//		        

				// System.out.println("THRESHOLD="+sharedPref.getString("username", "90"));

				
		        values.putString("T", sharedPref.getString("threshold", "90"));

		        sub.setArguments(values);
		        sub.show(fm, "subject_details");
		        //Toast.makeText(getActivity(), "Selected :" + " " + position + " " + attendanceData, Toast.LENGTH_LONG).show();
			}

		});
		else
			lv1.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> a, View v, int position, long id) {
					Toast.makeText(getActivity(), "Not available when using Quick Refresh", Toast.LENGTH_LONG).show();
				}
			});
			lv1.setClickable(false);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	//@Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.attendance, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {

		case R.id.action_refresh:
			try {
				setCredentials();
				if(WebUser.equals("n/a"))
					Toast.makeText(getActivity(), "Enter Credentials in Settings", Toast.LENGTH_LONG).show();
				else {
					web=new WebExtractor();
					web.execute(WebUser,WebPass,"10",Institute);
					}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
			
		case R.id.action_quickrefresh:
			try {

				setCredentials();
				if(WebUser.equals("n/a"))
					Toast.makeText(getActivity(), "Enter Credentials in Settings", Toast.LENGTH_LONG).show();
				else {
					web=new WebExtractor();
					web.execute(WebUser,WebPass,"20",Institute);

					}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
			
		case R.id.action_settings:
			
			

			// System.out.println("Pressed");

			
			try {
				Intent settingsActivity = new Intent(getActivity().getBaseContext(),SettingsView.class);
				startActivity(settingsActivity);
				//new WebExtractor().execute("121342".toString(),"Homerun".toString());
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}

	}


	public void setCredentials()
	{
		sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
		WebUser=sharedPref.getString("username","n/a");
        WebPass=sharedPref.getString("password","n/a");
        Institute=sharedPref.getString("institute","2");

	}

	//below for list


	private ArrayList<AttendanceValues> getListData() {
		results = new ArrayList<AttendanceValues>();
		AttendanceValues attendanceData;



		try{
			File file = getActivity().getBaseContext().getFileStreamPath("hello_file");
			if(file.exists()){
				

				// System.out.println("YYY");

				
				FileInputStream fos;
				fos=getActivity().openFileInput("hello_file");
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
				upDate=(TextView) v.findViewById(R.id.time);
				
				upDate.setText("Last Updated: "+textDate);
				line=line.split("~")[1];
				

				// System.out.println("Refresh type="+line);

				
				if(line.equals("notsoquick"))
					refreshtype=10;
				else
					refreshtype=20;
				while ((line = br.readLine()) != null) {
			        split=line.split("~");
			        

			        // System.out.println("Split size="+split.length);

			        
			        //

			        // System.out.println(split[0]+" "+split[1]+" "+split[2]+" "+split[3]+" "+split[4]+" ");

			        

			        attendanceData = new AttendanceValues();
			        attendanceData.subject=(split[0].substring(0, split[0].lastIndexOf('-')));
					if(!split[2].equals("N/A")) {
						attendanceData.attendance=split[1];
					}
					else {
						attendanceData.attendance=split[4];
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
					Toast.makeText(getActivity(), "Enter Credentials in Settings", Toast.LENGTH_LONG).show();
				else {
					web=new WebExtractor();
					web.execute(WebUser,WebPass,"10",Institute);

				}
			}
			} catch(Exception e) {

				// System.out.println("e="+e);

			}


		return results;
	}





	//Below for WebExtractor

	private class WebExtractor extends AsyncTask <String, Integer, Void> {


		String data[][]=new String[15][10];
		private HttpsURLConnection conn;
		private final String USER_AGENT = "Mozilla/5.0";
		String user,pwd;
		
		int got=0;
		//int Webrunning=0;

	  public Void doInBackground(String... cred) {
		  	String inst="";
		  	user=cred[0];pwd=cred[1];refreshtype=Integer.parseInt(cred[2]);
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
		    // make sure cookies is turn on
		    CookieHandler.setDefault(new CookieManager());
		    user=cred[0];pwd=cred[1];refreshtype=Integer.parseInt(cred[2]);
//		    String postParams="x=&txtInst=Institute&InstCode=J128&txtuType=Member+Type&UserType=S&txtCode=Enrollment+No&MemberCode=9911103500&DOB=DOB&DATE1=2-4-2014&txtPin=Password%2FPin&Password=Poisingh-123&BTNSubmit=Submit";
		    System.out.println("WUTT");
		    http.sendPost(url, postParams);
		    
		    String result = http.GetPageContent(attendance);

		    System.out.println(result);

		     // System.out.println(result);

		    
		    http.trimmer(result);
			//

			// System.out.println(data[i][5]);

			

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
			    		
					// System.out.println(i+" "+lastClass+" "+lastAbsent+" "+present+" "+absent+" ");

					
				}

		    }


		    got=1;
		    http.printData();
		    }
		    catch(Exception e) {
		    	
		    	
		    	System.out.println("Exception inBkg = "+e);

		    	
		    	if(e.getMessage().equals("https://webkiosk.juet.ac.in/StudentFiles/Academic/null")) {
		    		got=1;
				    http.printData();
		    	}
		    }
		    return null;
		  }


		  private void printData() {
		      for(int i=0;i<15;i++) {
		    //       System.out.print(data[i][0]+"\t");
		    //       System.out.print(data[i][1]+"\t");
		    //       System.out.print(data[i][2]+"\t");
		    //       System.out.print(data[i][3]+"\t");
				  // System.out.print(data[i][4]+"\t");
				  // System.out.print(data[i][5]+"\t");
				  // System.out.print(data[i][6]+"\t");
				  // System.out.print(data[i][7]+"\t");
				  // System.out.print(data[i][8]+"\t");
				  // System.out.println(data[i][9]);
		    //       System.out.println();

		          
		      }

		      
		      DateFormat dateFormat = new SimpleDateFormat("dd MMM");
		      Calendar cal = Calendar.getInstance();
		      String date=""+dateFormat.format(cal.getTime());
		      

				 String FILENAME = "hello_file";
				 FileOutputStream fos;
				try {
					fos = getActivity().getApplicationContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
					

					// System.out.println("This is crazy");

					
					int i=0;String s;
					

					// System.out.println("Refresh Code: "+refreshtype);
					
					
					if(refreshtype==10)
						fos.write((date+"~"+"notsoquick\n").getBytes());
					else if(refreshtype==20)
						fos.write((date+"~"+"reallyquick\n").getBytes());
					while(!data[i][0].isEmpty())
					{
						try {
							data[i][1]=((int)Double.parseDouble(data[i][1]))+"";
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							data[i][2]=((int)Double.parseDouble(data[i][2]))+"";
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							data[i][3]=((int)Double.parseDouble(data[i][3]))+"";
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							data[i][4]=((int)Double.parseDouble(data[i][4]))+"";
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						s=data[i][0]+"~"+data[i][1]+"~"+data[i][2]+"~"+data[i][3]+"~"+data[i][4]+"~"+data[i][6]+"~"+data[i][7]+"~"+data[i][8]+"~"+data[i][9]+"\n";
						 System.out.println("s = "+s);
						fos.write(s.getBytes());
						i++;
					}
					fos.close();
					

					// System.out.println("Done!");

					
				} catch(Exception e){

					 System.out.println("Err : "+e);

				}

		  }
		  private void trimmer(String url)
		  {
			  String result=url.substring(url.indexOf("<tbody>")+8,url.indexOf("</tbody>"));
			  String part="",part2="";
			  for(int i=0;i<15;i++) {
				  try{
					  part=result.substring(result.indexOf("<tr")+16,result.indexOf("</tr>"));
				  
					  // System.out.println(part);
				  
				  }
				  catch(Exception e){

				  	// System.out.println("Error "+e);

				  	break;}
				  
				  //part="<td>PROJECT PART I - 10B19CI791</td>\n	\n<td>&nbsp;</td>\n<td>&nbsp;</td>\n\n<td>&nbsp;</td>\n\n<td>&nbsp;</td>\n\n";

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
					  			// System.out.println("N/A is when i is "+i+j+data[i][0]+data[i][1]+" - "+part2);
					  			data[i][5]="N/A";
					  		}
				  		}
				  		
				  	}
				  	data[i][j]=part2;
				  	//

				  	// System.out.println(part2);

				  	
				  	part=part.substring(part.indexOf("</td")+5,part.length());

				  }
				  result=result.substring(result.indexOf("</tr>")+5,result.length());
				  if(data[i][1].equals("N/A")&&data[i][4].equals("N/A"))
				  		data[i][5]="N/A";
				    data[i][5]=data[i][5].replace("amp;", "");
			  }


			  //here here




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
		    // Send post request
		    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		    wr.writeBytes(postParams);
		    wr.flush();
		    wr.close();
		    //int responseCode = conn.getResponseCode();
		    //pd.setMessage("Logging in...");
		    

		     // System.out.println("\nSending 'POST' request to URL : " + url);

		    
		    

		     // System.out.println("Post parameters : " + postParams);

		    
		    

		    // System.out.println("Response Code : " + responseCode);

		    
		    BufferedReader in =
		             new BufferedReader(new InputStreamReader(conn.getInputStream()));
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
		    

		     // System.out.println("\nSending 'GET' request to URL : " + url);

		    
		    

		    // System.out.println("Response Code : " + responseCode);

		    
		    //pd.setMessage("Fetching data...");

		    BufferedReader in =
		            new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String inputLine;
		    StringBuffer response = new StringBuffer();
		    while ((inputLine = in.readLine()) != null) {
		        response.append(inputLine);
		    }
		    in.close();
		    return response.toString();
		  }
		 public void onPreExecute() {
			 

			 // System.out.println("PREEXECUTE");

			 
			 pd=new ProgressDialog(getActivity());
			 pd.setMessage("Fetching...");
			 pd.setCancelable(false);
			 pd.show();
			 //getActivity().setProgressBarIndeterminateVisibility(Boolean.TRUE);

		 }
		 public void onPostExecute(Void params) {
			 

			 // System.out.println("POSTEXECUTE");

			 
			 try{pd.dismiss();}
			 catch(Exception e){

			 	// System.out.println(e);

			 };

			 //getActivity().setProgressBarIndeterminateVisibility(Boolean.FALSE);
			 try{
			 

			 // System.out.println(""+data.length);

			 
			 if(got==0)
				 Toast.makeText(getActivity().getApplicationContext(), "Connection Error/Wrong Credentials", Toast.LENGTH_LONG).show();
			 else if(android.os.Build.VERSION.SDK_INT>10)
				 reCreate();
			 else
			 {	 getActivity().finish();
				 getActivity().startActivity(starter);
				 getActivity().finish();
			 }}catch(Exception e){

			 	// System.out.println("Recreation "+e);

			 }
		 }
		 @TargetApi(11)
		 public void reCreate() {
			 getActivity().recreate();
		 }
	}
}
