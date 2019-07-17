package com.example.fburecipeapp;

//public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
//
//    public Context context;
//    public JSONArray[] mItems;
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        context = parent.getContext();
//        LayoutInflater inflater = LayoutInflater.from(context);
//
//        View postView = inflater.inflate(R.layout.single_food_option, parent, false);
//        ViewHolder viewHolder = new ViewHolder(postView);
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        FoodType foodType = mItems.get(position);
//
//        holder.foodItem.setText(foodType.getItems());
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return 0;
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//
//        public TextView foodItem;
//        public CheckBox checkBox;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//
//            foodItem = itemView.findViewById(R.id.tvFoodItem);
//            checkBox = itemView.findViewById(R.id.checkBox);
//        }
//
//    }
//
//    public ItemsAdapter (JSONArray[] items) {
//        mItems = items;
//    }
//
//
//}