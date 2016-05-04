package httbdd.cse.nghiatran.halofind.fragment.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import httbdd.cse.nghiatran.halofind.Model.District;
import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.screen.DetailAddressActivity;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressHolder> {
    ArrayList<District> districts;
    private Activity activity;

    public AddressAdapter(Activity activity, ArrayList<District> districts) {
        this.activity = activity;
        this.districts = districts;
    }

    public static class AddressHolder extends RecyclerView.ViewHolder {
        TextView title;

        public AddressHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
        }
    }

    @Override
    public AddressHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (context == null) context = activity;
        View rowview = LayoutInflater.from(context).inflate(R.layout.adapter_address, parent, false);
        return new AddressHolder(rowview);
    }

    @Override
    public void onBindViewHolder(final AddressHolder holder, final int position) {
        holder.title.setText("Quận " + districts.get(position).getDistrict());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, DetailAddressActivity.class);
                intent.putExtra(DetailAddressActivity.DISTRICT, districts.get(position).getDistrict());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return districts.size();
    }

    public void updateList(ArrayList<District> districts) {
        this.districts = districts;
        notifyDataSetChanged();
    }

}
