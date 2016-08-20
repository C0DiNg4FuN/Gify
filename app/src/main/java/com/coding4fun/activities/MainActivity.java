package com.coding4fun.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.coding4fun.R;
import com.coding4fun.adapters.GifRVAdapter;
import com.coding4fun.models.GifModel;
import com.coding4fun.utils.HttpManager;
import com.coding4fun.utils.RequestPackage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    private static final int SELECT_PICTURE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_2);

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i,"Pick a gif"),SELECT_PICTURE);
                Toast.makeText(MainActivity.this, "Pick a gif", Toast.LENGTH_SHORT).show();
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this, "Upload GIF", Toast.LENGTH_SHORT).show();
                return true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == SELECT_PICTURE){
                Uri uri = data.getData();
                String type = getMimeType(uri);
                if (type == null)
                    Toast.makeText(this, "Unknown Error!", Toast.LENGTH_LONG).show();
                else if (type.toLowerCase().equals("image/gif"))
                    new UploadGIF(uri).execute();
                else
                    Toast.makeText(this, "Only GIFs are supported!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        ContentResolver cr = getContentResolver();
        mimeType = cr.getType(uri);
        if (mimeType==null || mimeType.equals("")) {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }

    private String getFileNameFromUri(Uri uri) {
        Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        return returnCursor.getString(nameIndex);
    }

    private long getFileSizeFromUri(Uri uri) {
        Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        return returnCursor.getLong(sizeIndex);
    }

    String getFileNameWithoutExtension(File f){
        String name = f.getName().substring(0, f.getName().lastIndexOf("."));
        return name;
    }

    private String getAbsolutePathFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
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
            //list.remove(list.size()-1);
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
                        list.add(new GifModel(joo.getString("name"),joo.getString("category"),formatSize(joo.getString("size")),downlaodImage(joo.getString("name"))));
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
            isLoading = false;
            list.remove(null);
            adapter.notifyDataSetChanged();
            if(s.equals("OK")) {
                offset += 20; //update offset. why 20? cz each request gets 20 item
            } else if(s.equals("No more data!")){
                moreDate = false;
                Toast.makeText(MainActivity.this, "No more data!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
            }
        }

        String formatSize(String size){
            long ss = Long.parseLong(size);
            String s = (ss >= (1024*1024)) ? (ss/(1024*1024d))+" MB" : (ss/(1024d))+" KB";
            return s;
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


    class UploadGIF extends AsyncTask<Void,Void,String> {

        Uri uri;
        ProgressDialog pd;
        String path;
        File file;
        RequestPackage p;

        public UploadGIF(Uri uri) {
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //get image path, name, and size
            path = getAbsolutePathFromUri(uri);
            // advise for self >> never say: File f = new File(uri.getPath())
            file = new File(path);
            String fileName = file.getName();
            long fileSize = file.length();
            String size = (fileSize >= (1024 * 1024)) ? (fileSize / (1024 * 1024d)) + " MB" : (fileSize / (1024d)) + " KB";

            //prepare HTTP post request
            p = getRequestPackage(getFileNameWithoutExtension(file), fileSize + "");

            //initialize progress dialog, and set its msg to file name & size
            pd = new ProgressDialog(MainActivity.this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.setMessage("Uploading " + fileName + " (" + size + ") ...");
            pd.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response = HttpManager.getData(p);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            Log.e("result",result);
            JSONObject jo = null;
            try {
                jo = new JSONObject(result);
                if (jo.getString("status").equals("OK"))
                    Toast.makeText(MainActivity.this, "Picture uploaded successfully", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Error uploading picture !", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        RequestPackage getRequestPackage(String name, String size) {
            RequestPackage p = new RequestPackage();
            p.setMethod_POST();
            p.setUrl("http://www.coding4fun.96.lt/gif/main.php");
            p.addParam("what", "uploadGIF");
            p.addParam("name", name);
            p.addParam("size", size);
            p.addParam("category", "test");
            p.addParam("encoded_string", file2base64(path));
            return p;
        }

        String file2base64(String path) {
            String base64 = null;
            try {
                File file = new File(path);
                FileInputStream fis = new FileInputStream(file);
                byte[] bytes = new byte[(int) file.length()];
                fis.read(bytes);
                base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return base64;
        }
    }
}