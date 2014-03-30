package com.juet.attendance;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.juet.attendance.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class Subject extends SherlockDialogFragment {
	
	private TextView LT,p,a,lA,lC,nP,nA,s;

	
	public Subject() {
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		//getDialog().requestWindowFeature((int) Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.activity_subject, container);
        getDialog().setTitle("Subject Details");
        Bundle values=getArguments();
        

        System.out.println(values.getString("l"));
        
        LT = (TextView) view.findViewById(R.id.LT);
        if(!(values.getString("l").equals("N/A") && values.getString("t").equals("N/A")))
        	LT.setText("Lec: "+values.getString("l")+"   Tut: "+values.getString("t"));
        else
        	LT.setVisibility(View.GONE);
        p = (TextView) view.findViewById(R.id.p);
        p.setText("Present: "+values.getString("p"));
        a = (TextView) view.findViewById(R.id.a);
        a.setText("Absent: "+values.getString("a"));
        lC = (TextView) view.findViewById(R.id.lC);
        lC.setText("Last Class: "+values.getString("lC"));
        lA = (TextView) view.findViewById(R.id.lA);
        lA.setText("Last Absent: "+values.getString("lA"));
        
        int total=Integer.parseInt(values.getString("p"))+Integer.parseInt(values.getString("a"));
        int present=Integer.parseInt(values.getString("p"));
        nP = (TextView) view.findViewById(R.id.nP);
        nP.setText("On attending next: "+(present+1)*100/(total+1)+"");
        nA = (TextView) view.findViewById(R.id.nA);
        nA.setText("On skipping next: "+(present)*100/(total+1)+"");
        System.out.println(values.getString("T")+"   ");
        String As=values.getString("A");
        if(As.equals("N/A"))
        	As="0";
        int T=Integer.parseInt(values.getString("T"));
        int A=Integer.parseInt(As);
        double sk;
        System.out.println(total+" "+present+" "+T);

        
        if(A<T) 
        {
        	System.out.println((total*T - present*100) +"   "+ (100 - T));
        	sk=(total*T - present*100.0)/(100.0 - T);
        	s = (TextView) view.findViewById(R.id.s);
            s.setText("Classes till "+T+": "+(int)((sk<((int)(sk)+0.01))?sk:sk+1));
        }
        else if(A>=T) 
        {
        	sk=100.0*present/T - total;
        	s = (TextView) view.findViewById(R.id.s);
            s.setText("Skips till "+T+": "+(int)Math.floor(sk));
        }
        return view;
    }

}
