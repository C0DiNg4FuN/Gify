package com.coding4fun.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.coding4fun.R;
import com.coding4fun.adapters.GifRVAdapter;
import com.coding4fun.models.GifModel;
import com.coding4fun.utils.HttpManager;
import com.coding4fun.utils.RequestPackage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Toolbar tb;
    RecyclerView rv;
    GifRVAdapter adapter;
    List<GifModel> list = new ArrayList<>();
    private int offset;
    private boolean moreDate = true;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        initToolbar();
        initRV();

        //set scroll listener to detect when the last item is visible to show prpgress bar and get new data
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && moreDate && totalItemCount-1 <= (lastVisibleItem + 0)) {
                    new GetGIFs().execute();
                }
            }
        });

        //list.add(null);
        adapter = new GifRVAdapter(this,list);
        rv.setAdapter(adapter);

        //get the first 20 gif from server
        offset = 0;
        new GetGIFs().execute();

    }

    private void initToolbar(){
        tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setElevation(5);
    }

    private void initRV(){
        rv = (RecyclerView) findViewById(R.id.RV);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        DefaultItemAnimator anim = new DefaultItemAnimator();
        anim.setAddDuration(500);
        anim.setRemoveDuration(500);
        rv.setItemAnimator(anim);
    }


    class GetGIFs extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list.add(null); //to show progress bar
            adapter.notifyItemInserted(list.size()-1);
            isLoading = true;
        }

        @Override
        protected String doInBackground(Void... voids) {
            list.remove(list.size()-1);
            // http request to get gifs names
            //String url = "http://www.coding4fun.96.lt/gif/main.php?what=getNames&offset="+offset;
            RequestPackage p = new RequestPackage();
            p.setUrl("http://www.coding4fun.96.lt/gif/main.php");
            p.addParam("what","getNames");
            p.addParam("offset",offset+"");
            String response = HttpManager.getData(p);
            //parse json response
            try {
                JSONObject jo = new JSONObject(response);
                if(jo.getString("status").equals("OK")){
                    //add the new gifs to list
                    JSONArray ja = jo.getJSONArray("names");
                    for(int i=0;i<ja.length();i++) {
                        JSONObject joo = ja.getJSONObject(i);
                        list.add(new GifModel(joo.getString("name"),joo.getString("category"),downlaodImage(joo.getString("name"))));
                    }
                    return "OK";
                }else {
                    return jo.getString("reason");
                }
            } catch (JSONException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter.notifyDataSetChanged();
            if(s.equals("OK")) {
                offset += 20; //update offset. why 20? cz each request gets 20 item
            } else {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
            }
        }

        Bitmap downlaodImage(String name){
            RequestPackage p = new RequestPackage();
            p.setMethod_GET();
            p.setUrl("http://www.coding4fun.96.lt/gif/gif2jpg.php");
            p.addParam("img",name);
            try {
                URL u = new URL(p.getUrl() + "?" + p.getEncodedParams());
                InputStream in = (InputStream) u.getContent();
                Bitmap b = BitmapFactory.decodeStream(in);
                in.close();
                return b;
            } catch (Exception e){
                return null;
            }
        }

    }

}