package com.impecabel.betherein;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DestinationListAdapter extends RecyclerView.Adapter<DestinationViewHolder> {

	private Context context;
	private ArrayList<Destination> destinations;

	public DestinationListAdapter(Context context,
			ArrayList<Destination> destinations) {
		this.context = context;
		this.destinations = destinations;
	}
	
	// Create new views (invoked by the layout manager)
    @Override
    public DestinationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.destination_group_item, parent, false);
        DestinationViewHolder viewHolder = new DestinationViewHolder(context, view);
        return viewHolder;
    }
    
 // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(DestinationViewHolder viewHolder, int position) {
    	Destination destination = destinations.get(position);
        Log.d("ADAPTER", Globals.staticCurrentLocation.toString());
        Log.d("ADAPTER", Globals.portoLocation.toString());
    	viewHolder.tvDescription.setText(destination.getDescription());
        viewHolder.ivCompass.initializeCompass(Globals.portoLocation, Globals.staticCurrentLocation, R.drawable.ic_action_send_now);
        /* if (position == 1)
        	Globals.ivCompass = viewHolder.ivCompass;*/
        
        //viewHolder.ivFlag.setImageResource(country.getFlagResID());
    }

	@Override 
	public int getItemCount() {	
		return destinations.size();
	}
	
	public void add(Destination destination, int position) {
		destinations.add(position, destination);
        notifyItemInserted(position);
    }

    public void remove(Destination destination) {
        int position = destinations.indexOf(destination);
        destinations.remove(position);
        notifyItemRemoved(position);
    }
    


}
