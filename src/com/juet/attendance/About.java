package com.juet.attendance;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import android.app.Activity;
import com.juet.attendance.R;

public class About extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);	
		TextView t2 = (TextView) findViewById(R.id.textView6);
	    t2.setMovementMethod(LinkMovementMethod.getInstance());
	}

}
