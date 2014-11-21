package com.impecabel.betherein;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class DestinationListAdapter extends BaseExpandableListAdapter {

	Activity activity;
	ArrayList<Destination> destinations;

	public DestinationListAdapter(Activity activity,
			ArrayList<Destination> destinations) {
		this.activity = activity;
		this.destinations = destinations;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return destinations.get(groupPosition).getDetails();
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		if (convertView == null){			
			LayoutInflater inflater = activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.destination_item, parent, false);
		}
		
		TextView tvFrom = (TextView) convertView.findViewById(R.id.tvFrom);
		//TextView tvTo = (TextView) convertView.findViewById(R.id.tvTo);
		
		DestinationDetails destDetails = destinations.get(groupPosition).getDetails();
		
		tvFrom.setText(destDetails.getOrigin());
		//tvTo.setText(destDetails.getDestination());
		
		return convertView;

	}


	@Override
	public Object getGroup(int groupPosition) {
		return destinations.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return destinations.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null){			
			LayoutInflater inflater = activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.destination_group_item, parent, false);
		}
		
		TextView tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
		
		Destination dest = destinations.get(groupPosition);
		
		tvDescription.setText(dest.getDescription());
		
		return convertView;
		
		
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}



}
