package com.example.myapplication.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;
import com.example.myapplication.helpers.CheckInternetConnection;
import com.example.myapplication.helpers.DarkModeHelper;
import com.example.myapplication.helpers.SoundsPlayer;
import com.example.myapplication.pojo.ImageToUpload;
import com.example.myapplication.pojo.Memory;
import com.example.myapplication.util.ImagePreviewClickListener;
import com.example.myapplication.util.OnImageFromStorageClickListeners;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MemoryViewerAdapter extends RecyclerView.Adapter<MemoryViewerAdapter.MemoryHolder> {

    private static final String TAG = "MemoryViewerAdapter";
    private List<DownloadedImage> imagesUris = new ArrayList<>();
    private Context context;
    ImagePreviewClickListener imagePreviewClickListener;
    private List<DownloadedImage> selectedImages = new ArrayList<>();
    private List<ImageToUpload> imageToUploads = new ArrayList<>();
    View view;
    Animation animation;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void setSelectedImages(List<DownloadedImage> selectedImages) {
        this.selectedImages = selectedImages;
    }

    public List<ImageToUpload> getImageToUploads() {
        return imageToUploads;
    }

    public void setImageToUploads(List<ImageToUpload> imageToUploads) {
        this.imageToUploads = imageToUploads;
    }

    public List<DownloadedImage> getSelectedImages() {
        return selectedImages;
    }

    public void setImagePreviewClickListener(ImagePreviewClickListener imagePreviewClickListener) {
        this.imagePreviewClickListener = imagePreviewClickListener;
    }

    public MemoryViewerAdapter(List<DownloadedImage> imagesUris, Context context) {
        this.imagesUris = imagesUris;
        this.context = context;
    }

    @NonNull
    @Override
    public MemoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        animation = AnimationUtils.loadAnimation(context, R.anim.liked_image);

        return new MemoryHolder(LayoutInflater.from(context).inflate(R.layout.image_viewer_image_item, null));
    }

    private boolean notifyImagesAlso = false;

    public boolean isNotifyImagesAlso() {
        return notifyImagesAlso;
    }

    public void setNotifyImagesAlso(boolean notifyImagesAlso) {
        this.notifyImagesAlso = notifyImagesAlso;
    }

    @Override
    public void onBindViewHolder(@NonNull MemoryHolder holder, int position) {

        String imgUrl = imagesUris.get(position).getImgUrl();


        holder.bind(imagesUris.get(position));
        if (notifyImagesAlso) {

        }

        Picasso.get().load(imgUrl)
                .placeholder(DarkModeHelper.iSNight() ? R.color.darkPicassoPlaceHolder : R.color.WhitePicassoPlaceHolder)
                .resize(700, 900).centerCrop()
                .into(holder.imgMemoryImage);
        Log.d(TAG, "onBindViewHolder: " + position);
        setView(holder.itemView);
        readUserData(holder, position);


    }

    private void readUserData(MemoryHolder holder, int position) {
        if (imageToUploads.get(position).isImageFavorite()) {
            holder.imgMemoryImageAddFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite_red));
        } else {
            holder.imgMemoryImageAddFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favoritr_white));
        }
    }


    @Override
    public int getItemCount() {
        return imagesUris.size();
    }

    public boolean isTagged;
    public boolean stillInProgress = false;


    public void setTagged(boolean tagged) {
        isTagged = tagged;
    }

    protected class MemoryHolder extends RecyclerView.ViewHolder {
        ImageView imgMemoryImage;
        ImageView imgMemoryImageCheck;
        ImageView imgMemoryImageAddFavorite;
        LottieAnimationView lottieAnimationView;
        CardView cardView;

        public MemoryHolder(@NonNull View itemView) {
            super(itemView);

            imgMemoryImage = itemView.findViewById(R.id.imgeView_ImageViewAdapter);
            cardView = itemView.findViewById(R.id.memoryViewerAdpter_Cardview);
            imgMemoryImageCheck = itemView.findViewById(R.id.imgeView_ImageViewAdapterChecked);
            imgMemoryImageAddFavorite = itemView.findViewById(R.id.imgeView_ImageViewAdapterAddFavorite);
            lottieAnimationView = itemView.findViewById(R.id.memoryViewer_lottieAnimation);
            imgMemoryImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    isTagged = true;
                    int adapterPosition = getAdapterPosition();
                    DownloadedImage downloadedImage = imagesUris.get(adapterPosition);

                    if (!downloadedImage.checked) {
                        downloadedImage.setChecked(true);
                        selectedImages.add(downloadedImage);
                        imgMemoryImageCheck.setVisibility(View.VISIBLE);
                    } else {
                        downloadedImage.setChecked(false);
                        selectedImages.remove(downloadedImage);
                        imgMemoryImageCheck.setVisibility(View.INVISIBLE);
                    }
                    imagePreviewClickListener.onImageLongClick(adapterPosition, selectedImages, imgMemoryImage);
                    return true;
                }
            });
            imgMemoryImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedImages.size() == 0) {
                        isTagged = false;
                    }
                    Log.d(TAG, "onClick: " + isTagged);
                    int adapterPosition = getAdapterPosition();
                    DownloadedImage downloadedImage = imagesUris.get(adapterPosition);
                    if (isTagged) {

                        if (!downloadedImage.checked) {
                            downloadedImage.setChecked(true);
                            selectedImages.add(downloadedImage);
                            imgMemoryImageCheck.setVisibility(View.VISIBLE);
                        } else {
                            downloadedImage.setChecked(false);
                            selectedImages.remove(downloadedImage);
                            imgMemoryImageCheck.setVisibility(View.INVISIBLE);
                        }
                        imagePreviewClickListener.onImageLongClick(adapterPosition, selectedImages, imgMemoryImage);
                    } else {
                        imagePreviewClickListener.onImageClick(getAdapterPosition(), downloadedImage.getImgUrl(), imgMemoryImage);
                    }
                    if (selectedImages.size() == 0) {
                        isTagged = false;
                    }
                }
            });

            imgMemoryImageAddFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (CheckInternetConnection.connection(context)) {


                    ImageToUpload imageToUpload = getImageToUploads().get(getAdapterPosition());
                    if (imageToUpload.isImageFavorite()) {
                        imageToUpload.setImageFavorite(false);
                        SoundsPlayer soundsPlayer =new SoundsPlayer(context,R.raw.memory_dis_like);
                        soundsPlayer.play();
                        imgMemoryImageAddFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favoritr_white));

                        imagePreviewClickListener.onImageFavoriteClick(getAdapterPosition(), imageToUpload.getUrl(), true);
                    } else {

                        lottieAnimationView.playAnimation();

                        imageToUpload.setImageFavorite(true);
                        SoundsPlayer soundsPlayer =new SoundsPlayer(context,R.raw.memory_like);
                        soundsPlayer.play();

                        imgMemoryImageAddFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite_red));
                        imagePreviewClickListener.onImageFavoriteClick(getAdapterPosition(), imageToUpload.getUrl(), false);


                        lottieAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                lottieAnimationView.setVisibility(View.GONE);
                                super.onAnimationEnd(animation);
                            }
                        });


                    }
                    }
                    else
                    {
                        ImageToUpload imageToUpload = getImageToUploads().get(getAdapterPosition());

                        imagePreviewClickListener.onImageFavoriteClick(getAdapterPosition(), imageToUpload.getUrl(), true);

                    }

                }
            });



        }

        public void bind(DownloadedImage image) {
            if (isTagged) {
                imgMemoryImageCheck.setVisibility(image.isChecked() ? View.VISIBLE : View.GONE);

            }


        }
    }
}
