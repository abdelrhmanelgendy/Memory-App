package com.example.myapplication.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;
import com.example.myapplication.pojo.ImageFromStorag;
import com.example.myapplication.util.OnImageFromStorageClickListeners;

import java.io.File;
import java.util.ArrayList;

public class CustomeGalleryAdapter extends RecyclerView.Adapter<CustomeGalleryAdapter.ImageHolder> {
    boolean isTagged;
    private ArrayList<ImageFromStorag> arrayList = new ArrayList<>();
    private ArrayList<ImageFromStorag> selectedImages = new ArrayList<>();
    private Context context;
    private OnImageFromStorageClickListeners imageFromStorageClickListeners;


    public ArrayList<ImageFromStorag> getSelectedImages() {
        ArrayList<ImageFromStorag> selected = new ArrayList<>();
        for (ImageFromStorag imageFromStorag : arrayList) {
            if (imageFromStorag.getChecked()) {
                Log.d("TAG90", "getSelectedImages: "+imageFromStorag.getImage().getUri());
                selected.add(imageFromStorag);
            }
        }
        return selected;
    }

    public void setImageFromStorageClickListeners(OnImageFromStorageClickListeners imageFromStorageClickListeners) {
        this.imageFromStorageClickListeners = imageFromStorageClickListeners;
    }

    public CustomeGalleryAdapter(ArrayList<ImageFromStorag> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    public void setArrayList(ArrayList<ImageFromStorag> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.custome_gallery_image_item, null);
        return new ImageHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        ImageFromStorag imageFromStorag = arrayList.get(position);
        holder.bind(imageFromStorag);


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    protected class ImageHolder extends RecyclerView.ViewHolder {
        ImageView imgFromStorage;
        ConstraintLayout constraintLayoutChecked;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayoutChecked = itemView.findViewById(R.id.customeGalleyChecked);
            imgFromStorage = itemView.findViewById(R.id.customeGalleyImage);
        }

        public void bind(ImageFromStorag imageFromStorag) {
            constraintLayoutChecked.setVisibility(imageFromStorag.getChecked() ? View.VISIBLE : View.GONE);
            String path = imageFromStorag.getImage().getUri();
            File file = new File(path);
            Uri uri1 = Uri.fromFile(file);
            Glide.with(context).load(uri1).apply(new RequestOptions().override(350, 350)).into(imgFromStorage);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isTagged) {
                        imageFromStorag.setChecked(!imageFromStorag.getChecked());
                        constraintLayoutChecked.setVisibility(imageFromStorag.getChecked() ? View.VISIBLE : View.GONE);
                        imageFromStorageClickListeners.onImageClick(imageFromStorag,OnImageFromStorageClickListeners.MULTIPLE_SELECTION);
                    } else {
                        Toast.makeText(context, "ready", Toast.LENGTH_SHORT).show();
//                        imageFromStorag.setChecked(!imageFromStorag.getChecked());
//                        constraintLayoutChecked.setVisibility(imageFromStorag.getChecked() ? View.VISIBLE : View.GONE);
                        imageFromStorageClickListeners.onImageClick(imageFromStorag,OnImageFromStorageClickListeners.SINGLE_SELECTION);
                    }
                    if (getSelectedImages().size()==0)
                    {
                        isTagged=false;
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    isTagged = true;
                    imageFromStorag.setChecked(!imageFromStorag.getChecked());
                    constraintLayoutChecked.setVisibility(imageFromStorag.getChecked() ? View.VISIBLE : View.GONE);
                    imageFromStorageClickListeners.onImageLongClick(imageFromStorag);
                    return true;
                }
            });

        }
    }
}
