package httbdd.cse.nghiatran.halofind.foursquare;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.screen.PostActivity;
import httbdd.cse.nghiatran.halofind.util.InterfaceHelper;


// List Adapter for NearBy ListView

public class NearbyListAdapter extends ArrayAdapter<FoursquareModel> {

    SharedPreferences share;
    ArrayList<FoursquareModel> mNearByList;
    Activity activity;

    public NearbyListAdapter(Activity activity, int textViewResourceId,
                             ArrayList<FoursquareModel> objects) {
        super(activity, textViewResourceId, objects);
        this.mNearByList = objects;
        this.activity = activity;
        // TODO Auto-generated constructor stub
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View single_row = inflater.inflate(R.layout.row_layout, null, true);
        TextView tv_name = (TextView) single_row.findViewById(R.id.tv_name);
        TextView tv_address = (TextView) single_row.findViewById(R.id.tv_address);
        TextView tv_distance = (TextView) single_row.findViewById(R.id.tv_distance);
        ImageView icon = (ImageView) single_row.findViewById(R.id.icon);

        tv_name.setText(mNearByList.get(position).getName());
        tv_address.setText(mNearByList.get(position).getAddress());
        tv_distance.setText("~" + mNearByList.get(position).getDistance() + "m");
        Glide.with(getContext()).load(mNearByList.get(position).getCategoryIcon()).asBitmap().into(icon);
        single_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InterfaceHelper.showDialog(activity);
                share = PreferenceManager.getDefaultSharedPreferences(activity);
                if (share.getString(activity.getString(R.string.back), "").equalsIgnoreCase(activity.getString(R.string.back))) {
                    EventBus.getDefault().postSticky(mNearByList.get(position));
                }
                else {
                    PostActivity.startWithUri(getContext(), activity.getIntent().getData(), mNearByList.get(position));
                }
                share.edit().remove(activity.getString(R.string.back)).commit();
                activity.finish();
            }
        });
        return single_row;
    }
}
