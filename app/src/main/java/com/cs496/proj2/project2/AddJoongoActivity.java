package com.cs496.proj2.project2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private ImageView mPicture;
    private Uri mPhotoURI = null;
    private boolean isComment = false;
    private ImageButton mCameraButton;
    private Button mAddCommitButton;
    private Button mCancelButton;
    private Button mFinalizeButton;
    private ToggleButton mNegoButton;
    private ToggleButton mTBButton;
    private EditText mDescEt;
    private Bitmap bm = null;
    private TextView mCommentView;
    private EditText mCommentAuthorEdit;
    private EditText mCommentEdit;
    private String id;
    private String deviceID;
    String mCurrentPhotoPath = null;


    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_SEARCH = 2;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addjoongo);

        mNameEt = (EditText) findViewById(R.id.name_addJoongo);
        mPriceEt = (EditText) findViewById(R.id.price_addJoongo);
        mCameraButton = (ImageButton) findViewById(R.id.newPic_addJoongo);
        mCancelButton = (Button) findViewById(R.id.addCancelButton);
        mAddCommitButton = (Button) findViewById(R.id.addCommitButton);
        mFinalizeButton = (Button) findViewById(R.id.addFinalizeButton);
        mNegoButton = (ToggleButton) findViewById(R.id.negotiable_addJoongo);
        mTBButton = (ToggleButton) findViewById(R.id.TBable_addJoongo);
        mDescEt = (EditText) findViewById(R.id.desc_addJoongo);
        mPicture = (ImageView) findViewById(R.id.pic_addJoongo);
        //mCommentView = (TextView) findViewById(R.id.commentView_addJoongo);
        mCommentAuthorEdit = (EditText)findViewById(R.id.commentAuthor_addJoongo);
        mCommentEdit = (EditText)findViewById(R.id.comment_addJoongo);

        JSONArray comments;
        String commentsStr = "";
        LinearLayout mButtons = (LinearLayout) findViewById(R.id.buttonsLayout_addJoongo);
        LinearLayout mCommentAdd = (LinearLayout) findViewById(R.id.addCommentLayout_addJoongo);
        LinearLayout mCommentsLayout = (LinearLayout) findViewById(R.id.commentsLayout_addJoongo);
        Intent gotIntent = getIntent();
        if (gotIntent != null) {
            Bundle gotBundle = gotIntent.getBundleExtra("data");
            if (gotBundle != null) {
                mNameEt.setText(gotBundle.getString("name"));
                mPriceEt.setText(gotBundle.getString("price"));
                mTBButton.setChecked(gotBundle.getBoolean("delivery"));
                mNegoButton.setChecked(gotBundle.getBoolean("negotiable"));
                mDescEt.setText(gotBundle.getString("desc"));
                //mPhotoURI = Uri.parse(gotBundle.getString("image"));
                id = gotBundle.getString("id");
                deviceID = gotBundle.getString("deviceID");
                isComment = gotBundle.getBoolean("isComment");
                try {
                    comments = new JSONArray(gotBundle.getString("comments"));
                    for(int i =0; i<comments.length(); i++){
                        JSONObject j = comments.getJSONObject(i);
                        commentsStr = j.getString("author") + ": " + j.getString("content");
                        LinearLayout l = new LinearLayout(getApplicationContext());
                        TextView textView = new TextView(getApplicationContext());
                        textView.setText(commentsStr);
                        textView.setTextColor(getResources().getColor(R.color.colorFont));
                        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow, 0, 0, 0);
                        l.addView(textView);
                        mCommentsLayout.addView(l);
                    }
                    //mCommentView.setText(commentsStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //setPic();


            }
        }
        mFinalizeButton.setEnabled(false);
        mFinalizeButton.setVisibility(View.GONE);
        if(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).equals(deviceID)){
            mFinalizeButton.setEnabled(true);
            mFinalizeButton.setVisibility(View.VISIBLE);
        }

        if(isComment){
            mNameEt.setEnabled(false);
            mPriceEt.setEnabled(false);
            mPicture.setEnabled(false);
            mButtons.setVisibility(View.GONE);
            mCameraButton.setEnabled(false);
            mDescEt.setEnabled(false);

        } else
        {
            //mCommentView.setVisibility(View.GONE);
            mCommentAdd.setVisibility(View.GONE);
        }

    }

    private void setPic() {

        int targetW = 150; //mProfile.getWidth();
        int targetH = 150; //mProfile.getHeight();
        try {
            Log.d("SetPic", "setPic");
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
            mPicture.setImageBitmap(bitmap);
            bm = bitmap;



        } catch (Exception e) {
            //mProfile.setImageResource(R.drawable.);
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addCommitButton: {
                Bundle bundle = new Bundle();
                bundle.putString("name", mNameEt.getText().toString());
                bundle.putString("price", mPriceEt.getText().toString());
                bundle.putBoolean("negotiable", mNegoButton.isChecked());
                bundle.putBoolean("delivery", mTBButton.isChecked());
                bundle.putString("desc", mDescEt.getText().toString());
                //bundle.putString("deviceID", Settings.Secure.ANDROID_ID);
                if(mPhotoURI == null){
                    bundle.putString("image", "");
                } else {
                    bundle.putString("image", mPhotoURI.toString());
                }
                Log.d("AddJoongo", "Till here");
                //bundle.putParcelable("thumbnail", ThumbnailUtils.extractThumbnail(bm, 500, 500));
                Log.d("AddJoongo", "Thumb?");
                Intent newIntent = new Intent(this, MainActivity.class);
                newIntent.putExtra("data", bundle);
                Log.d("AddJoongo", "Intent creation fail T.T");

                setResult(RESULT_OK, newIntent);
                Log.i("cs496", "onClick");
                finish();
                break;
            }
            case R.id.addCancelButton: {
                finish();
                break;
            }
            case R.id.newPic_addJoongo: {
                Intent newIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                /*newIntent.putExtra("outputFormat",
                        Bitmap.CompressFormat.JPEG.toString());*/
                if (newIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (photoFile != null) {
                        mPhotoURI = FileProvider.getUriForFile(this, "com.cs496.proj2.project2", photoFile);
                        Log.i("cs496", mPhotoURI.toString());
                        newIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoURI);

                        startActivityForResult(newIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
                break;
            }
            case R.id.pic_addJoongo: {
                Intent newIntent = new Intent(Intent.ACTION_GET_CONTENT);
                newIntent.setType("image/*");
                if (newIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(newIntent, REQUEST_IMAGE_SEARCH);
                }
                break;
            }
            case R.id.addComment_addJoongo:
            {
                Intent intent = new Intent(this, MainActivity.class);
                JSONObject j = new JSONObject();
                try {
                    j.put("author", mCommentAuthorEdit.getEditableText().toString());
                    j.put("content", mCommentEdit.getEditableText().toString());
                    intent.putExtra("id", id);
                    intent.putExtra("comment", j.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setResult(RESULT_OK, intent);
                finish();
                break;
            }
            case R.id.addFinalizeButton:
            {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("soldOut", true);
                intent.putExtra("id", id);

                setResult(RESULT_FIRST_USER, intent);
                finish();
                break;
            }
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