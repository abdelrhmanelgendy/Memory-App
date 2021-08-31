package com.example.myapplication.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.pojo.Memory;
import com.example.myapplication.util.MemoryClickListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragmentAllMemories extends RecyclerView.Adapter<HomeFragmentAllMemories.AllMemoryHolder> {

    List<Memory> list;
    Context context;

    List<Integer> colorList = new ArrayList<>();
    MemoryClickListener memoryClickListener;

    public void setMemoryClickListener(MemoryClickListener memoryClickListener) {
        this.memoryClickListener = memoryClickListener;
    }

    public HomeFragmentAllMemories(Context context) {
        this.context = context;
        colorList.add(context.getResources().getColor(R.color.card1));
        colorList.add(context.getResources().getColor(R.color.card2));
        colorList.add(context.getResources().getColor(R.color.card3));
    }

    public void setList(List<Memory> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public AllMemoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.old_memory_item, null);

        return new AllMemoryHolder(view);
    }

    int x = 0;

    @Override
    public void onBindViewHolder(@NonNull AllMemoryHolder holder, int position) {



        setCardColor(holder);

holder.cardView.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recycler_view_slide_down));
        Memory memory = list.get(position);
        if (memory == null) {
            return;
        }
        if (memory.getMainPicUrl().

                length() < 4) {
            Picasso.get().load(memory.getPictures().get(0).getUrl()).resize(500,500).into(holder.circleImageView);
        } else {
            Picasso.get().load(memory.getMainPicUrl()).resize(500,500)
                    .centerCrop().into(holder.circleImageView);
        }
        holder.txtImagesCount.setText(memory.getPicturesCount() + "");
        holder.txtMemoryTitle.setText(memory.getTittle());
        holder.txtMemortDate.setText(

                getDate(memory.getTimeInMillis()) + "");

    }

    private void setCardColor(AllMemoryHolder holder) {
        x++;

        if (x == 1) {
            holder.cardView.setCardBackgroundColor(colorList.get(0));
            return;
        } else if (x == 2) {
            holder.cardView.setCardBackgroundColor(colorList.get(1));
            return;
        } else if (x == 3) {
            holder.cardView.setCardBackgroundColor(colorList.get(2));
            x = 0;
            return;
        }
    }

    private String getDate(String timeInMillis) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(Long.parseLong(timeInMillis));
        int day = instance.get(Calendar.DAY_OF_MONTH);
        int month = instance.get(Calendar.MONTH);
        int year = instance.get(Calendar.YEAR);
        return day + "-" + month + "-" + year;

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class AllMemoryHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView txtImagesCount;
        TextView txtMemoryTitle, txtMemortDate;
        CardView cardView;

        public AllMemoryHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.old_memory_imgMain);
            txtImagesCount = itemView.findViewById(R.id.old_memory_TV_count);
            txtMemoryTitle = itemView.findViewById(R.id.old_memory_TV_name);
            txtMemortDate = itemView.findViewById(R.id.old_memory_TV_DateTime);
            cardView = itemView.findViewById(R.id.old_memory_CardMomentsiew);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != -1) {
                        if (list.get(position) != null) {
                            memoryClickListener.onMemoryClick(list.get(position));
                        }

                    }
                }
            });
        }
    }
}
