package com.juet.attendance;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.juet.attendance.R;

public class CustomListAdapter extends BaseAdapter {

	private ArrayList<AttendanceValues> listData;

	private LayoutInflater layoutInflater;

	public CustomListAdapter(Context context, ArrayList<AttendanceValues> listData) {
		this.listData = listData;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_row_layout, null);
			holder = new ViewHolder();
			holder.subject = (TextView) convertView.findViewById(R.id.subject);
			holder.attendance = (TextView) convertView.findViewById(R.id.value);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.subject.setText(listData.get(position).subject);
		holder.attendance.setText(listData.get(position).attendance);
		return convertView;
	}

	static class ViewHolder {
		TextView subject;
		TextView attendance;
	}

}
