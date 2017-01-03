package com.cs496.proj2.project2.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;

import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs496.proj2.project2.AddJoongoActivity;
import com.cs496.proj2.project2.JoongoEntry;
import com.cs496.proj2.project2.MainActivity;
import com.cs496.proj2.project2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by q on 2016-12-30.
 */

public class CTabFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static CTabFragment newInstance(){
        return new CTabFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_c, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.joongoRecyclerView);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new JoongoAdapter(new ArrayList<JoongoEntry>());
        mRecyclerView.setAdapter(mAdapter);


        new PopulateAsyncTask().execute();
        return rootView;
    }

    public void addData(Bundle bundle){
        JoongoEntry j = new JoongoEntry();
        j.name = bundle.getString("name");
        j.price = bundle.getString("price");
        j.soldOut = false;
        j.thumbnail = (Bitmap) bundle.getParcelable("thumbnail");
        j.negotiable = bundle.getBoolean("negotiable");
        j.delivery = bundle.getBoolean("delivery");
        j.desc = bundle.getString("desc");
        j.image = Uri.parse(bundle.getString("image"));
        j.deviceID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            InputStream is = getActivity().getContentResolver().openInputStream(j.image);
            Bitmap bm = BitmapFactory.decodeStream(is);
            j.thumbnail = ThumbnailUtils.extractThumbnail(bm, 300, 300);
        }
        catch(Exception e){
            e.printStackTrace();
            j.thumbnail = null;
        }
        new AddJoongoAsyncTask().execute(j);
        //((JoongoAdapter) mAdapter).addItem(j);


    }

    public void addComment(String id, String jsonStr) {

        new AddCommentAsyncTask().execute(id, jsonStr);

    }

    public void finishDeal(String id) {
        new SoldAsyncTask().execute(id);

    }


    class SoldAsyncTask extends AsyncTask<String, Void, Void> {
        private String server_url = "http://ec2-52-79-161-158.ap-northeast-2.compute.amazonaws.com:3000/api/joongo/sold/";
        public Void doInBackground(String... params){
            HttpURLConnection conn = null;
            String id = params[0];

            try{
                URL url = new URL(server_url + id);
                Log.d("SoldAsyncTask", server_url + id);
                conn = (HttpURLConnection) url.openConnection();
                Log.d("SoldAsyncTask", "conn pass");
                //conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                //conn.setDoOutput(true);
                //conn.setDoInput(true);
                int responseCode = conn.getResponseCode();

                Log.d("SoldAsyncTask", "Resp code" + responseCode);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally{
                conn.disconnect();
            }
            return null;
        }
        public void onPostExecute(JoongoEntry j){
            //((JoongoAdapter)mAdapter).addItem(j);
            new PopulateAsyncTask().execute();
        }

    }

    class AddCommentAsyncTask extends AsyncTask<String, Void, Void> {
        private String server_url = "http://ec2-52-79-161-158.ap-northeast-2.compute.amazonaws.com:3000/api/joongo";
        public Void doInBackground(String... params){
            HttpURLConnection conn = null;
            String id = params[0];
            String comment = params[1];

            try{
                URL url = new URL(server_url + "/" + id);
                conn = (HttpURLConnection) url.openConnection();
                Log.d("AddJoongoAsyncTask", "conn pass");
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                wr.write(comment);
                Log.d("AddJoongoAsyncTask", "write pass");
;
                wr.flush();
                int responseCode = conn.getResponseCode();
                Log.d("AddCommentAsyncTask", "Resp code" + responseCode);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally{
                conn.disconnect();
            }
            return null;
        }
        public void onPostExecute(JoongoEntry j){
            //((JoongoAdapter)mAdapter).addItem(j);
            new PopulateAsyncTask().execute();
        }

    }

    class AddJoongoAsyncTask extends AsyncTask<JoongoEntry, Void, JoongoEntry> {
        private String server_url = "http://ec2-52-79-161-158.ap-northeast-2.compute.amazonaws.com:3000/api/joongo";
        public JoongoEntry doInBackground(JoongoEntry... joongoEntries){
            HttpURLConnection conn = null;
            JoongoEntry joongoEntry = joongoEntries[0];

            try{
                URL url = new URL(server_url);
                conn = (HttpURLConnection) url.openConnection();
                Log.d("AddJoongoAsyncTask", "conn pass");
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject j = new JSONObject();

                j.put("name", joongoEntry.name);
                j.put("price", joongoEntry.price);
                j.put("isNegotiable", joongoEntry.negotiable);
                j.put("isTaekBae", joongoEntry.delivery);
                j.put("isSold", joongoEntry.soldOut);
                j.put("description", joongoEntry.desc);
                j.put("comments", "[]");
                j.put("id", joongoEntry.deviceID);
                Log.d("AddJoongoAsyncTask", "JSON create pass");
                InputStream is = getActivity().getContentResolver().openInputStream(joongoEntry.image);
                Bitmap bm = BitmapFactory.decodeStream(is);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] image = baos.toByteArray();
                String encodedImage = Base64.encodeToString(image, Base64.DEFAULT);

                baos = new ByteArrayOutputStream();
                joongoEntry.thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] thumb = baos.toByteArray();
                String encodedThumb = Base64.encodeToString(thumb, Base64.DEFAULT);
                j.put("thumbnail", encodedThumb);
                j.put("image", encodedImage);
                Log.d("CS469", encodedImage.substring(0, 10));
                Log.d("CS469", encodedImage.substring(encodedImage.length() - 10));
                Log.d("AddJoongoAsyncTask", "Thumb pass");
                Log.d("CS496", "Image sent");

                BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                wr.write(j.toString());
                Log.d("AddJoongoAsyncTask", "write pass");
                Log.d("AddJoongoAsyncTask", j.toString());
                wr.flush();
                int responseCode = conn.getResponseCode();
                Log.d("AddJoongoAsyncTask", "Resp code" + responseCode);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally{
                conn.disconnect();
            }
            return joongoEntry;
        }

        public void onPostExecute(JoongoEntry j){
            //((JoongoAdapter)mAdapter).addItem(j);
            new PopulateAsyncTask().execute();
        }

    }

    class PopulateAsyncTask extends AsyncTask<Void, Void, ArrayList<JoongoEntry>>{
        String server_url = "http://ec2-52-79-161-158.ap-northeast-2.compute.amazonaws.com:3000/api/joongo";
        @Override
        protected ArrayList<JoongoEntry> doInBackground(Void... voids){
            HttpURLConnection conn = null;

            ArrayList<JoongoEntry> list = new ArrayList<>();

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
                    JoongoEntry joongoEntry = new JoongoEntry();
                    joongoEntry.id = j.getString("_id");
                    byte[] thumb = Base64.decode(j.getString("thumbnail"), Base64.DEFAULT);
                    joongoEntry.thumbnail = BitmapFactory.decodeByteArray(thumb, 0, thumb.length);
                    Log.d("JSONArray", j.toString());
//                    byte[] image = Base64.decode(j.getString("image"), Base64.DEFAULT);
//
//                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//                    String imageFileName = "JPEG_" + timeStamp + "_";
//
//                    File storageDir = getContext().getCacheDir();
//                    File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
//                    Bitmap bm = BitmapFactory.decodeByteArray(image, 0, image.length);
//                    bm.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(imageFile));
//
//                    joongoEntry.image = FileProvider.getUriForFile(getContext(), "com.cs496.proj2.project2", imageFile);
//                    bm.recycle();
                    joongoEntry.name = j.getString("name");
                    joongoEntry.negotiable = j.getBoolean("isNegotiable");
                    joongoEntry.price = j.getString("price");
                    joongoEntry.delivery = j.getBoolean("isTaekBae");
                    joongoEntry.soldOut = j.getBoolean("isSold");
                    joongoEntry.desc = j.getString("description");
                    joongoEntry.comments = j.getJSONArray("comments");
                    joongoEntry.deviceID = j.getString("id");
                    joongoEntry.id = j.getString("_id");
                    list.add(joongoEntry);
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

        protected void onPostExecute(ArrayList<JoongoEntry> result){
            super.onPostExecute(result);

            ((JoongoAdapter)mAdapter).setmDataSet(result);

        }
    }

    class JoongoAdapter extends RecyclerView.Adapter<JoongoAdapter.ViewHolder> implements View.OnClickListener{
        private ArrayList<JoongoEntry> mDataSet;

        public class ViewHolder extends RecyclerView.ViewHolder{
            public View mSoldOut;
            public ImageView mImage;
            public TextView mName;
            public TextView mPrice;
            public TextView mNegotiable;
            public TextView mDelivery;
            public TextView mDesc;
            public ViewHolder(View view){
                super(view);
                //mSoldOut = (TextView) view.findViewById(R.id.joongoSoldOut);
                mImage = (ImageView) view.findViewById(R.id.joongoImage);
                mName = (TextView) view.findViewById(R.id.joongoName);
                mPrice = (TextView) view.findViewById(R.id.joongoPrice);
                mNegotiable = (TextView) view.findViewById(R.id.joongoNegotiable);
                mDelivery = (TextView) view.findViewById(R.id.joongoTBable);
                mDesc = (TextView) view.findViewById(R.id.joongoDesc);
                mSoldOut = view.findViewById(R.id.Barcolor);
            }
        }

        public JoongoAdapter(ArrayList<JoongoEntry> data){
            mDataSet = data;
        }

        @Override
        public JoongoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.joongoview, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }


        public void onBindViewHolder(ViewHolder holder, int position){
            JoongoEntry j = mDataSet.get(position);
           // holder.mSoldOut.setVisibility(j.soldOut?View.VISIBLE:View.GONE);
            holder.mNegotiable.setEnabled(j.negotiable);
            holder.mDelivery.setEnabled(j.delivery);
            holder.mImage.setImageBitmap(j.thumbnail);
            holder.mName.setText(j.name);
            holder.mPrice.setText(j.price);
            holder.mDesc.setText(j.desc);
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(this);
           // holder.mSoldOut.setBackgroundColor(getResources().getColor(j.soldOut?R.color.colorFont:R.color.colorPrimary));
           holder.mSoldOut.setBackgroundColor(ContextCompat.getColor(getContext(), j.soldOut?R.color.colorFont:R.color.colorPrimary));
        }


//        public void addItem(JoongoEntry j){
//            mDataSet.add(j);
//            this.notifyItemInserted(mDataSet.size() - 1);
//        }

        public JoongoEntry getItem(int pos){
            return mDataSet.get(pos);
        }

        public void setmDataSet(ArrayList<JoongoEntry> list){
            mDataSet = list;
            this.notifyDataSetChanged();
        }

        public int getItemCount(){
            return mDataSet.size();
        }

        public void onClick(View v){
            JoongoEntry j = this.mDataSet.get((Integer)v.getTag());
            Intent newIntent = new Intent(getActivity().getApplicationContext(), AddJoongoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("name", j.name);
            bundle.putString("price", j.price);
            bundle.putBoolean("negotiable", j.negotiable);
            bundle.putBoolean("delivery", j.delivery);
            bundle.putString("desc", j.desc);
            bundle.putString("comments", j.comments.toString());
            //bundle.putString("image", j.image.toString());
            bundle.putBoolean("isComment", true);
            bundle.putString("id", j.id);
            bundle.putString("deviceID", j.deviceID);
            newIntent.putExtra("data", bundle);



            getActivity().startActivityForResult(newIntent, MainActivity.ADD_NEW_JOONGO_COMMENT);
        }

    }
}




