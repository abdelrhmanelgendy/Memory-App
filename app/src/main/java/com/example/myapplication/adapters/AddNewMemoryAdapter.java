package com.example.myapplication.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.pojo.Image;
import com.example.myapplication.ui.AddNewMemory;
import com.example.myapplication.util.OnPictureClickListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddNewMemoryAdapter extends RecyclerView.Adapter<AddNewMemoryAdapter.CustomAdd> {
    private static final String TAG = "AddNewMemoryAdapter";
    public static boolean FROMEDITING = false;
    List<Image> imgUri = new ArrayList<>();
    Context context;
    OnPictureClickListener listener;
    Animation shakeAnimation;

    public void setListener(OnPictureClickListener listener) {
        this.listener = listener;
    }

    public AddNewMemoryAdapter(Context context) {
        this.context = context;
    }

    public void setImgUri(List<Image> imgUri) {
        this.imgUri = imgUri;
    }

    @NonNull
    @Override
    public CustomAdd onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.add_new_img_item, null);

        return new CustomAdd(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdd holder, int position) {
        Image image = imgUri.get(position);

        if (image.getId() == AddNewMemory.DEFAULT_IMAGE_ID) {
            holder.imageView.setImageResource(R.drawable.ic_baseline_add_a_photo_24);
            holder.imageViewDelete.setVisibility(View.GONE);

        } else {

            Picasso.get().load(image.getUri())
                    .resize(500, 500)
                    .centerCrop()
                    .into(holder.imageView);


        }


//        }


    }


    @Override
    public int getItemCount() {
        return imgUri.size();
    }

    class CustomAdd extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView imageViewDelete;

        public CustomAdd(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.addItem_Imag);
            imageViewDelete = itemView.findViewById(R.id.addItem_ImagDelete);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Image image = imgUri.get(position);

                        listener.onClick(image);
                    }
                }
            });
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Image image = imgUri.get(position);

                        listener.onLongClick(image, imageView);
                    }
                    return true;
                }
            });
            imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Image image = imgUri.get(position);

                        listener.onEditeClick(image);
                    }
                }
            });


        }
    }
}
