package httbdd.cse.nghiatran.halofind.screen;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.fragment.MainFragment;

/**
 * Created by TranMinhNghia_512023 on 4/9/2016.
 */
public class Details extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        if (savedInstanceState == null) {
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.add(R.id.fragment, MainFragment.newInstance(null));
            trans.commit();
        }

    }


}
