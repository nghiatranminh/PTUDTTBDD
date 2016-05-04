package httbdd.cse.nghiatran.halofind.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import httbdd.cse.nghiatran.halofind.Model.Category;
import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.fragment.adapter.CategoryAdapter;
import httbdd.cse.nghiatran.halofind.util.HaloFindAPI;
import httbdd.cse.nghiatran.halofind.util.HaloFindHelper;


public class CategoryFragment extends BaseFragment {
    private static CategoryFragment fragment = null;
    RecyclerView recyclerView;
    CategoryAdapter categoryAdapter;

    public static CategoryFragment newInstance() {
        if (fragment == null)
            fragment = new CategoryFragment();
        return fragment;
    }

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        HaloFindAPI.getDatasnapshotCategory(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_category);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    public void onSuccessAction(Object object, String key) {
        super.onSuccessAction(object, key);
        if (key.equalsIgnoreCase(HaloFindHelper.CATEGORY)) {
            ArrayList<Category> categories = (ArrayList<Category>) object;
            categoryAdapter = new CategoryAdapter(getActivity(), categories);
            categoryAdapter.updateList(categories);
            recyclerView.setAdapter(categoryAdapter);
        }
    }
}
