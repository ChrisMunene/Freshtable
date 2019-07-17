package com.example.fburecipeapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import java.util.List;

public class KitchenAdapter extends RecyclerView.Adapter<KitchenAdapter.ViewHolder> {

        public List<FoodType> mTypes;
        public Context context;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView typeTextView;
            public ImageButton addBtn;
            public ImageView typeImage;
            public Context context;
            public CardView card;

            public ViewHolder(View itemView) {
                super(itemView);

                typeTextView = itemView.findViewById(R.id.tvType);
                addBtn = itemView.findViewById(R.id.addBtn);
                typeImage = itemView.findViewById(R.id.imageViewType);
                card = itemView.findViewById(R.id.card_view);
                card.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(v.getContext(), KitchenMenuActivity.class);
                    v.getContext().startActivity(intent);
                }

            }
        }


        public KitchenAdapter (List<FoodType> types) {
            mTypes = types;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
                context = parent.getContext();
                LayoutInflater inflater = LayoutInflater.from(context);
                Log.d("Viewholder", "Viewholder success");

                View postView = inflater.inflate(R.layout.kitchen_item, parent, false);
                ViewHolder viewHolder = new ViewHolder(postView);
                return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

                FoodType foodType = mTypes.get(position);

                holder.typeTextView.setText(foodType.getType());
                ParseFile image = foodType.getImage();
                if (image != null) {
                        Glide.with(context).load(image.getUrl()).into(holder.typeImage);

                }
        }

        @Override
        public int getItemCount() {
               // Log.d("item count", String.format("%s" , mTypes.size()));
                return mTypes.size();
        }
}

