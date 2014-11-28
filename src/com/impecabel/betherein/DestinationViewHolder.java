package com.impecabel.betherein;

import garin.artemiy.compassview.library.CompassView;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DestinationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private Context context;
    public TextView tvDescription;
    public TextView tvEta;
    public ImageView ivMode;
    public CompassView ivCompass;
    

    public DestinationViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
        tvEta = (TextView) itemView.findViewById(R.id.tvEta);
        ivMode = (ImageView) itemView.findViewById(R.id.ivMode);
        ivCompass = (CompassView) itemView.findViewById(R.id.ivCompass);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(context, tvDescription.getText().toString(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context,
				DestinationDetailsActivity.class);
		context.startActivity(intent);
    }
}