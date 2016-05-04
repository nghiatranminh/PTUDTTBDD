package httbdd.cse.nghiatran.halofind.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import httbdd.cse.nghiatran.halofind.Model.Food;
import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.fragment.adapter.FoodAdapter;
import httbdd.cse.nghiatran.halofind.util.HaloFindAPI;
import httbdd.cse.nghiatran.halofind.util.HaloFindHelper;
import httbdd.cse.nghiatran.halofind.util.InterfaceHelper;


public class FoodFragment extends BaseFragment {
    private static FoodFragment fragment = null;
    RecyclerView recyclerView;
    FoodAdapter foodAdapter;
    String objectId;

    public static FoodFragment newInstance() {
        if (fragment == null)
            fragment = new FoodFragment();
        return fragment;
    }

    public FoodFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        HaloFindAPI.getDatasnapshotFood(this);
        objectId = getActivity().getIntent().getStringExtra("ObjectId");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_food);
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
            foodAdapter = new FoodAdapter(getActivity(), foods);
            foodAdapter.updateList(foods);
            recyclerView.setAdapter(foodAdapter);
            if (InterfaceHelper.Dialog.isShowing() && InterfaceHelper.Dialog != null) {
                InterfaceHelper.Dialog.dismiss();
            }
        }
    }
}
