package httbdd.cse.nghiatran.halofind.fragment.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import httbdd.cse.nghiatran.halofind.Model.Category;
import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.screen.DetailCategoryActivity;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {
    ArrayList<Category> categories;
    private Activity activity;

    public CategoryAdapter(Activity activity, ArrayList<Category> categories) {
        this.activity = activity;
        this.categories = categories;
    }

    public static class CategoryHolder extends RecyclerView.ViewHolder {
        CircleImageView icon;
        TextView title;
        RelativeLayout rlView;

        public CategoryHolder(View v) {
            super(v);
            icon = (CircleImageView) v.findViewById(R.id.category);
            title = (TextView) v.findViewById(R.id.title);
            rlView = (RelativeLayout) v.findViewById(R.id.rlView);
        }
    }

    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (context == null) context = activity;
        View rowview = LayoutInflater.from(context).inflate(R.layout.adapter_category, parent, false);
        return new CategoryHolder(rowview);
    }

    @Override
    public void onBindViewHolder(final CategoryHolder holder, final int position) {
        holder.title.setText(categories.get(position).getTitle());
        byte[] image = Base64.decode(categories.get(position).getImage(), Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
        Glide.with(activity).load(image).asBitmap().into(holder.icon);
        Palette.from(bmp).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                if (palette.getDarkVibrantSwatch() != null) {
//                    holder.title.setTextColor(Color.WHITE);
                    holder.rlView.setBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
                } else if (palette.getMutedSwatch() != null) {
//                    holder.title.setTextColor(Color.BLACK);
                    holder.rlView.setBackgroundColor(palette.getMutedSwatch().getRgb());
                }
                holder.rlView.getBackground().setAlpha(200);
            }
        });
//        holder.icon.setImageBitmap(bmp);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, DetailCategoryActivity.class);
                intent.putExtra(DetailCategoryActivity.TITLE, categories.get(position).getTitle());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void updateList(ArrayList<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

}
