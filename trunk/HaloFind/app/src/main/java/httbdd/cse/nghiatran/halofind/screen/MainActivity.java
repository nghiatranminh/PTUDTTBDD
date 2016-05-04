/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package httbdd.cse.nghiatran.halofind.screen;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import httbdd.cse.nghiatran.halofind.Model.User;
import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.foursquare.SearchLocation;
import httbdd.cse.nghiatran.halofind.fragment.AddressFragment;
import httbdd.cse.nghiatran.halofind.fragment.CategoryFragment;
import httbdd.cse.nghiatran.halofind.fragment.FoodFragment;
import httbdd.cse.nghiatran.halofind.fragment.ProfileFragment;
import httbdd.cse.nghiatran.halofind.fragment.adapter.FragmentPageAdapter;
import httbdd.cse.nghiatran.halofind.util.HaloFindAPI;
import httbdd.cse.nghiatran.halofind.util.HaloFindHelper;
import httbdd.cse.nghiatran.halofind.util.InterfaceHelper;


public class MainActivity extends BaseActivity {
    private FragmentPageAdapter pageAdapter;
    String library, recents, favourites, notifications, settings;
    public static final String OBJECTID = "ObjectId";
    String objectId;
    User user;
    ViewPager viewPager;
    TabLayout tabLayout;
    FloatingActionButton add;
    private static final int REQUEST_SELECT_PICTURE = 0x01;
    private Uri mDestinationUri;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage.jpeg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        objectId = getIntent().getStringExtra(OBJECTID);
        InterfaceHelper.showDialog(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        HaloFindAPI.getDatasnapshotUsers(this);
//        if (Network.isAvailiable(this))
//            InterfaceHelper.showDialog(this);
//        else {
//            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "You are offline!", Snackbar.LENGTH_INDEFINITE)
//                    .setAction("Action", null);
//            InterfaceHelper.confirm(snackbar).show();
//        }
        mDestinationUri = Uri.fromFile(new File(getCacheDir(), SAMPLE_CROPPED_IMAGE_NAME));
        add = (FloatingActionButton) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, httbdd.cse.nghiatran.halofind.slidinguppanel.map.SlidingUpMap.class);
//                startActivity(intent);
                if (objectId != null) {
//                    Intent intent = new Intent(getApplicationContext(), PostActivity.class);
//                    startActivity(intent);
                    pickFromGallery();
                } else {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setMessage(getString(R.string.need_login)).
                            setPositiveButton(android.R.string.yes, dialogClickListener).
                            setNegativeButton(android.R.string.no, dialogClickListener).show();
                }

            }
        });


        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        if (objectId != null) {
            add.setVisibility(View.VISIBLE);
        } else {
            add.setVisibility(View.GONE);
        }
        viewPager = (ViewPager) findViewById(R.id.viewpager);
//        if (viewPager != null) {
//            setupViewPager(viewPager);
//        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);
        ///////////////

        setupViewPager(viewPager);
        setupTabLayout(tabLayout);

        /////////////////////////

    }

    private void pickFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), REQUEST_SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_PICTURE) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startCropActivity(data.getData());
                } else {
                    Toast.makeText(MainActivity.this, R.string.toast_cannot_retrieve_selected_image, Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        }
    }

    private void startCropActivity(@NonNull Uri uri) {
        UCrop uCrop = UCrop.of(uri, mDestinationUri);
        uCrop = advancedConfig(uCrop);
        uCrop.start(MainActivity.this);
    }

    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        return uCrop.withOptions(options);
    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            SearchLocation.startWithUri(MainActivity.this, resultUri);
//            PostActivity.startWithUri(FoursquareActivity.this, resultUri);
        } else {
            Toast.makeText(MainActivity.this, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Toast.makeText(MainActivity.this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, R.string.toast_unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }
////////////////

    public void setupViewPager(ViewPager viewPager) {
        pageAdapter = new FragmentPageAdapter(getApplicationContext(), getSupportFragmentManager());
        pageAdapter.addFragment(FoodFragment.newInstance(), R.drawable.ic_tabbar_home);
        pageAdapter.addFragment(CategoryFragment.newInstance(), R.drawable.ic_tabbar_category);
        pageAdapter.addFragment(AddressFragment.newInstance(), R.drawable.ic_tabbar_address);
        if (objectId != null) {
            pageAdapter.addFragment(ProfileFragment.newInstance(), R.drawable.ic_tabbar_settings);
        }
        viewPager.setAdapter(pageAdapter);
    }

    public void setupTabLayout(TabLayout tabLayout) {
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pageAdapter.getTabView(i));
        }
        tabLayout.requestFocus();
    }

    ////////////////////////
//    private void setupViewPager(ViewPager viewPager) {
//        Adapter adapter = new Adapter(getSupportFragmentManager());
//        adapter.addFragment(new FoodFragment(), "Food");
//        adapter.addFragment(new CategoryFragment(), "Category");
//        adapter.addFragment(new AddressFragment(), "HCM city");
//        if (objectId != null) {
//            adapter.addFragment(new ProfileFragment(), "Profile");
//        }
//        viewPager.setAdapter(adapter);
//    }

//    static class Adapter extends FragmentPagerAdapter {
//        private final List<Fragment> mFragments = new ArrayList<>();
//        private final List<String> mFragmentTitles = new ArrayList<>();
//
//        public Adapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        public void addFragment(Fragment fragment, String title) {
//            mFragments.add(fragment);
//            mFragmentTitles.add(title);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragments.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragments.size();
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mFragmentTitles.get(position);
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sample_actions, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                handleSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                } else {
                }
                return true;
            }
        });
        return true;
    }


    private void handleSearch(String query) {
        search(query.trim());
    }

    public void search(String keyword) {
        invalidateOptionsMenu();
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        intent.putExtra(HaloFindHelper.KEY_WORK, keyword);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    boolean mDoubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (mDoubleBackToExitPressedOnce) {
            super.onBackPressed();
            StartActivity.REMEMBER_ME = true;
            return;
        }

        mDoubleBackToExitPressedOnce = true;
        Toast toast = Toast.makeText(this, getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        v.setTextColor(Color.WHITE);
        toast.show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mDoubleBackToExitPressedOnce = false;   //reset mDoubleBackToExitPressedOnce to false after 2 seconds
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().removeAllStickyEvents();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (user != null) {
            EventBus.getDefault().postSticky(user);
        }
    }

    @Override
    public void onSuccessAction(Object object, String key) {
        super.onSuccessAction(object, key);
        if (key.equalsIgnoreCase(HaloFindHelper.USERS)) {
            ArrayList<User> users = (ArrayList<User>) object;
            for (User user : users) {
                if (user.getEmail().equalsIgnoreCase(objectId)) {
                    this.user = user;
                    EventBus.getDefault().postSticky(user);
                    break;
                }
            }
        }
    }

}
