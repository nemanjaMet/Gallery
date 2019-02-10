package adapter;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.neman.gallery.R;

import java.io.IOException;
import java.util.ArrayList;

import model.ThumbnailPhoto;

public class ThumbnailsAdapter extends RecyclerView.Adapter<ThumbnailsAdapter.ThumbnailViewHolder> {

    private Context mContext;
    private ArrayList<ThumbnailPhoto> mData;
    private int imageSize;
    private ArrayList<Integer> selectedPhotos;

    // data is passed into the constructor
    public ThumbnailsAdapter(Context context, ArrayList<ThumbnailPhoto> data, int imageSize) {
        this.mContext = context;
        this.mData = data;
        this.imageSize = imageSize;
    }

    @NonNull
    @Override
    public ThumbnailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.thumb_item, viewGroup, false);

        return new ThumbnailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailViewHolder thumbnailViewHolder, final int position) {

        thumbnailViewHolder.clPhotoholder.getLayoutParams().width = imageSize;
        thumbnailViewHolder.clPhotoholder.getLayoutParams().height = imageSize;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            thumbnailViewHolder.ivPhoto.setForegroundGravity(Gravity.CENTER);
        }

        thumbnailViewHolder.ivPhoto.getLayoutParams().width = (int) (imageSize * 0.97f);
        thumbnailViewHolder.ivPhoto.getLayoutParams().height = (int) (imageSize * 0.97f);

        Glide.with(mContext).load(mData.get(position).getPath()).into(thumbnailViewHolder.ivPhoto);

        thumbnailViewHolder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(mData.get(position).getPath()));
                //Bitmap bitmap = BitmapFactory.decodeFile(mData.get(position).getPath());

                Bitmap bitmap = getBitmapById(mData.get(position).getId());

                Toast.makeText(mContext,  String.valueOf(bitmap.getWidth() + "x" + String.valueOf(bitmap.getHeight())), Toast.LENGTH_SHORT ).show();
            }
        });

        //Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), mData.get(i));
        //thumbnailViewHolder.ivPhoto.setImageBitmap(bitmap);
    }

    private Bitmap getBitmapById(long imageId){

        /** Setting uri to the original image files stored in external storage device */
        //Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        /** Creating a cursor loader from the uri corresponds to mID **/
       // CursorLoader cLoader= new CursorLoader(mContext, uri, null, "_id=" + String.valueOf(imageId) , null, null);

        /*String[] projection = {MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID,
                MediaStore.Images.Thumbnails.DATA};
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection,
                "_id=" + String.valueOf(imageId), null, null);

        ArrayList<ThumbnailPhoto> thumbnailPhotos = new ArrayList<>();

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
        }*/

        /** Move to the first row of the cursor, if it exists */
        /*if(cLoader.moveToFirst()){
            path = arg1.getString(arg1.getColumnIndex("_data"));
            File imgFile = new  File(path);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                mImgOriginal.setImageBitmap(myBitmap);
                mImgOriginal.setAdjustViewBounds(true);
            }
        }*/

        Uri uri = Uri.withAppendedPath( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Long.toString(imageId) );

        Bitmap bitmap = null;
        //Glide.with(mContext).load(uri).into(bitmap);

        /*try {
            bitmap = Glide
                    .with(mContext)
                    .asBitmap()
                    .load(uri)
                    .submit()
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/

        try {
            bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(), imageId, MediaStore.Images.Thumbnails.MINI_KIND, null);
        return bitmap;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ThumbnailViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout clPhotoholder;
        ImageView ivPhoto;

        public ThumbnailViewHolder(View itemView) {
            super(itemView);

            clPhotoholder = itemView.findViewById(R.id.clPhotoHolder);
            ivPhoto = itemView.findViewById(R.id.iv_photo);

        }

    }

}
