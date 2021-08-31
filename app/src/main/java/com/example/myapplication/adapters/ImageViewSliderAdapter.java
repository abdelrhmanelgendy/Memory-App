package com.example.myapplication.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.myapplication.pojo.ImageToUpload;
import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.OnSingleFlingListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class ImageViewSliderAdapter extends PagerAdapter {
    List<ImageToUpload> imgsUrl = new ArrayList<>();
    Context context;
    PhotoViewGetDrawable photoViewGetDrawable;

    public void setPhotoViewMatrixChangeListener(PhotoViewGetDrawable photoViewMatrixChangeListener) {
        this.photoViewGetDrawable = photoViewMatrixChangeListener;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        int itemPosition = super.getItemPosition(object);
        return super.getItemPosition(object);
    }

    public ImageViewSliderAdapter(List<ImageToUpload> imgsUrl, Context context, boolean isFromFavorite) {
        this.imgsUrl = imgsUrl;
        this.context = context;
        this.fromFavorite = isFromFavorite;
    }

    @Override
    public int getCount() {
        return imgsUrl.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    boolean fromFavorite = false;

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(context);

        photoView.setFitsSystemWindows(true);
        Picasso.get().load(imgsUrl.get(position).getUrl()).into(photoView);
//      if (position==imgsUrl.size()||position>imgsUrl.size())
//        {
//            return photoView;
//        }
//        if (fromFavorite) {
//            Target target = new Target() {
//                @Override
//                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                    photoView.setImageBitmap(bitmap);
//                    Drawable image = photoView.getDrawable();
//                    if (bitmap!=null) {
//                        try {
//                            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
//                            photoViewGetDrawable.onGetUri(Uri.parse(path));
//                        } catch (Exception w)
//                        {
//                            if (position==imgsUrl.size()||position>imgsUrl.size())
//                            {
//                                return;
//                            }
//                            Picasso.get().load(imgsUrl.get(position).getUrl()).into(photoView);
//
//                            return;
//                        }
//
//                    }
//                    else
//                    {
//                        Picasso.get().load(imgsUrl.get(position).getUrl()).into(photoView);
//
//                    }
//    }
//
//    @Override
//    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//
//    }
//
//
//    @Override
//    public void onPrepareLoad(Drawable placeHolderDrawable) {
//    }
//};
//            if(position==imgsUrl.size()||position>imgsUrl.size())
//                    {
//                    return photoView;
//                    }
//                    Picasso.get().load(imgsUrl.get(position).getUrl()).into(target);
//
//                    }else{
//                    if(position==imgsUrl.size()||position>imgsUrl.size())
//                    {
//                    return photoView;
//                    }
//                    Picasso.get().load(imgsUrl.get(position).getUrl()).into(photoView);
//                    }
//

                    container.addView(photoView);
                    return photoView;

                    }

@Override
public void destroyItem(@NonNull ViewGroup container,int position,@NonNull Object object){
        container.removeView((PhotoView)object);
        }

public interface PhotoViewGetDrawable {
    void onGetUri(Uri uri);
}
}
