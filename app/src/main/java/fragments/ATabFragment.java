package fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;


import com.cs496.proj2.project2.Contact;
import com.cs496.proj2.project2.R;

import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;


import android.database.Cursor;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by q on 2016-12-30.
 */

public class ATabFragment extends Fragment {
    public ListView listView;
    public CallbackManager callbackManager;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    public static ATabFragment newInstance(){
        return new ATabFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());




        View rootView = inflater.inflate(R.layout.fragment_a, container, false);


        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton)rootView.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("result", object.toString());
                        try {
                            String user_id = object.getString("id");
                            Log.d("test", user_id);

                            new GraphRequest(
                                    AccessToken.getCurrentAccessToken(),
                                    "/" + user_id + "/taggable_friends?limit=1000",
                                    null,
                                    HttpMethod.GET,
                                    new GraphRequest.Callback() {
                                        public void onCompleted(GraphResponse response) {
                                            Log.d("RESPONSE", "***********************************"+ response.toString());
                                            if(response != null) {
                                                JSONObject json = response.getJSONObject();
                                                Log.d("JSONOBJECT","***************8"+json.toString());
                                                try {
                                                    JSONArray data = json.getJSONArray("data");
                                                   // ArrayList<String> namelist = new ArrayList<String>(data.length()+1);
                                                    JSONArray facebook_contact = new JSONArray();
                                                    for(int i=0;i<data.length();i++){
                                                        JSONObject names = data.getJSONObject(i);
                                                       // namelist.add(names.getString("name"));
                                                        JSONObject small_facebook = new JSONObject();
                                                        small_facebook.put("name", names.getString("name"));
                                                        small_facebook.put("phoneNumber","weareFACEBOOKfriends");
                                                        facebook_contact.put(small_facebook);

                                                    }
                                                    new UploadAsyncTask().execute(facebook_contact);

                                                    /*for(int j=0;j<namelist.size();j++){
                                                        Log.d("NAME", namelist.get(j));
                                                    }*/

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }


                                            }else{

                                            }

                                        }
                                    }
                            ).executeAsync();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,taggable_friends.limit(30)");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();



            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("LoginErr", error.toString());
            }
        });


        try {
            queryContact();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<Contact> contactArrayList;
        new UploadAsyncTask().execute(json_contact);
        listView = (ListView) rootView.findViewById(R.id.list);
        new PopulateAsyncTask().execute();

        return rootView;
    }

    JSONArray json_contact = new JSONArray();
    private void readContact() throws JSONException{
        //Load contacts @ phone

        ContentResolver cr = getActivity().getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        int ididx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        int nameidx=cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);



        while (cursor.moveToNext()){
            JSONObject name_num = new JSONObject();
            try {
                name_num.put("name",cursor.getString(nameidx));
                String id=cursor.getString(ididx);
                Cursor cursor2 = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id},null);
                int typeidx = cursor2.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.TYPE);
                int numidx = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                while(cursor2.moveToNext()){
                    String num = cursor2.getString(numidx);
                    name_num.put("phoneNumber", num);

                }
                cursor2.close();
                json_contact.put(name_num);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        Log.i("READCONTACT","*******************"+json_contact.toString());
    }

    private void queryContact() throws JSONException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            readContact();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == PERMISSIONS_REQUEST_READ_CONTACTS){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                try {
                    readContact();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(getActivity(), "Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UploadAsyncTask extends AsyncTask<JSONArray, Void, Void> {
        String server_url = "http://ec2-52-79-161-158.ap-northeast-2.compute.amazonaws.com:3000/api/contacts";

        @Override
        protected Void doInBackground(JSONArray...param) {
            HttpURLConnection conn = null;
            String id = null;
            try {
                URL url = new URL(server_url);
                Log.d("Port:", "Port " + url.getPort());
                conn = (HttpURLConnection) url.openConnection();
                Log.d("Connection", conn.toString());
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONArray jsonArray = param[0];
                Log.d("CS496", "Image sent");
                conn.setRequestProperty("Content-Type","application/json");
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(jsonArray.toString());
                wr.flush();
                String inputLine = null;
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);
                }
                in.close();
                wr.close();



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } //catch(JSONException e){
              //  e.printStackTrace();}
             finally {
                conn.disconnect();
                return null;
            }

        }
    }

    private class ContactAdapter extends ArrayAdapter<Contact> {

        private ArrayList<Contact> items;

        public ContactAdapter(Context context, int textViewResourceId, ArrayList<Contact> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.contact_item, null);
            }
            Contact p = items.get(position);
            if (p != null) {
                TextView tt = (TextView) v.findViewById(R.id.toptext);
                TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                if (tt != null){
                    tt.setText(p.name);
                }
                if(bt != null){
                    bt.setText("number: "+ p.phoneNumber);
                }
            }
            return v;
        }
    }


    class PopulateAsyncTask extends AsyncTask<Void, Void,  ArrayList<Contact>> {
        String server_url = "http://ec2-52-79-161-158.ap-northeast-2.compute.amazonaws.com:3000/api/contacts";

        @Override
        protected ArrayList<Contact> doInBackground(Void... voids) {
            HttpURLConnection conn = null;

            ArrayList<Contact> final_contact = new ArrayList<Contact>();

            try {
                URL url = new URL(server_url);
                conn = (HttpURLConnection) url.openConnection();

                conn.setDoInput(true);

                conn.setRequestProperty("Accept", "Application/json");

                int responseCode = conn.getResponseCode();
                Log.d("CS496", "Response Code: " + responseCode);

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = rd.readLine();
                Log.i("LINE","************"+line);
                JSONArray jsonArray = new JSONArray(line);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject j = jsonArray.getJSONObject(i);
                    Contact new_contact = new Contact();
                    new_contact.name = j.getString("name");
                    new_contact.phoneNumber = j.getString("phoneNumber");
                    final_contact.add(new_contact);
                }
                Log.i("CS496","**********"+final_contact.toString());


            } catch (MalformedURLException e) {
                Log.d("BAD URL", server_url);
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
                return final_contact;
            }
        }

        protected void onPostExecute(ArrayList<Contact> result){
            super.onPostExecute(result);
                ContactAdapter m_adapter = new ContactAdapter(getActivity(), R.layout.contact_item, result);
                listView.setAdapter(m_adapter);
            }
        }
    }





