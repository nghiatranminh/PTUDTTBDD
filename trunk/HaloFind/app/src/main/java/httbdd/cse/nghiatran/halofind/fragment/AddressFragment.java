package httbdd.cse.nghiatran.halofind.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import httbdd.cse.nghiatran.halofind.Model.District;
import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.fragment.adapter.AddressAdapter;
import httbdd.cse.nghiatran.halofind.util.HaloFindAPI;
import httbdd.cse.nghiatran.halofind.util.HaloFindHelper;

/**
 * Created by TranMinhNghia on 4/6/2016.
 */
public class AddressFragment extends BaseFragment {
    private static AddressFragment fragment = null;
    RecyclerView recyclerView;
    AddressAdapter addressAdapter;

    public static AddressFragment newInstance() {
        if (fragment == null)
            fragment = new AddressFragment();
        return fragment;
    }

    public AddressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        HaloFindAPI.getDatasnapshotDistrict(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_address);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }


    @Override
    public void onSuccessAction(Object object, String key) {
        super.onSuccessAction(object, key);
        if (key.equalsIgnoreCase(HaloFindHelper.DISTRICT)) {
            ArrayList<District> districts = (ArrayList<District>) object;
            addressAdapter = new AddressAdapter(getActivity(), districts);
            addressAdapter.updateList(districts);
            recyclerView.setAdapter(addressAdapter);
        }
    }
}

