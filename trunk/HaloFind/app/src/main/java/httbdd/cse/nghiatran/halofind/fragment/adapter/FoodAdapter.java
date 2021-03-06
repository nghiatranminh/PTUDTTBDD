package httbdd.cse.nghiatran.halofind.fragment.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import httbdd.cse.nghiatran.halofind.Model.Food;
import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.screen.DetailFoodActivity;
import httbdd.cse.nghiatran.halofind.util.HaloFindHelper;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodHolder> {
    ArrayList<Food> foods;
    private Activity activity;

    public FoodAdapter(Activity activity, ArrayList<Food> foods) {
        this.activity = activity;
        this.foods = foods;
    }

    public static class FoodHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        CircleImageView avatar;
        TextView title, description, name;
//        LinearLayout lnView;

        public FoodHolder(View v) {
            super(v);
            icon = (ImageView) v.findViewById(R.id.icon);
            title = (TextView) v.findViewById(R.id.title);
            name = (TextView) v.findViewById(R.id.name);
            avatar = (CircleImageView) v.findViewById(R.id.avatar);
            description = (TextView) v.findViewById(R.id.desctiption);
//            lnView = (LinearLayout) v.findViewById(R.id.lnView);
        }
    }

    @Override
    public FoodHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (context == null) context = activity;
        View rowview = LayoutInflater.from(context).inflate(R.layout.adapter_food, parent, false);
        return new FoodHolder(rowview);
    }

    @Override
    public void onBindViewHolder(final FoodHolder holder, final int position) {
        holder.title.setText(foods.get(position).getTitle());
        holder.description.setText(foods.get(position).getAddress());
        byte[] image = Base64.decode(foods.get(position).getImage(), Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
        Glide.with(activity).load(image).asBitmap().fitCenter().into(holder.icon);
        holder.name.setText(foods.get(position).getUsername());
        byte[] avatar = Base64.decode(foods.get(position).getUserimage(), Base64.DEFAULT);
        Glide.with(activity).load(avatar).asBitmap().fitCenter().into(holder.avatar);
//        Palette.from(bmp).generate(new Palette.PaletteAsyncListener() {
//            @Override
//            public void onGenerated(Palette palette) {
//                if (palette.getDarkVibrantSwatch() != null) {
//                    holder.title.setTextColor(Color.WHITE);
//                    holder.description.setTextColor(Color.WHITE);
//                    holder.lnView.setBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
//                } else if (palette.getMutedSwatch() != null) {
//                    holder.title.setTextColor(Color.BLACK);
//                    holder.description.setTextColor(Color.BLACK);
//                    holder.lnView.setBackgroundColor(palette.getMutedSwatch().getRgb());
//                }
//                holder.lnView.getBackground().setAlpha(200);
//            }
//        });
//        holder.icon.setImageBitmap(bmp);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, DetailFoodActivity.class);
                intent.putExtra(HaloFindHelper.FOOD, foods.get(position));
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public void updateList(ArrayList<Food> foods) {
        this.foods = foods;
        notifyDataSetChanged();
    }

//    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {
//
//        final float densityMultiplier = context.getResources().getDisplayMetrics().density;
//
//        int h = (int) (newHeight * densityMultiplier);
//        int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));
//
//        photo = Bitmap.createScaledBitmap(photo, w, h, true);
//
//        return photo;
//    }
}
