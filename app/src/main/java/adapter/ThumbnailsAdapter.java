package adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.neman.gallery.R;

import java.io.IOException;
import java.util.ArrayList;

import model.ThumbnailPhoto;

import static helpers.Constants.CLICK_ACTION_THRESHHOLD;
import static helpers.Constants.LONG_PRESS_TIME;

public class ThumbnailsAdapter extends RecyclerView.Adapter<ThumbnailsAdapter.ThumbnailViewHolder> {

    private Context mContext;
    private ArrayList<ThumbnailPhoto> mData;
    private int imageSize;
    private ArrayList<Integer> selectedPhotos;
    private long lastTouchDown;
    private boolean isActionDown = false;

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

        thumbnailViewHolder.clPhotoHolder.getLayoutParams().width = imageSize;
        thumbnailViewHolder.clPhotoHolder.getLayoutParams().height = imageSize;
        thumbnailViewHolder.ivSelectedPhotoHolder.getLayoutParams().width = (int) (imageSize * 0.97f);
        thumbnailViewHolder.ivSelectedPhotoHolder.getLayoutParams().height = (int) (imageSize * 0.97f);


        if (selectedPhotos != null && selectedPhotos.contains(position)) {
            thumbnailViewHolder.ivSelectedPhotoHolder.setBackgroundColor(mContext.getResources().getColor(R.color.selectedPhoto));
            thumbnailViewHolder.ivPhoto.getLayoutParams().width = (int) (imageSize * 0.85f);
            thumbnailViewHolder.ivPhoto.getLayoutParams().height = (int) (imageSize * 0.85f);
        } else {
            thumbnailViewHolder.ivSelectedPhotoHolder.setBackgroundColor(mContext.getResources().getColor(R.color.notSelectedPhoto));
            thumbnailViewHolder.ivPhoto.getLayoutParams().width = (int) (imageSize * 0.97f);
            thumbnailViewHolder.ivPhoto.getLayoutParams().height = (int) (imageSize * 0.97f);
        }

        Glide.with(mContext).load(mData.get(position).getPath()).into(thumbnailViewHolder.ivPhoto);

        /*Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(), mData.get(position).getId(), MediaStore.Images.Thumbnails.MICRO_KIND, null);
        thumbnailViewHolder.ivPhoto.setImageBitmap(bitmap);
        thumbnailViewHolder.ivPhoto.setScaleType(ImageView.ScaleType.FIT_XY);*/

        setOnTouchListener(thumbnailViewHolder.ivPhoto, position);

        /*thumbnailViewHolder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(mData.get(position).getPath()));
                //Bitmap bitmap = BitmapFactory.decodeFile(mData.get(position).getPath());

                //Bitmap bitmap = getBitmapById(mData.get(position).getId());

                Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(), mData.get(position).getId(), MediaStore.Images.Thumbnails.MICRO_KIND, null);

                Toast.makeText(mContext,  String.valueOf(bitmap.getWidth() + "x" + String.valueOf(bitmap.getHeight())), Toast.LENGTH_SHORT ).show();
            }
        });*/



        //Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), mData.get(i));
        //thumbnailViewHolder.ivPhoto.setImageBitmap(bitmap);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchListener(ImageView imageView, final int position) {
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastTouchDown = System.currentTimeMillis();
                        isActionDown = true;
                        break;
                    case MotionEvent.ACTION_MOVE:

                        Log.d("ActionMoveTest:", "x");

                        if (isActionDown && System.currentTimeMillis() - lastTouchDown > LONG_PRESS_TIME) {
                            onPhotoTouch(position);
                            isActionDown = false;
                            return false;
                        }

                        break;
                    case MotionEvent.ACTION_UP:

                        if (isActionDown) {
                            if (System.currentTimeMillis() - lastTouchDown < CLICK_ACTION_THRESHHOLD) {
                                if (selectedPhotos != null && selectedPhotos.size() > 0)
                                    onPhotoTouch(position);
                                else
                                    onPhotoClick(position);
                            } else {
                                onPhotoTouch(position);
                            }
                        }

                        isActionDown = false;
                        break;
                }

                return true;
            }
        });
    }

    private void onPhotoClick(int position) {
        Bitmap bitmap = getBitmapById(mData.get(position).getId());
        Toast.makeText(mContext,  String.valueOf(bitmap.getWidth() + "x" + String.valueOf(bitmap.getHeight())), Toast.LENGTH_SHORT ).show();
    }

    private void onPhotoTouch(int position) {
        if (selectedPhotos == null)
            selectedPhotos = new ArrayList<>();



        if (selectedPhotos.contains(position)) {
            selectedPhotos.remove((Integer) position);
        }
        else {
            selectedPhotos.add(position);
        }

        notifyItemChanged(position);

    }

    private Bitmap getBitmapById(long imageId){

        Uri uri = Uri.withAppendedPath( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Long.toString(imageId) );

        Bitmap bitmap = null;

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

        ConstraintLayout clPhotoHolder;
        ImageView ivPhoto;
        ImageView ivSelectedPhotoHolder;

        public ThumbnailViewHolder(View itemView) {
            super(itemView);

            clPhotoHolder = itemView.findViewById(R.id.clPhotoHolder);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            ivSelectedPhotoHolder = itemView.findViewById(R.id.ivSelectedPhotoBackground);

        }

    }

}
