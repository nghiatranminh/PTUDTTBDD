package httbdd.cse.nghiatran.halofind.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.Normalizer;
import java.util.ArrayList;

import httbdd.cse.nghiatran.halofind.Model.Food;
import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.fragment.adapter.DetailAddressAdapter;
import httbdd.cse.nghiatran.halofind.util.HaloFindAPI;
import httbdd.cse.nghiatran.halofind.util.HaloFindHelper;

/**
 * Created by TranMinhNghia_512023 on 4/13/2016.
 */
public class DetailAddressFragment  extends BaseFragment {
    private static DetailAddressFragment fragment = null;
    RecyclerView recyclerView;
    DetailAddressAdapter detailAddressAdapter;

    public static DetailAddressFragment newInstance() {
        if (fragment == null)
            fragment = new DetailAddressFragment();
        return fragment;
    }

    public DetailAddressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        HaloFindAPI.getDatasnapshotFood(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_address, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_detail_address);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    public void onSuccessAction(Object object, String key) {
        super.onSuccessAction(object, key);
        if (key.equalsIgnoreCase(HaloFindHelper.FOOD)) {
            ArrayList<Food> foods = (ArrayList<Food>) object;
            ArrayList<Food> foodss = new ArrayList<>();
            for (Food food : foods) {
                if (Normalizer.normalize(food.getDistrict().toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").contains(Normalizer.normalize(getActivity().getIntent().getStringExtra(HaloFindHelper.DISTRICT).toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", ""))) {
                    foodss.add(food);
                }
            }
            detailAddressAdapter = new DetailAddressAdapter(getActivity(), foodss);
            detailAddressAdapter.updateList(foodss);
            recyclerView.setAdapter(detailAddressAdapter);
        }
    }
}


