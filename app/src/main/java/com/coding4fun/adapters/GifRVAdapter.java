package com.coding4fun.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coding4fun.R;
import com.coding4fun.activities.WebView;
import com.coding4fun.models.GifModel;

import java.util.List;

/**
 * Created by coding4fun on 15-Jul-16.
 */

public class GifRVAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {

    private List<GifModel> items;
    Context context;
    private static final int EMPTY_VIEW = 10;
    private static final int GIF_VIEW = 11;
    private static final int LOADING_VIEW = 12;

    public GifRVAdapter(Context context, List<GifModel> modelData) {
        this.context = context;
    	items = modelData;
    }

    //describes an item view, and
    //Contains references for all views that are filled by the data of the entry
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name,category;
        ImageView details, share, bitmap;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.gif_name);
            category = (TextView) itemView.findViewById(R.id.gif_category);
            details = (ImageView) itemView.findViewById(R.id.gif_details);
            share = (ImageView) itemView.findViewById(R.id.gif_share);
            bitmap = (ImageView) itemView.findViewById(R.id.gif_image);
            itemView.setOnClickListener(this);
            details.setOnClickListener(this);
            share.setOnClickListener(this);
        }

		@Override
		public void onClick(View v) {
            final int index = getAdapterPosition();
            final GifModel item = items.get(index);
            if(v.getId() == R.id.gif_details){
                //prepare details
                String msg = "Name: "+item.getName()+"\n";
                msg += "Category: "+item.getCategory()+"\n";
                msg += "Size: "+item.getSize()+"\n";
                //build & show dialog
                AlertDialog.Builder d = new AlertDialog.Builder(context);
                d.setTitle("GIF Details");
                d.setMessage(msg);
                d.setIcon(R.drawable.gif_icon);
                d.setNegativeButton("OK", null);
                AlertDialog a = d.create();
                a.show();
            } else if(v.getId() == R.id.gif_share){
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, item.getLink());
                shareIntent.setType("text/plain");
                context.startActivity(shareIntent);
            } else {
                /*Intent openIntent = new Intent();
                openIntent.setAction(Intent.ACTION_VIEW);
                openIntent.setData(Uri.parse(item.getLink()));
                context.startActivity(openIntent);*/
                Intent viewIntent = new Intent(context, WebView.class);
                //viewIntent.putExtra("url",item.getLink());
                viewIntent.putExtra("gif",item);
                context.startActivity(viewIntent);
            }
		}
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        //ProgressBar pb;
        android.webkit.WebView wv;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            //pb = (ProgressBar) itemView.findViewById(R.id.loading_pb);
            wv = (android.webkit.WebView) itemView.findViewById(R.id.loading_wv);
        }
    }

    // Return the size of the items list
    @Override
    public int getItemCount() {
        return items.size()>0 ? items.size() : 1;	//otherwise, even empty layout won't appear
    }

    @Override
    public int getItemViewType(int position) {
        if (items.size() == 0)
            return EMPTY_VIEW;
        else if (items.get(position) instanceof GifModel)
            return GIF_VIEW;
        else if (items.get(position) == null)
            return LOADING_VIEW;
        return super.getItemViewType(position);
    }

    // inflate the item (row) layout and create the holder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if (viewType == EMPTY_VIEW) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty, viewGroup, false);
            EmptyViewHolder evh = new EmptyViewHolder(v);
            return evh;
        }
        if (viewType == GIF_VIEW) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gif_rv_row, viewGroup, false);
            MyViewHolder evh = new MyViewHolder(v);
            return evh;
        }
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loading, viewGroup, false);
        LoadingViewHolder vh = new LoadingViewHolder(v);
        return vh;
    }

    //display (update) the data at the specified position
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof MyViewHolder) {
            MyViewHolder vh = (MyViewHolder) viewHolder;
            GifModel item = items.get(position);
            vh.name.setText(item.getName());
            vh.category.setText(item.getCategory());
            vh.bitmap.setImageBitmap(item.getBitmap());
        } else if(viewHolder instanceof LoadingViewHolder) {
            LoadingViewHolder vh = (LoadingViewHolder) viewHolder;
            vh.wv.setBackgroundColor(Color.TRANSPARENT);
            vh.wv.loadUrl("file:///android_asset/loading.html");
        }
    }

}