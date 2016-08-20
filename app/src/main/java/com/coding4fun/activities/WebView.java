package com.coding4fun.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.coding4fun.R;
import com.coding4fun.models.GifModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by coding4fun on 16-Jul-16.
 */

public class WebView extends AppCompatActivity implements View.OnClickListener {

    GifModel gif;
    FloatingActionButton download, share, details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_gif);

        gif = (GifModel) getIntent().getExtras().getSerializable("gif");
        download = (FloatingActionButton) findViewById(R.id.wv_download);
        share = (FloatingActionButton) findViewById(R.id.wv_share);
        details = (FloatingActionButton) findViewById(R.id.wv_details);

        download.setOnClickListener(this);
        share.setOnClickListener(this);
        details.setOnClickListener(this);

        String url = gif.getLink();
        /*if(getIntent().getExtras() == null)
            url = "http://www.coding4fun.96.lt/gif/f.gif";
        else
            url = getIntent().getExtras().getString("url");*/

        android.webkit.WebView wv = (android.webkit.WebView) findViewById(R.id.web_view);
        wv.getSettings().setLoadsImagesAutomatically(true);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.loadUrl(url);

        /*Intent openIntent = new Intent();
        openIntent.setAction(Intent.ACTION_VIEW);
        openIntent.setData(Uri.parse(url));
        startActivity(openIntent);*/

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.wv_download:
                new DownloadGIF().execute();
                break;
            case R.id.wv_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, gif.getLink());
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
                break;
            case R.id.wv_details:
                //prepare details
                String msg = "Name: "+gif.getName()+"\n";
                msg += "Category: "+gif.getCategory()+"\n";
                msg += "Size: "+gif.getSize()+"\n";
                //build & show dialog
                AlertDialog.Builder d = new AlertDialog.Builder(this);
                d.setTitle("GIF Details");
                d.setMessage(msg);
                d.setIcon(R.drawable.gif_icon);
                d.setNegativeButton("OK", null);
                AlertDialog a = d.create();
                a.show();
                break;
        }
    }


    class DownloadGIF extends AsyncTask<Void,Void,String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(WebView.this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.setMessage("Downloading " + gif.getName() + " (" + gif.getSize() + ") ...");
            pd.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                //check if directory exists
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Gify";
                File dir = new File(path);
                if (!dir.exists())
                    dir.mkdirs();
                //check if gif exists
                File f = new File(dir, gif.getName() + ".gif");
                if (f.exists())
                    return "GIF already exists!";
                //get input stream
                URL u = new URL(gif.getLink());
                InputStream in = (InputStream) u.getContent();
                //write input stream to output stream
                OutputStream out = new FileOutputStream(f);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
                return "OK";
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            if (result.equals("OK"))
                Toast.makeText(WebView.this, "GIF is downloaded successfully to 'Gify' directory", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(WebView.this, "Error downloading GIF!\n" + result, Toast.LENGTH_LONG).show();
        }
    }

}