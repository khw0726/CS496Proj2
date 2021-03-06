package com.cs496.proj2.project2;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.cs496.proj2.project2.fragments.ATabFragment;
import com.cs496.proj2.project2.fragments.BTabFragment;
import com.cs496.proj2.project2.fragments.CTabFragment;


public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * com.cs496.proj2.project2.fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public static final int REQUEST_IMAGE_SEARCH = 8;
    public static final int ADD_NEW_JOONGO = 6;
    public static final int ADD_NEW_JOONGO_COMMENT = 4;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d("CS496Test", "onActivityResult");
        Log.d("CS496Test", resultCode + " " + requestCode);
        Log.d("CS496Test", "1st user"+RESULT_FIRST_USER);
        if(requestCode == REQUEST_IMAGE_SEARCH){
            if(resultCode == RESULT_OK) {
                Log.d("CS496Test", "addData");
                Fragment f = mSectionsPagerAdapter.fragments[1];
                if (f != null) {

                    ((BTabFragment) f).addData(data.getData());
                }
            }
        } else if(requestCode == ADD_NEW_JOONGO){
            if(resultCode == RESULT_OK) {
                Log.d("CS496Test", "addData");
                Fragment f = mSectionsPagerAdapter.fragments[2];
                if (f != null) {
                    ((CTabFragment) f).addData(data.getBundleExtra("data"));
                }
            }
        } else if(requestCode == ADD_NEW_JOONGO_COMMENT){
            Log.d("CS496Test", "addComment");
            if(resultCode == RESULT_OK) {
                Fragment f = mSectionsPagerAdapter.fragments[2];
                if (f != null) {

                    ((CTabFragment) f).addComment(data.getStringExtra("id"), data.getStringExtra("comment"));
                }
            } else if(resultCode == RESULT_FIRST_USER){
                Fragment f = mSectionsPagerAdapter.fragments[2];
                if (f != null) {

                    ((CTabFragment) f).finishDeal(data.getStringExtra("id"));
                }
            }

        }
        ((ATabFragment)mSectionsPagerAdapter.fragments[0]).callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if((mViewPager.getCurrentItem() == 1)){
                Intent newIntent = new Intent(Intent.ACTION_GET_CONTENT);
                newIntent.setType("image/*");
                if (newIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(newIntent, MainActivity.REQUEST_IMAGE_SEARCH);
                }
            } else if(mViewPager.getCurrentItem() == 2){
                Intent newIntent = new Intent(getApplicationContext(), AddJoongoActivity.class);
                startActivityForResult(newIntent, ADD_NEW_JOONGO);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        Fragment[] fragments = new Fragment[3];
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position){
                case 0:
                    return fragments[0] = ATabFragment.newInstance();
                case 1:
                    return fragments[1] = BTabFragment.newInstance();
                case 2:
                    return fragments[2] = CTabFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "CONTACTS";
                case 1:
                    return "GALLERY";
                case 2:
                    return "중고세상";
            }
            return null;
        }
    }
}
