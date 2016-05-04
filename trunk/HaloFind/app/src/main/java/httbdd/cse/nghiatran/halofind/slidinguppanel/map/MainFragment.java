/**
 * Copyright 2015-present Amberfog
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package httbdd.cse.nghiatran.halofind.slidinguppanel.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;

import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.foursquare.GPSTracker;

public class MainFragment extends Fragment implements  OnMapReadyCallback {

    private static final String ARG_LOCATION = "arg.location";
    protected double latitude, longitude;
    // private LockableListView mListView;
    private SlidingUpPanelLayout mLayout;
    public MapView mapView;
    // ListView stuff
    //private View mTransparentHeaderView;
    //private View mSpaceView;
    private View panelSpaceView;
    private View panelTransparentView;
    private LockableScrollView mScrollView;

    private LatLng mLocation;
    private Marker mLocationMarker;

    private SupportMapFragment mMapFragment;

    private MapboxMap mMap;
    private boolean mIsNeedLocationUpdate = true;
    GPSTracker gps;

    public MainFragment() {
    }

    public static MainFragment newInstance(LatLng location) {
        MainFragment f = new MainFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOCATION, location);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mScrollView = (LockableScrollView) rootView.findViewById(android.R.id.list);
        panelTransparentView = rootView.findViewById(R.id.transparentView);
        panelSpaceView = rootView.findViewById(R.id.space);

        mLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);

        //tell our SlidingPanel, that there is ScrollView
        mLayout.setScrollableView(
                mScrollView,
                getResources().getDimensionPixelSize(R.dimen.sliding_panel_padding)
        );
        //tell our SlidingPanel the height + the offset height we want after expanding
        mLayout.setPanelHeight(
                getResources().getDimensionPixelSize(R.dimen.sliding_panel_height) +
                        getResources().getDimensionPixelSize(R.dimen.sliding_panel_padding)
        );
        //don't forget to enable dragViewTouchEvents
        mLayout.setEnableDragViewTouchEvents(true);
        gps = new GPSTracker(getActivity());

        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

        } else {

            gps.showSettingsAlert();
        }
        mapView.getMapAsync(this);

        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
//                setActionBarTranslation(mLayout.getCurrentParalaxOffset());
            }

            @Override
            public void onPanelExpanded(View panel) {

                //make visible space view inside scrollView
                panelSpaceView.setVisibility(View.VISIBLE);
                //make gone space view outside scrollView
                panelTransparentView.setVisibility(View.GONE);
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 1000, null);
                }
                //enable scrolling in scrollView
                mScrollView.setScrollingEnabled(true);
            }

            @Override
            public void onPanelCollapsed(View panel) {

                //make gone space view inside scrollView
                panelSpaceView.setVisibility(View.GONE);
                //make invisible space view outside scrollView
                panelTransparentView.setVisibility(View.INVISIBLE);
                panelTransparentView.setVisibility(View.GONE);
                if (mMap != null && mLocation != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 11f), 1000, null);
                }
                //disable scrolling in scrollView (so it can't intercept our gestures, that is opening panel)
                mScrollView.setScrollingEnabled(false);
            }

            @Override
            public void onPanelAnchored(View panel) {
            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });
//        mLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                mLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                mLayout.onPanelDragged(0);
//            }
//        });
        return rootView;
    }

//
//    private void collapseMap() {
//        if (mHeaderAdapter != null) {
//            mHeaderAdapter.showSpace();
//        }
//        mWhiteSpaceView.setVisibility(View.VISIBLE);
//        mTransparentView.setVisibility(View.GONE);
//        if (mMap != null && mLocation != null) {
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 11f), 1000, null);
//        }
//        mListView.setScrollingEnabled(true);
//    }

//    private void expandMap() {
//        if (mHeaderAdapter != null) {
//            mHeaderAdapter.hideSpace();
//        }
//        mWhiteSpaceView.setVisibility(View.GONE);
//        mTransparentView.setVisibility(View.INVISIBLE);
//        if (mMap != null) {
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 1000, null);
//        }
//        mListView.setScrollingEnabled(false);
//    }

//    @Override
//    public void onPanelSlide(View view, float v) {
//    }
//
//    @Override
//    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
//
//    }

//    @Override
//    public void onPanelCollapsed(View view) {
//        expandMap();
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                new LatLng(latitude, longitude), 13));
//    }
//
//    @Override
//    public void onPanelExpanded(View view) {
//        collapseMap();
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                new LatLng(latitude, longitude), 13));
//    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mMap = mapboxMap;
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(latitude, longitude), 13));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)) // Sets the center
                // of the map to
                // location user
                .zoom(17) // Sets the zoom
                .build(); // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }
}
