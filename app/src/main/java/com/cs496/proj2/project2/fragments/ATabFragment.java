package com.cs496.proj2.project2.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cs496.proj2.project2.R;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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


/**
 * Created by q on 2016-12-30.
 */

public class ATabFragment extends Fragment {
    public CallbackManager callbackManager;
    private static final String TAG = "MainActivity";
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
                                                    ArrayList<String> namelist = new ArrayList<String>(data.length()+1);
                                                    for(int i=0;i<data.length();i++){
                                                        JSONObject names = data.getJSONObject(i);
                                                        namelist.add(names.getString("name"));

                                                    }

                                                    for(int j=0;j<namelist.size();j++){
                                                        Log.d("NAME", namelist.get(j));
                                                    }

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



        return rootView;
    }

}
