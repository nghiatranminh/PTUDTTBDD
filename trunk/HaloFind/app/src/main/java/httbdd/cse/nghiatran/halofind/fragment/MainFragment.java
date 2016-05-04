/**
 * Copyright 2015-present Amberfog
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package httbdd.cse.nghiatran.halofind.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationRequest;
import com.mapbox.directions.service.models.DirectionsRoute;
import com.mapbox.directions.service.models.Waypoint;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.slidinguppanel.map.SlidingUpPanelLayout;
import httbdd.cse.nghiatran.halofind.util.PermissionUtils;

public class MainFragment extends BaseFragment implements MapboxMap.OnMyLocationChangeListener, OnMapReadyCallback, com.mapbox.mapboxsdk.location.LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {
    public LocationManager locationManager;
    private MapView mapView;
    protected String mLastUpdateTime;
    public MapboxMap mapboxMap;
    private DirectionsRoute currentRoute = null;
    protected LocationRequest mLocationRequest;
    protected Boolean mRequestingLocationUpdates = true;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
//    double lat = 0;
private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private static final String ARG_LOCATION = "arg.location";
//    double longt = 0;
//    String title = "";
//    String addresss = "";
    public static MainFragment newInstance(LatLng location) {
        MainFragment f = new MainFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOCATION, location);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.demo, container, false);
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.slidingLayout);
        mSlidingUpPanelLayout.setEnableDragViewTouchEvents(true);

        int mapHeight = getResources().getDimensionPixelSize(R.dimen.map_height);
        mSlidingUpPanelLayout.setPanelHeight(mapHeight); // you can use different height here

        mSlidingUpPanelLayout.setPanelSlideListener((SlidingUpPanelLayout.PanelSlideListener) getActivity());
        mapView = (MapView) rootView.findViewById(R.id.mapView);
//        lat = getIntent().getDoubleExtra("lat", lat);
//        longt = getIntent().getDoubleExtra("long", longt);
//        addresss = getIntent().getStringExtra("address");
//        title = getIntent().getStringExtra("title");
        mapView.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        updateValuesFromBundle(savedInstanceState);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setMessage("GPS is disabled in your device. Would you like to enable it?");
            alert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.setNegativeButton("Go to Settings", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    Intent I = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(I);
                }
            });
            AlertDialog al_gps = alert.create();
            al_gps.show();
        } else {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) this);
            }
        }
        mapView.getMapAsync(this);
        createLocationRequest();
        return rootView;
    }


    protected synchronized void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    //    protected void startLocationUpdates() {
//        // The final argument to {@code requestLocationUpdates()} is a LocationListener
//        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            //
//            LocationServices.FusedLocationApi.requestLocationUpdates(
//                    mGoogleApiClient, mLocationRequest, this);
//            return;
//        }
//
//    }
    private MapboxMap.OnMyLocationChangeListener myLocationChangeListener = new MapboxMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            final LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            Geocoder geocoder;
            List<Address> address = null;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mapboxMap != null && address != null) {
                mapView.clearAnimation();
                mapboxMap.removeAnnotations();

                IconFactory iconFactory = IconFactory.getInstance(getActivity());
                Drawable iconDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.icon_food);
                Icon icon = iconFactory.fromDrawable(iconDrawable);
                mapboxMap.moveCamera(com.mapbox.mapboxsdk.camera.CameraUpdateFactory.newCameraPosition(
                        new com.mapbox.mapboxsdk.camera.CameraPosition.Builder()
                                .target(loc)
                                .zoom(14)
                                .tilt(20)
                                .build()));
                mapboxMap.addMarker(new com.mapbox.mapboxsdk.annotations.MarkerOptions().position(new com.mapbox.mapboxsdk.geometry.LatLng(loc)).title("").snippet("").icon(icon));
            }
            mapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull com.mapbox.mapboxsdk.geometry.LatLng point) {
                    com.mapbox.mapboxsdk.camera.CameraPosition position = new com.mapbox.mapboxsdk.camera.CameraPosition.Builder()
                            .target(loc) // Sets the new camera position
                            .zoom(17) // Sets the zoom
                            .bearing(180) // Rotate the camera
                            .tilt(30) // Set the camera tilt
                            .build(); // Creates a CameraPosition from the builder

                    mapboxMap.animateCamera(com.mapbox.mapboxsdk.camera.CameraUpdateFactory
                            .newCameraPosition(position), 7000);
                }
            });
//            // Dupont Circle (Washington, DC)
//            Waypoint origin = new Waypoint(location.getLongitude(), location.getLatitude());
//
//            // The White House (Washington, DC)
//            Waypoint destination = new Waypoint(loc);
//
//            // Centroid
//            com.mapbox.mapboxsdk.geometry.LatLng centroid = new com.mapbox.mapboxsdk.geometry.LatLng(
//                    (origin.getLatitude() + destination.getLatitude()) / 2,
//                    (origin.getLongitude() + destination.getLongitude()) / 2);
//// Build the client object
//            MapboxDirections client = new MapboxDirections.Builder()
//                    .setAccessToken(mapboxMap.getAccessToken())
//                    .setOrigin(origin)
//                    .setDestination(destination)
//                    .setProfile(DirectionsCriteria.PROFILE_CYCLING)
//                    .build();
//
//// Execute the request
//            client.enqueue(new Callback<DirectionsResponse>() {
//                @Override
//                public void onResponse(Response<DirectionsResponse> response, Retrofit retrofit) {
//                    if(response != null) {
//                        currentRoute = response.body().getRoutes().get(0);
//                        drawRoute(currentRoute);
//                    }
//                }
//
//                @Override
//                public void onFailure(Throwable t) {
//
//                }
//            });

        }

    };


    private void drawRoute(DirectionsRoute route) {
        int distance = route.getDistance();
        // Convert List<Waypoint> into LatLng[]
        List<Waypoint> waypoints = route.getGeometry().getWaypoints();
        com.mapbox.mapboxsdk.geometry.LatLng[] points = new com.mapbox.mapboxsdk.geometry.LatLng[waypoints.size()];
        for (int i = 0; i < waypoints.size(); i++) {
            points[i] = new com.mapbox.mapboxsdk.geometry.LatLng(
                    waypoints.get(i).getLatitude(),
                    waypoints.get(i).getLongitude());
        }

// Draw Points on MapView
        mapboxMap.addPolyline(new PolylineOptions()
                .add(points)
                .color(Color.parseColor("#3887be"))
                .width(5));
    }

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
    public void onMapReady(final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setMyLocationEnabled(true);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mapView != null) {
            // Access to the location has been granted to the app.
            mapboxMap.setMyLocationEnabled(true);
        }
        mapboxMap.setOnMyLocationChangeListener(myLocationChangeListener);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {

            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }


            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {

            }

            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        mapView.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onMyLocationChange(@Nullable Location location) {

    }
}