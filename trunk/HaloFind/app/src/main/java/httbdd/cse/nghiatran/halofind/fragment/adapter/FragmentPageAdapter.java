package httbdd.cse.nghiatran.halofind.fragment.adapter;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import httbdd.cse.nghiatran.halofind.R;

/**
 * Created by Tuan on 6/18/2015.
 */
public class FragmentPageAdapter extends FragmentStatePagerAdapter {
    private Context mContext;

    private List<Fragment> mFragments = new ArrayList<>();
    private List<Integer> mFragmentIcons = new ArrayList<>();

    public FragmentPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.mContext = context;
    }

    public void addFragment(Fragment fragment, int drawable) {
        mFragments.add(fragment);
        mFragmentIcons.add(drawable);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }


    public View getTabView(int position) {
        View tab = LayoutInflater.from(mContext).inflate(R.layout.tabbar_view, null);
        TextView tabText = (TextView) tab.findViewById(R.id.tabText);
        ImageView tabImage = (ImageView) tab.findViewById(R.id.tabImage);
        tabImage.setBackgroundResource(mFragmentIcons.get(position));
        if (position == 0) {
            tab.setSelected(true);
        }
        return tab;
    }
}
