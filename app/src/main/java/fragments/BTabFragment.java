package fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.cs496.proj2.project2.GalleryEntry;
import com.cs496.proj2.project2.MainActivity;
import com.cs496.proj2.project2.PhotoDisplayActivity;
import com.cs496.proj2.project2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import android.net.Uri;

import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by q on 2016-12-30.
 */

public class BTabFragment extends Fragment {

    public GalleryAdapter mAdapter = null;
    public GridView gv;
    public static BTabFragment newInstance(){

        return new BTabFragment();
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        new PopulateAsyncTask().execute();

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_b, container, false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        gv = (GridView) rootView.findViewById(R.id.gridView);


        gv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView parent, View v, int position, long id){
                GalleryEntry ge = (GalleryEntry) parent.getItemAtPosition(position);
                Intent newIntent = new Intent(getActivity(), PhotoDisplayActivity.class);
                newIntent.putExtra("id", ge.id);
                getActivity().startActivity(newIntent);

            }
        });



        /*FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.addButton);

        fab.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent newIntent = new Intent(Intent.ACTION_GET_CONTENT);
                newIntent.setType("image/*");
                if (newIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    getActivity().startActivityForResult(newIntent, MainActivity.REQUEST_IMAGE_SEARCH);
                }
            }
        });*/

        return rootView;
    }

    public void addData(Uri photoURI){
        Log.d("CS496Test", "addData");
        new UploadAsyncTask().execute(photoURI);
    }

    private class UploadAsyncTask extends AsyncTask<Uri, Void, String> {
        String server_url = "http://ec2-52-79-161-158.ap-northeast-2.compute.amazonaws.com:3000/api/pics";

        String boundary = "qPo$^%@#bvER";

        @Override
        protected String doInBackground(Uri... param) {
            HttpURLConnection conn = null;
            String id = null;
            try {
                URL url = new URL(server_url);
                Log.d("Port:", "Port " + url.getPort());
                conn = (HttpURLConnection) url.openConnection();
                Log.d("Connection", conn.toString());
                conn.setDoOutput(true);
                conn.setDoInput(true);
                //conn.setRequestProperty("Content-Type",  "application/json");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                //Log.d("Props", conn.getHeaderField("Content-Type"));
                conn.setRequestMethod("POST");
                Uri photoURI = param[0];
                String filename = photoURI.getLastPathSegment();
                InputStream is = getActivity().getContentResolver().openInputStream(photoURI);
                Bitmap bm = BitmapFactory.decodeStream(is);
                Bitmap thumb = ThumbnailUtils.extractThumbnail(bm, 500, 500);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] thumbs = baos.toByteArray();
                String encodedThumb = Base64.encodeToString(thumbs, Base64.DEFAULT);
                Log.d("Thumb", encodedThumb);

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                /*baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] image = baos.toByteArray();
                String encodedImage = Base64.encodeToString(image, Base64.DEFAULT);
                JSONObject j = new JSONObject();
                j.put("thumbnail", encodedThumb);
                j.put("image", encodedImage);
                Log.d("CS496", "Image sent");
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(j.toString());*/
                wr.writeBytes("\r\n--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"thumbnail\"\r\n\r\n" + encodedThumb);
                Log.d("CS496", "Thumb sent");
                wr.writeBytes("\r\n--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"pic\"; " +
                        "filename=\""+ filename.split(":")[1] +"\"\r\n");

                wr.writeBytes("Content-Type: image/jpeg\r\n\r\n"); //
                is = getActivity().getContentResolver().openInputStream(photoURI);
                int bytesAvailable = is.available();
                int maxBufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[maxBufferSize];

                int bytesRead = is.read(buffer, 0, bufferSize);
                while(bytesRead > 0){
                    DataOutputStream dataWrite = new DataOutputStream(conn.getOutputStream());
                    dataWrite.write(buffer, 0, bufferSize);
                    Log.d("UploadAsyncTask", "uploaded " + bufferSize);
                    bytesAvailable = is.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = is.read(buffer, 0, bufferSize);

                }
                is.close();
                wr.writeBytes("\r\n--" + boundary + "--\r\n");
                Log.d("CS496", "Image sent");
                wr.flush();

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while((line = rd.readLine()) != null){
                    Log.i("ResultFromHttp", line);
                    JSONObject jsonObject = new JSONObject(line);
                    id = jsonObject.getJSONObject("result").getString("_id");

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch(JSONException e){
                e.printStackTrace();
            } finally {
                conn.disconnect();
                return id;
            }

        }

        protected void onPostExecute(String result){
            new PopulateAsyncTask().execute();
        }

    }

    private class GalleryAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<GalleryEntry> mEntries = null;
        private ViewHolder viewHolder = null;
        private LayoutInflater inflater;
        private ImageView v;
        private class ViewHolder{

        }

        public GalleryAdapter(Context context, ArrayList<GalleryEntry> entries){
            this.mEntries = entries;
            this.mContext = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount(){
            return mEntries.size();
        }

        public void setArrayList(ArrayList<GalleryEntry> list){
            mEntries = list;
        }

        public GalleryEntry getItem(int position){
            return mEntries.get(position);
        }

        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            ImageView v = (ImageView) convertView;

            if(v == null){
                v = new ImageView(mContext);
                v.setLayoutParams(new GridView.LayoutParams(500, 500));
                v.setScaleType(ImageView.ScaleType.FIT_CENTER);
                v.setPadding(20, 20, 20, 20);
            }

            v.setImageBitmap(mEntries.get(position).thumbnail);

            return v;

        }


    }

    class PopulateAsyncTask extends AsyncTask<Void, Void, ArrayList<GalleryEntry>>{
        String server_url = "http://ec2-52-79-161-158.ap-northeast-2.compute.amazonaws.com:3000/api/pics";
        @Override
        protected ArrayList<GalleryEntry> doInBackground(Void... voids){
            HttpURLConnection conn = null;

            ArrayList<GalleryEntry> list = new ArrayList<>();

            try{
                URL url = new URL(server_url);
                conn = (HttpURLConnection) url.openConnection();

                conn.setDoInput(true);

                conn.setRequestProperty("Accept","Application/json");

                int responseCode = conn.getResponseCode();
                Log.d("CS496", "Response Code: " + responseCode);

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = rd.readLine();

                JSONArray jsonArray = new JSONArray(line);

                for(int i = 0; i< jsonArray.length(); i++){
                    JSONObject j = jsonArray.getJSONObject(i);
                    GalleryEntry g = new GalleryEntry();
                    g.id = j.getString("_id");
                    byte[] thumb = Base64.decode(j.getString("thumbnail"), Base64.DEFAULT);
                    g.thumbnail = BitmapFactory.decodeByteArray(thumb, 0, thumb.length);
                    list.add(g);
                }


            } catch (MalformedURLException e){
                Log.d("BAD URL", server_url);
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            }
            catch (JSONException e){
                e.printStackTrace();
            }

            finally{
                conn.disconnect();
                return list;
            }
        }

        protected void onPostExecute(ArrayList<GalleryEntry> result){
            super.onPostExecute(result);
            if(mAdapter == null){
                mAdapter = new GalleryAdapter(getContext(), result);
                gv.setAdapter(mAdapter);
            } else {
                mAdapter.setArrayList(result);
                mAdapter.notifyDataSetChanged();
            }

        }
    }


}


