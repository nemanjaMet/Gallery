package com.example.neman.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import java.util.ArrayList;

import adapter.ThumbnailsAdapter;
import helpers.Constants;
import model.ThumbnailPhoto;

public class MainActivityJava extends AppCompatActivity {

    private ArrayList<ThumbnailPhoto> thumbnailPhotos;
    RecyclerView rvGallery;
    int numberOfColumns = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        setAdapter();
    }

    private void setAdapter()
    {
        // set up the RecyclerView
        rvGallery = findViewById(R.id.rvGallery);

        rvGallery.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        ThumbnailsAdapter adapter = new ThumbnailsAdapter(this, thumbnailPhotos, getDimension());
        //adapter.setClickListener(this);
        rvGallery.setAdapter(adapter);
    }

    private int getDimension()
    {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        //if you need three fix imageview in width
        int devicewidth = displaymetrics.widthPixels / numberOfColumns;

        //if you need 4-5-6 anything fix imageview in height
        int deviceheight = displaymetrics.heightPixels / numberOfColumns;

        //holder.image_view.getLayoutParams().width = devicewidth;

        //if you need same height as width you can set devicewidth in holder.image_view.getLayoutParams().height
        //holder.image_view.getLayoutParams().height = deviceheight;

        return devicewidth;
    }

    private void getThumbnailsPaths()
    {
        thumbnailPhotos = new ArrayList<>();

        String[] projection = {MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID,
                MediaStore.Images.Thumbnails.DATA};
        Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection,
                null, null, null);
        //HashMap<String, String> thumbnailList = new HashMap<String, String>();

        if (cursor != null && cursor.moveToFirst()) {
            int image_idColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID);
            int dataColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);

            int index = cursor.getCount() - 1;

            while (index >= 0) {

                cursor.moveToPosition(index);

                ThumbnailPhoto thumbnailPhoto = new ThumbnailPhoto();
                thumbnailPhoto.setId(cursor.getLong(image_idColumn));
                thumbnailPhoto.setPath(cursor.getString(dataColumn));

                thumbnailPhotos.add(thumbnailPhoto);

                index--;
            }

            /*do {
                image_id = cursor.getInt(image_idColumn);
                image_path = cursor.getString(dataColumn);
                thumbnailList.put("" + image_id, image_path);
            } while (cursor.moveToNext());*/

            cursor.close();
        }
    }


    private void checkPermission()
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constants.EXTERNAL_STORAGE_PERMISSION_REQ);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted

            getThumbnailsPaths();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.EXTERNAL_STORAGE_PERMISSION_REQ: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    getThumbnailsPaths();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    /*
    * Uri lUri;
    Cursor lCursor;
    Log.w("ImageUtils: ", "GetAllImagesFoldersCursor");

    lUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    String[] lProjection = {"DISTINCT " + MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN};
    String lSelectionString = MediaStore.Images.Media.BUCKET_DISPLAY_NAME +
            " IS NOT NULL) GROUP BY (" + MediaStore.Images.Media.BUCKET_DISPLAY_NAME;
    lCursor = aContext.getContentResolver().query(lUri, lProjection, lSelectionString,
            null, null);

    if (lCursor != null) {
        Log.w("ImageUtils: ", "Returning total Folders: " + lCursor.getCount());
    }
    return lCursor;
    * */

}
