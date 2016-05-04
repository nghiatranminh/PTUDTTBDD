package httbdd.cse.nghiatran.halofind.screen;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.text.Normalizer;
import java.util.ArrayList;

import httbdd.cse.nghiatran.halofind.Model.Food;
import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.fragment.adapter.SearchAdapter;
import httbdd.cse.nghiatran.halofind.util.HaloFindAPI;
import httbdd.cse.nghiatran.halofind.util.HaloFindHelper;
import httbdd.cse.nghiatran.halofind.util.InterfaceHelper;


/**
 * Created by TranMinhNghia_512023 on 11/10/2015.
 */
public class SearchActivity extends BaseActivity {
    RecyclerView recyclerView;
    SearchAdapter searchAdapter;
    ArrayList<Food> foodsearch;

    @Override
    protected void onStart() {
        super.onStart();
        HaloFindAPI.getDatasnapshotFood(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Network.isAvailiable(this)) {
////            InterfaceHelper.showDialog(this);
//        }
//        else {
//            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "You are offline!", Snackbar.LENGTH_INDEFINITE)
//                    .setAction("Action", null);
//            InterfaceHelper.confirm(snackbar).show();
//        }
        setTitle(this.getIntent().getExtras().getString(HaloFindHelper.KEY_WORK));
        setContentView(R.layout.activity_search_screen);
        InterfaceHelper.addActivityToolbar(this);
        InterfaceHelper.addSupportActionBar(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_search);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onSuccessAction(Object object, String key) {
        super.onSuccessAction(object, key);
        ArrayList<Food> foods = (ArrayList<Food>) object;
        foodsearch = new ArrayList<Food>();
        if (key.equalsIgnoreCase(HaloFindHelper.FOOD)) {
            for (Food food : foods) {
                if (Normalizer.normalize(food.getTitle().toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").contains(Normalizer.normalize(getIntent().getStringExtra(HaloFindHelper.KEY_WORK).toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")) || Normalizer.normalize(food.getAddress().toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").contains(Normalizer.normalize(getIntent().getStringExtra(HaloFindHelper.KEY_WORK).toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")) || Normalizer.normalize(food.getDistrict().toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").contains(Normalizer.normalize(getIntent().getStringExtra(HaloFindHelper.KEY_WORK).toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")) || Normalizer.normalize(food.getCategory().toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").contains(Normalizer.normalize(getIntent().getStringExtra(HaloFindHelper.KEY_WORK).toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", ""))) {
                    foodsearch.add(food);
                }
            }
            searchAdapter = new SearchAdapter(this, new ArrayList<Food>());
            searchAdapter.updateList(foodsearch);
            recyclerView.setAdapter(searchAdapter);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
