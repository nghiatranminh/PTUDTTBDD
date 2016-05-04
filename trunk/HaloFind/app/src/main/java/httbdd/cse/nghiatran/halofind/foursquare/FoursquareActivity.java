package httbdd.cse.nghiatran.halofind.foursquare;


// Implementation of FourSquare API and Change the CLIENT ID AND CLIENT SECRET

import android.app.Activity;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.util.HaloFindHelper;

public class FoursquareActivity extends Activity implements OnMapReadyCallback {

    ArrayList<FoursquareModel> venuesList;
    // we will need to take the latitude and the logntitude from a certain point

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected double latitude, longitude;
    ArrayAdapter<String> myAdapter;

    // GPSTracker class
    GPSTracker gps;

    // Near BY ListView
    ListView lv;

    // Google Map
    public MapboxMap mapboxMap;
    public MapView mapView;

    // ArrayList & array list adapter to set listview
    ArrayList<FoursquareModel> _commentList = new ArrayList<FoursquareModel>();
    NearbyListAdapter _nearByListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foursquare);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        lv = (ListView) findViewById(R.id.listview);
        gps = new GPSTracker(this);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

        } else {

            gps.showSettingsAlert();
        }
        mapView.getMapAsync(this);
        try {

//            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                    new LatLng(latitude, longitude), 13));
//
//            CameraPosition cameraPosition = new CameraPosition.Builder()
//                    .target(new LatLng(latitude, longitude)) // Sets the center
//                    // of the map to
//                    // location user
//                    .zoom(17) // Sets the zoom
//                    .build(); // Creates a CameraPosition from the builder
//            mapboxMap.animateCamera(CameraUpdateFactory
//                    .newCameraPosition(cameraPosition));

            mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(new LatLng(latitude, longitude))
                            .zoom(14)
                            .tilt(20)
                            .build()));
            mapboxMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));

        } catch (Exception e) {
            e.printStackTrace();
        }


        // start the AsyncTask that makes the call for the venus search.

        new fourquare().execute();

    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setMyLocationEnabled(true);
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(latitude, longitude), 13));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)) // Sets the center
                // of the map to
                // location user
                .zoom(17) // Sets the zoom
                .build(); // Creates a CameraPosition from the builder
        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }


    private class fourquare extends AsyncTask<View, Void, String> {
        String temp;

        @Override
        protected String doInBackground(View... urls) {
            // make Call to the url
            temp = makeCall(HaloFindHelper.FOURSQUAREINIT
                    + HaloFindHelper.CLIENT_ID
                    + "&client_secret="
                    + HaloFindHelper.CLIENT_SECRET
                    + "&v=20130815&ll="
                    + String.valueOf(latitude)
                    + ","
                    + String.valueOf(longitude));
            Log.e("Link ---- > ", temp);
            return "";
        }

        @Override
        protected void onPreExecute() {
            // we can start a progress bar here
        }

        @Override
        protected void onPostExecute(String result) {
            if (temp == null) {
                // we have an error to the call
                // we can also stop the progress bar
            } else {
                // all things went right

                // parseFoursquare venues search result
                venuesList = (ArrayList<FoursquareModel>) parseFoursquare(temp);

                // set the results to the list
                // and show them in the xml
                _nearByListAdapter = new NearbyListAdapter(FoursquareActivity.this, 0,
                        venuesList);

                lv.setAdapter(_nearByListAdapter);

                for (FoursquareModel model : venuesList) {
                    if (mapboxMap != null) {
                        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(model.getLatitude(), model.getLongtitude())).title(model.getName()).snippet(model.getAddress()));
                    }
                }

            }
        }
    }

    public static String makeCall(String url) {

        // string buffers the url
        StringBuffer buffer_string = new StringBuffer(url);
        String replyString = "";

        // instanciate an HttpClient
        HttpClient httpclient = new DefaultHttpClient();
        // instanciate an HttpGet
        HttpGet httpget = new HttpGet(buffer_string.toString());

        try {
            // get the responce of the httpclient execution of the url
            HttpResponse response = httpclient.execute(httpget);
            InputStream is = response.getEntity().getContent();

            // buffer input stream the result
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(20);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            // the result as a string is ready for parsing
            replyString = new String(baf.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // trim the whitespaces
        return replyString.trim();
    }

    private static ArrayList<FoursquareModel> parseFoursquare(
            final String response) {

        ArrayList<FoursquareModel> temp = new ArrayList<FoursquareModel>();
        try {

            // make an jsonObject in order to parse the response
            JSONObject jsonObject = new JSONObject(response);

            // make an jsonObject in order to parse the response
            if (jsonObject.has("response")) {
                if (jsonObject.getJSONObject("response").has("venues")) {
                    JSONArray jsonArray = jsonObject.getJSONObject("response")
                            .getJSONArray("venues");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        FoursquareModel poi = new FoursquareModel();

                        try {
                            if (jsonArray.getJSONObject(i).has("name")) {
                                poi.setName(jsonArray.getJSONObject(i)
                                        .getString("name"));

                                // We will get only those locations which has
                                // address
                                if (jsonArray.getJSONObject(i).has("location")) {
                                    if (jsonArray.getJSONObject(i)
                                            .getJSONObject("location")
                                            .has("address")) {
                                        poi.setAddress(jsonArray
                                                .getJSONObject(i)
                                                .getJSONObject("location")
                                                .getString("address"));

                                        if (jsonArray.getJSONObject(i)
                                                .getJSONObject("location")
                                                .has("lat")) {
                                            poi.setLatitude(jsonArray
                                                    .getJSONObject(i)
                                                    .getJSONObject("location")
                                                    .getString("lat"));
                                        }
                                        if (jsonArray.getJSONObject(i)
                                                .getJSONObject("location")
                                                .has("distance")) {
                                            poi.setDistance(jsonArray
                                                    .getJSONObject(i)
                                                    .getJSONObject("location")
                                                    .getString("distance"));
                                        }
                                        if (jsonArray.getJSONObject(i)
                                                .getJSONObject("location")
                                                .has("lng")) {
                                            poi.setLongtitude(jsonArray
                                                    .getJSONObject(i)
                                                    .getJSONObject("location")
                                                    .getString("lng"));
                                        }

                                        if (jsonArray.getJSONObject(i)
                                                .getJSONObject("location")
                                                .has("city")) {
                                            poi.setCity(jsonArray
                                                    .getJSONObject(i)
                                                    .getJSONObject("location")
                                                    .getString("city"));
                                        }
                                        if (jsonArray.getJSONObject(i)
                                                .getJSONObject("location")
                                                .has("country")) {
                                            poi.setCountry(jsonArray
                                                    .getJSONObject(i)
                                                    .getJSONObject("location")
                                                    .getString("country"));
                                        }
                                        if (jsonArray.getJSONObject(i).has(
                                                "categories")) {
                                            if (jsonArray.getJSONObject(i)
                                                    .getJSONArray("categories")
                                                    .length() > 0) {
                                                if (jsonArray
                                                        .getJSONObject(i)
                                                        .getJSONArray(
                                                                "categories")
                                                        .getJSONObject(0)
                                                        .has("name")) {
                                                    poi.setCategory(jsonArray
                                                            .getJSONObject(i)
                                                            .getJSONArray(
                                                                    "categories")
                                                            .getJSONObject(0)
                                                            .getString("name"));
                                                }
                                                if (jsonArray
                                                        .getJSONObject(i)
                                                        .getJSONArray(
                                                                "categories")
                                                        .getJSONObject(0)
                                                        .has("id")) {
                                                    poi.setCategoryID(jsonArray
                                                            .getJSONObject(i)
                                                            .getJSONArray(
                                                                    "categories")
                                                            .getJSONObject(0)
                                                            .getString("id"));
                                                }
                                                if (jsonArray
                                                        .getJSONObject(i)
                                                        .getJSONArray(
                                                                "categories")
                                                        .getJSONObject(0)
                                                        .has("icon")) {

                                                    poi.setCategoryIcon(jsonArray
                                                            .getJSONObject(i)
                                                            .getJSONArray(
                                                                    "categories")
                                                            .getJSONObject(0)
                                                            .getJSONObject(
                                                                    "icon")
                                                            .getString("prefix")
                                                            + "bg_32.png");
                                                }
                                            }
                                        }
                                        temp.add(poi);

                                    }
                                }

                            }
                        } catch (Exception e) {

                        }

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        new fourquare().execute();
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
}
