package com.juet.attendance;



import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.juet.attendance.R;


public class MainActivity extends SherlockFragmentActivity {

	//private FragmentTabHost mTabHost;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
	}

	    @Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			//getSupportMenuInflater().inflate(R.menu.main, menu);
			return true;
		}
	    
	    
	    @Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch(item.getItemId()) {
			
			case R.id.action_settings:
				System.out.println("Pressed");
				try {
					Intent settingsActivity = new Intent(getBaseContext(),SettingsView.class);
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

}
