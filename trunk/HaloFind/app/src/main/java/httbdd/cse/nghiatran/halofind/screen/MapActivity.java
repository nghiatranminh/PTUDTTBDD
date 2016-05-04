package httbdd.cse.nghiatran.halofind.screen;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipDescription;
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
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.mapbox.directions.DirectionsCriteria;
import com.mapbox.directions.MapboxDirections;
import com.mapbox.directions.service.models.DirectionsResponse;
import com.mapbox.directions.service.models.DirectionsRoute;
import com.mapbox.directions.service.models.Waypoint;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import httbdd.cse.nghiatran.halofind.Model.Food;
import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.util.HaloFindHelper;
import httbdd.cse.nghiatran.halofind.util.PermissionUtils;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class MapActivity extends AppCompatActivity implements MapboxMap.OnMyLocationChangeListener, OnMapReadyCallback, LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {
    public LocationManager locationManager;
    private MapView mapView;
    protected String mLastUpdateTime;
    public MapboxMap mapboxMap;
    private DirectionsRoute currentRoute;
    protected LocationRequest mLocationRequest;
    protected Boolean mRequestingLocationUpdates = true;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    Food food;

    @Override
    protected void onStart() {
        super.onStart();
        food = (Food) getIntent().getSerializableExtra(HaloFindHelper.FOOD);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_map);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        updateValuesFromBundle(savedInstanceState);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
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
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) this);
                return;
            }
        }
        mapView.getMapAsync(this);
        createLocationRequest();
        mapView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {

                // Defines a variable to store the action type for the incoming event
                final int action = event.getAction();

                // Handles each of the expected events
                switch (action) {

                    case DragEvent.ACTION_DRAG_STARTED:

                        // Determines if this View can accept the dragged data
                        if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

                            // As an example of what your application might do,
                            // applies a blue color tint to the View to indicate that it can accept
                            // data.
                            v.setBackgroundColor(Color.BLUE);

                            // Invalidate the view to force a redraw in the new tint
                            v.invalidate();

                            // returns true to indicate that the View can accept the dragged data.
                            return true;

                        }

                        // Returns false. During the current drag and drop operation, this View will
                        // not receive events again until ACTION_DRAG_ENDED is sent.
                        return false;

                    case DragEvent.ACTION_DRAG_ENTERED:

                        // Applies a green tint to the View. Return true; the return value is ignored.

                        v.setBackgroundColor(Color.GREEN);

                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate();

                        return true;

                    case DragEvent.ACTION_DRAG_LOCATION:

                        // Ignore the event
                        return true;

                    case DragEvent.ACTION_DRAG_EXITED:

                        // Re-sets the color tint to blue. Returns true; the return value is ignored.
                        v.setBackgroundColor(Color.BLUE);

                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate();

                        return true;

                    case DragEvent.ACTION_DROP:

                        // Gets the item containing the dragged data
                        ClipData.Item item = event.getClipData().getItemAt(0);

                        // Gets the text data from the item.

                        // Displays a message containing the dragged data.
                        Toast.makeText(MapActivity.this, "Dragged data is " + item.getText(), Toast.LENGTH_LONG).show();

                        // Turns off any color tints
                        v.clearAnimation();

                        // Invalidates the view to force a redraw
                        v.invalidate();

                        // Returns true. DragEvent.getResult() will return true.
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:

                        // Turns off any color tinting
                        v.clearAnimation();

                        // Invalidates the view to force a redraw
                        v.invalidate();

                        // Does a getResult(), and displays what happened.
                        if (event.getResult()) {
                            Toast.makeText(MapActivity.this, "The drop was handled.", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(MapActivity.this, "The drop didn't work.", Toast.LENGTH_LONG).show();

                        }

                        // returns true; the value is ignored.
                        return true;

                    // An unknown action type was received.
                    default:
                        Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                        break;
                }

                return false;
//                float x = event.getX(); // get screen x position or coordinate
//                float y = event.getY();  // get screen y position or coordinate
//
//                int x_co = Integer.parseInt(String.valueOf(Math.round(x))); // casting float to int
//                int y_co = Integer.parseInt(String.valueOf(Math.round(y))); // casting float to int
//
//
//                Point x_y_points = new Point(x_co, y_co);// accept int x,y value
//                final Projection projection = v
//                latitude = latLng.latitude; // your latitude
//                longitude = latLng.longitude; // your longitude

            }
        });

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
            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mapboxMap != null && food.getAddress() != null) {
                mapView.clearAnimation();
                mapboxMap.removeAnnotations();

                IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
                Drawable iconDrawable = ContextCompat.getDrawable(MapActivity.this, R.drawable.icon_food);
                Icon icon = iconFactory.fromDrawable(iconDrawable);
                mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                                .target(new LatLng(food.getLatitude(), food.getLongitude()))
                                .zoom(14)
                                .tilt(20)
                                .build()));
                mapboxMap.addMarker(new MarkerOptions().position(new LatLng(food.getLatitude(), food.getLongitude())).title(food.getTitle()).snippet(food.getAddress()).icon(icon));
            }
            mapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng point) {
                    CameraPosition position = new CameraPosition.Builder()
                            .target(loc) // Sets the new camera position
                            .zoom(17) // Sets the zoom
                            .bearing(180) // Rotate the camera
                            .tilt(30) // Set the camera tilt
                            .build(); // Creates a CameraPosition from the builder

                    mapboxMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(position), 7000);
                }
            });
            // Dupont Circle (Washington, DC)
            Waypoint origin = new Waypoint(location.getLongitude(), location.getLatitude());

            // The White House (Washington, DC)
            Waypoint destination = new Waypoint(food.getLongitude(), food.getLatitude());

            // Centroid
            LatLng centroid = new LatLng(
                    (origin.getLatitude() + destination.getLatitude()) / 2,
                    (origin.getLongitude() + destination.getLongitude()) / 2);
// Build the client object
            MapboxDirections client = new MapboxDirections.Builder()
                    .setAccessToken(mapboxMap.getAccessToken())
                    .setOrigin(origin)
                    .setDestination(destination)
                    .setProfile(DirectionsCriteria.PROFILE_CYCLING)
                    .build();

// Execute the request
            client.enqueue(new Callback<DirectionsResponse>() {
                @Override
                public void onResponse(Response<DirectionsResponse> response, Retrofit retrofit) {
                    if (response != null) {
                        currentRoute = response.body().getRoutes().get(0);
                        drawRoute(currentRoute);
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });

        }

    };


    private void drawRoute(DirectionsRoute route) {
        int distance = route.getDistance();
        // Convert List<Waypoint> into LatLng[]
        List<Waypoint> waypoints = route.getGeometry().getWaypoints();
        LatLng[] points = new LatLng[waypoints.size()];
        for (int i = 0; i < waypoints.size(); i++) {
            points[i] = new LatLng(
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
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMapReady(final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setMyLocationEnabled(true);
        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(MapActivity.this, LOCATION_PERMISSION_REQUEST_CODE,
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