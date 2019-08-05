package com.example.fburecipeapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.fburecipeapp.R;
import com.example.fburecipeapp.fragments.DetailsFragment;
import com.example.fburecipeapp.models.Recipes;
import com.parse.ParseFile;

import java.util.ArrayList;

public class StaggeredRecyclerViewAdapter extends RecyclerView.Adapter<StaggeredRecyclerViewAdapter.ViewHolder>  {

    private static final String TAG = "StaggeredRecyclerViewAdapter";

    private ArrayList<Recipes> mRecipes = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private FragmentManager fragmentManager;
    private Context mContext;
    private ImageButton searchBtn;
    private Fragment recipeFragment;

    public StaggeredRecyclerViewAdapter(ArrayList<Recipes> mRecipes, ArrayList<String> mImages, Context mContext,
                                        FragmentManager fragmentManager, Fragment recipeFragment) {
        this.mRecipes = mRecipes;
        this.mImages = mImages;
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
        this.recipeFragment = recipeFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background);

        Recipes recipe = mRecipes.get(position);
        ParseFile image = recipe.getImage();


        Glide.with(mContext)
                .load(image.getUrl())
                .apply(requestOptions)
                .into(holder.recipeImg);
        holder.recipeName.setText(recipe.getName());


    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
         ImageView recipeImg;
         TextView recipeName;
         ImageButton searchBtn;

         View v = LayoutInflater.from(mContext).inflate(R.layout.fragment_recipe, null);

         public ViewHolder(View itemView) {
             super(itemView);
             this.recipeImg = itemView.findViewById(R.id.recipeImg);
             this.recipeName = itemView.findViewById(R.id.tvRecipe);
             this.searchBtn = v.findViewById(R.id.searchBtn);
             itemView.setOnClickListener(this);

//             searchBtn.setOnClickListener(new View.OnClickListener() {
//                 @Override
//                 public void onClick(View v) {
//                     Toast.makeText(mContext, "search clicked", Toast.LENGTH_SHORT).show();
//                     FragmentManager fm = ((AppCompatActivity)mContext).getSupportFragmentManager();
//                     if (fm != null) {
//                         FilterRecipeDialogFragment frag = FilterRecipeDialogFragment.newInstance();
//                         frag.setTargetFragment(recipeFragment, 0);
//                         frag.show(fm, "receipt_dialog_fragment");
//                     }
//                 }
//             });
         }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            Recipes recipe = mRecipes.get(position);
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                Fragment fragment = new DetailsFragment(recipe.getName(), recipe.getImage(), recipe.getAllIngredients(), recipe.getInstructions());
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContainer, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }
    }
}
