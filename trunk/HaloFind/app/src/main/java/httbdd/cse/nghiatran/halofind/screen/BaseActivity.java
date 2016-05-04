package httbdd.cse.nghiatran.halofind.screen;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import de.greenrobot.event.EventBus;
import httbdd.cse.nghiatran.halofind.util.InterfaceHelper;
import httbdd.cse.nghiatran.halofind.util.UpdateUIsHandler;

public class BaseActivity extends AppCompatActivity implements UpdateUIsHandler {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InterfaceHelper.colorSystem(this);
    }

    public void onEvent(Object e) {
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


    BaseActivity.OnSearchListener mSearchCallback;


    @Override
    public void onSuccessAction(Object object, String key) {

    }


    public interface OnSearchListener {
        void search(String keyword);
    }

    public void switchToFragment(Fragment fragment, int _id) {
        String backStateName;
        backStateName = ((Object) fragment).getClass().getName();
        String fragmentTag = backStateName;
        FragmentManager manager = getSupportFragmentManager();

        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(_id, fragment, fragmentTag);
//            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }
    /**
     * Start Activity with bundle
     *
     * @param clazz
     * @param bundle
     */
    protected void startActivity(Class clazz, Bundle bundle) {
        try {
            Intent intent = new Intent(this, clazz);
            if (bundle != null)
                intent.putExtras(bundle);
            startActivity(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Start Activity without bundle
     *
     * @param clazz
     */
    protected void startActivity(Class clazz) {
        startActivity(clazz, null);
    }
}
