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
import httbdd.cse.nghiatran.halofind.fragment.adapter.DetailCategoryAdapter;
import httbdd.cse.nghiatran.halofind.util.HaloFindAPI;
import httbdd.cse.nghiatran.halofind.util.HaloFindHelper;

/**
 * Created by TranMinhNghia_512023 on 4/12/2016.
 */
public class DetailCategoryFragment extends BaseFragment {
    private static DetailCategoryFragment fragment = null;
    RecyclerView recyclerView;
    DetailCategoryAdapter detailCategoryAdapter;

    public static DetailCategoryFragment newInstance() {
        if (fragment == null)
            fragment = new DetailCategoryFragment();
        return fragment;
    }

    public DetailCategoryFragment() {
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
        View view = inflater.inflate(R.layout.fragment_detail_category, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_detail_category);
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
                if (Normalizer.normalize(food.getTitle().toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").contains(Normalizer.normalize(getActivity().getIntent().getStringExtra(HaloFindHelper.TITLE).toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", ""))) {
                    foodss.add(food);
                }
            }
            detailCategoryAdapter = new DetailCategoryAdapter(getActivity(), foodss);
            detailCategoryAdapter.updateList(foodss);
            recyclerView.setAdapter(detailCategoryAdapter);
        }
    }
}

