package com.example.myapplication.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.fragments.FavoriteFragment;
import com.example.myapplication.helpers.DarkModeHelper;
import com.example.myapplication.pojo.ImageToUpload;
import com.example.myapplication.pojo.Memory;
import com.example.myapplication.util.FavouriteAdapterListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoriteMemoryAdapter extends RecyclerView.Adapter<FavoriteMemoryAdapter.FavoriteItemHolder> {

    List<ImageToUpload> listOfimageToUploads;
    Context context;
    FavouriteAdapterListener favouriteAdapterListener;

    public void setFavouriteAdapterListener(FavouriteAdapterListener favouriteAdapterListener) {
        this.favouriteAdapterListener = favouriteAdapterListener;
    }

    public void setListOfimageToUploads(List<ImageToUpload> listOfimageToUploads) {
        this.listOfimageToUploads = listOfimageToUploads;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public FavoriteItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_favorite_images, null);
        return new FavoriteItemHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteItemHolder holder, int position) {

        ImageToUpload imageToUpload = listOfimageToUploads.get(position);
        Log.d("TAG", "onBindViewHolder: " + imageToUpload.getUrl());
        bind(imageToUpload, holder);
    }

    private void bind(ImageToUpload imageToUpload, FavoriteItemHolder holder) {
        if (imageToUpload.isImageFavorite()) {
            Picasso.get().load(imageToUpload.getUrl()).placeholder(DarkModeHelper.iSNight() ? R.color.darkPicassoPlaceHolder :
                    R.color.WhitePicassoPlaceHolder)
                    .into(holder.imageViewFavourite);
        }
    }

    @Override
    public int getItemCount() {
        return listOfimageToUploads.size();
    }

    class FavoriteItemHolder extends RecyclerView.ViewHolder {

        ImageView imageViewFavourite;

        public FavoriteItemHolder(@NonNull View itemView) {
            super(itemView);
            imageViewFavourite = itemView.findViewById(R.id.item_favourite_imageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
                    if (adapterPosition != -1) {
                        favouriteAdapterListener.onImageClick(listOfimageToUploads, listOfimageToUploads.get(adapterPosition), getAdapterPosition());
                    }

                }
            });
        }
    }
}
