package httbdd.cse.nghiatran.halofind.screen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.fragment.DetailCategoryFragment;
import httbdd.cse.nghiatran.halofind.util.InterfaceHelper;

/**
 * Created by TranMinhNghia_512023 on 4/12/2016.
 */
public class DetailCategoryActivity extends BaseActivity {
    public static final String TITLE = "title";

    @Override
    protected void onStart() {
        super.onStart();
        setTitle(getIntent().getStringExtra(TITLE));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_category);
        InterfaceHelper.addActivityToolbar(this);
        InterfaceHelper.addSupportActionBar(this);
        switchToFragment(DetailCategoryFragment.newInstance(), R.id.frame_container);
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        getSupportFragmentManager().popBackStackImmediate();
        if (count == 1) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
