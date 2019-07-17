package com.example.fburecipeapp;

//public class ItemsAdapter extends RecyclerView.Adapter<KitchenAdapter.ViewHolder> {

//    private Context context;
//    private List<Items>
//
//    public static class ViewHolder extends RecyclerView.ViewHolder  {
//
//        public TextView typeTextView;
//        public ImageButton addBtn;
//        public ImageView typeImage;
//
//        public ViewHolder(View itemView){
//            super(itemView);
//
//            typeTextView = itemView.findViewById(R.id.tvType);
//            addBtn = itemView.findViewById(R.id.addBtn);
//            typeImage = itemView.findViewById(R.id.imageViewType);
//        }
//
//    }
//
//    public ItemsAdapter (List<FoodType> types){
//        mTypes = types;
//    }
//
//    public KitchenAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
//        context = parent.getContext();
//        LayoutInflater inflater = LayoutInflater.from(context);
//        Log.d("Viewholder", "Viewholder success");
//
//        View postView = inflater.inflate(R.layout.kitchen_item, parent, false);
//        KitchenAdapter.ViewHolder viewHolder = new KitchenAdapter.ViewHolder(postView);
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull KitchenAdapter.ViewHolder holder, int position) {
//
//        FoodType foodType = mTypes.get(position);
//
//        holder.typeTextView.setText(foodType.getType());
//        holder.addBtn.setId(Integer.valueOf(foodType.getObject()));
//        ParseFile image = foodType.getImage();
//        if (image != null) {
//            Glide.with(context).load(image.getUrl()).into(holder.typeImage);
//
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        // Log.d("item count", String.format("%s" , mTypes.size()));
//        return mTypes.size();
//    }
//}
