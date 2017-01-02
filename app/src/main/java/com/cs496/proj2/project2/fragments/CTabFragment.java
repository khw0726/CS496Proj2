package com.cs496.proj2.project2.fragments;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs496.proj2.project2.JoongoEntry;
import com.cs496.proj2.project2.R;

import java.util.ArrayList;

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

        return rootView;
    }


}

/*class AddJoongoAsyncTask extends AsyncTask<JoongoEntry, Void, ArrayList<JoongoEntry>> {

}*/

class JoongoAdapter extends RecyclerView.Adapter<JoongoAdapter.ViewHolder>{
    private ArrayList<JoongoEntry> mDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mSoldOut;
        public ImageView mImage;
        public TextView mName;
        public TextView mPrice;
        public TextView mNegotiable;
        public TextView mDelivery;
        public TextView mDesc;

        public ViewHolder(View view){
            super(view);
            mSoldOut = (TextView) view.findViewById(R.id.joongoSoldOut);
            mImage = (ImageView) view.findViewById(R.id.joongoImage);
            mName = (TextView) view.findViewById(R.id.joongoName);
            mPrice = (TextView) view.findViewById(R.id.joongoPrice);
            mNegotiable = (TextView) view.findViewById(R.id.joongoNegotiable);
            mDelivery = (TextView) view.findViewById(R.id.joongoTBable);
            mDesc = (TextView) view.findViewById(R.id.joongoDesc);
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
        holder.mSoldOut.setVisibility(j.soldOut?View.VISIBLE:View.GONE);
        holder.mNegotiable.setEnabled(j.negotiable?true:false);
        holder.mDelivery.setEnabled(j.delivery?true:false);
        holder.mImage.setImageBitmap(j.image);
        holder.mName.setText(j.name);
        holder.mPrice.setText(j.price);
        holder.mDesc.setText(j.desc);
    }

    public int getItemCount(){
        return mDataSet.size();
    }


}
