package com.cs496.proj2.project2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by q on 2017-01-03.
 */

public class AddJoongoActivity extends Activity {
    private EditText mNameEt;
    private EditText mPriceEt;
    private int modifyPosition = -1;
    private ImageView mProfile;
    private Uri mPhotoURI = null;
    private boolean isModifying = false;
    private ImageButton mCameraButton;
    private Button mAddCommitButton;
    private Button mCancelButton;
    private Button mEditButton;
    private ToggleButton mNegoButton;
    private ToggleButton mTBButton;
    private EditText mDescEt;
    private Bitmap bm = null;
    String mCurrentPhotoPath = null;


    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_SEARCH = 2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addjoongo);

        mNameEt = (EditText) findViewById(R.id.name_addJoongo);
        mPriceEt = (EditText) findViewById(R.id.price_addJoongo);
        mCameraButton = (ImageButton) findViewById(R.id.newPic_addjoongo);
        mCancelButton = (Button) findViewById(R.id.addCancelButton);
        mAddCommitButton = (Button) findViewById(R.id.addCommitButton);
        mEditButton = (Button) findViewById(R.id.addEditButton);
        mNegoButton = (ToggleButton) findViewById(R.id.negotiable_addJoongo);
        mTBButton = (ToggleButton) findViewById(R.id.TBable_addJoongo);
        mDescEt = (EditText) findViewById(R.id.desc_addJoongo);
        /*
        Intent gotIntent = getIntent();
        if (gotIntent != null) {
            Bundle gotBundle = gotIntent.getBundleExtra("data");
            if (gotBundle != null) {
                mNameEt.setText(gotBundle.getString("name"));
                mPhonenumberEt.setText(gotBundle.getString("phoneNumber"));
                modifyPosition = gotBundle.getInt("position");
                isModifying = gotBundle.getBoolean("isModifying");
                if (gotBundle.getString("photoDir") != null && !gotBundle.getString("photoDir").equals("")) {
                    //mCurrentPhotoPath = gotBundle.getString("photoDir");
                    mPhotoURI = Uri.parse(gotBundle.getString("photoDir"));
                    //Log.i("cs496: ocCreate", mCurrentPhotoPath);
                }

            }
        }

        mProfile = (ImageView) findViewById(R.id.pic_add);
        if (mPhotoURI == null) {//(mCurrentPhotoPath == null) {
            mProfile.setImageResource(R.drawable.ic_face_black_48dp);
        } else {
            setPic();
        }
        if(isModifying){
            mNameEt.setEnabled(false);
            mPhonenumberEt.setEnabled(false);
            mProfile.setEnabled(false);
            mAddCommitButton.setEnabled(false);
            mAddCommitButton.setVisibility(View.GONE);
            mCameraButton.setEnabled(false);

        } else*/
        //{
            mEditButton.setEnabled(false);
            mEditButton.setVisibility(View.GONE);
        //}

    }

    private void setPic() {

        int targetW = 150; //mProfile.getWidth();
        int targetH = 150; //mProfile.getHeight();
        try {

            InputStream is = this.getContentResolver().openInputStream(mPhotoURI);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            //BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            //MediaStore.Images.Media.getBitmap(this.getContentResolver(), mPhotoURI);
            BitmapFactory.decodeStream(is, null, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            //Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            is = this.getContentResolver().openInputStream(mPhotoURI);
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, bmOptions);

            //Log.i("cs496", bitmap.getWidth() + "," + bitmap.getHeight());
            mProfile.setImageBitmap(bitmap);
            bm = bitmap;



        } catch (Exception e) {
            //mProfile.setImageResource(R.drawable.);
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addCommitButton: {
                JoongoEntry j = new JoongoEntry();
                j.name = mNameEt.getText().toString();
                j.price = mPriceEt.getText().toString();
                j.negotiable = mNegoButton.isChecked();
                j.delivery = mTBButton.isChecked();
                j.desc = mDescEt.getText().toString();
                j.soldOut = false;
                j.image = bm;



                Intent newIntent = new Intent(this, MainActivity.class);
                newIntent.putExtra("data", j);
                Log.i("cs496", "onClick");
                setResult(RESULT_OK, newIntent);
                finish();
                break;
            }
            case R.id.addCancelButton: {
                finish();
                break;
            }
            case R.id.pic_addJoongo: {
                Intent newIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (newIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (photoFile != null) {
                        mPhotoURI = FileProvider.getUriForFile(this, "com.group2.team.project1", photoFile);
                        Log.i("cs496", mPhotoURI.toString());
                        newIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoURI);
                        startActivityForResult(newIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
                break;
            }
            case R.id.newPic_addjoongo: {
                Intent newIntent = new Intent(Intent.ACTION_GET_CONTENT);
                newIntent.setType("image/*");
                if (newIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(newIntent, REQUEST_IMAGE_SEARCH);
                }
                break;
            }
            /*case R.id.addEditButton:
            {
                mNameEt.setEnabled(true);
                mPhonenumberEt.setEnabled(true);
                mProfile.setEnabled(true);
                mAddCommitButton.setEnabled(true);
                mAddCommitButton.setVisibility(View.VISIBLE);
                mCameraButton.setEnabled(true);
                mEditButton.setEnabled(false);
                mEditButton.setVisibility(View.GONE);
                break;
            }*/
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
        } else if (requestCode == REQUEST_IMAGE_SEARCH && resultCode == RESULT_OK) {
            mPhotoURI = data.getData();
            setPic();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


}