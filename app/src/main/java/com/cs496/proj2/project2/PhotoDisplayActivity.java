package com.cs496.proj2.project2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by q on 2017-01-02.
 */

public class PhotoDisplayActivity extends Activity {

    private ImageView iv = null;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_photodisplay);
        iv = (ImageView) findViewById(R.id.imageView);
        Intent gotIntent = getIntent();
        String id = gotIntent.getStringExtra("id");
        new DownloadAsyncTask().execute(id);
    }

    private class DownloadAsyncTask extends AsyncTask<String, Void, Bitmap> {
        String server_url = "http://ec2-52-79-161-158.ap-northeast-2.compute.amazonaws.com:3000/api/pics";
        protected Bitmap doInBackground(String... params){
            HttpURLConnection conn = null;
            String id = params[0];
            Bitmap bitmap = null;
            try{
                URL url = new URL(server_url + "/" + id);
                Log.d("Port:", "Port " + url.getPort());
                Log.d("Down URL", url.toString());
                conn = (HttpURLConnection) url.openConnection();
                //conn.setDoOutput(true);
                conn.setDoInput(true);

                //conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept","application/json");
                int responseCode = conn.getResponseCode();
                Log.d("response code", "" + responseCode);
                BufferedInputStream is = new BufferedInputStream(conn.getInputStream());
                //BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                //String str = rd.readLine();
                bitmap = BitmapFactory.decodeStream(is);
                /*byte[] image = Base64.decode(str, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);*/
                if (bitmap != null) {
                    Log.d("Bitmap", "Not null");
                }
            } catch (MalformedURLException e){
                Log.d("BAD URL", server_url + id);
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            }
            finally{
                conn.disconnect();
                return bitmap;
            }
        }

        protected void onPostExecute(Bitmap result){
            super.onPostExecute(result);

            iv.setImageBitmap(result);
        }
    }

}
