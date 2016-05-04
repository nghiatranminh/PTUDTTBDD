package httbdd.cse.nghiatran.halofind.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import de.greenrobot.event.EventBus;
import httbdd.cse.nghiatran.halofind.util.UpdateUIsHandler;

public class BaseFragment extends Fragment implements UpdateUIsHandler {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onEvent(Object e) {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isStickyAvailable()) {
            EventBus.getDefault().registerSticky(this);
        } else {
            EventBus.getDefault().register(this);
        }
    }

    protected boolean isStickyAvailable() {
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onSuccessAction(Object object, String key) {

    }

}
