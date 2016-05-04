package httbdd.cse.nghiatran.halofind.foursquare;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mapbox.mapboxsdk.location.LocationListener;

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
import httbdd.cse.nghiatran.halofind.screen.BaseActivity;
import httbdd.cse.nghiatran.halofind.util.HaloFindHelper;
import httbdd.cse.nghiatran.halofind.util.InterfaceHelper;

/**
 * Created by TranMinhNghia_512023 on 4/23/2016.
 */

public class SearchLocation extends BaseActivity {
    ArrayList<FoursquareModel> venuesList;
    SharedPreferences share;

    // we will need to take the latitude and the logntitude from a certain point

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected double latitude, longitude;
    ArrayAdapter<String> myAdapter;

    // GPSTracker class
    GPSTracker gps;

    // Near BY ListView
    ListView lv;

    // ArrayList & array list adapter to set listview
    ArrayList<FoursquareModel> _commentList = new ArrayList<FoursquareModel>();
    NearbyListAdapter _nearByListAdapter;

    public static void startWithUri(@NonNull Context context, @NonNull Uri uri) {
        Intent intent = new Intent(context, SearchLocation.class);
        intent.setData(uri);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seachlocation);
        InterfaceHelper.addActivityToolbar(this);
        InterfaceHelper.addSupportActionBar(this);
        lv = (ListView) findViewById(R.id.listview);
        gps = new GPSTracker(this);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

        } else {

            gps.showSettingsAlert();
        }

        // start the AsyncTask that makes the call for the venus search.

        new fourquare().execute();

    }

    private class fourquaresearch extends AsyncTask<View, Void, String> {

        String temp;
        String search = "";

        public fourquaresearch(String newText) {
            this.search = newText;
        }

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
                    + String.valueOf(longitude)
                    + "&query="
                    + search.replaceAll(" ", "%20"));
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
                _nearByListAdapter = new NearbyListAdapter(SearchLocation.this, 0,
                        venuesList);

                lv.setAdapter(_nearByListAdapter);

            }
        }
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
                _nearByListAdapter = new NearbyListAdapter(SearchLocation.this, 0,
                        venuesList);

                lv.setAdapter(_nearByListAdapter);

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
                                poi.setName(jsonArray.getJSONObject(i).getString("name"));

                                // We will get only those locations which has
                                // address
                                if (jsonArray.getJSONObject(i).has("location")) {
                                    if (jsonArray.getJSONObject(i).getJSONObject("location").has("address")) {
                                        poi.setAddress(jsonArray.getJSONObject(i).getJSONObject("location").getString("address"));

                                        if (jsonArray.getJSONObject(i).getJSONObject("location").has("lat")) {
                                            poi.setLatitude(jsonArray.getJSONObject(i).getJSONObject("location").getString("lat"));
                                        }
                                        if (jsonArray.getJSONObject(i).getJSONObject("location").has("distance")) {
                                            poi.setDistance(jsonArray.getJSONObject(i).getJSONObject("location").getString("distance"));
                                        }
                                        if (jsonArray.getJSONObject(i).getJSONObject("location").has("lng")) {
                                            poi.setLongtitude(jsonArray.getJSONObject(i).getJSONObject("location").getString("lng"));
                                        }

                                        if (jsonArray.getJSONObject(i).getJSONObject("location").has("city")) {
                                            poi.setCity(jsonArray.getJSONObject(i).getJSONObject("location").getString("city"));
                                        }
                                        if (jsonArray.getJSONObject(i).getJSONObject("location").has("country")) {
                                            poi.setCountry(jsonArray.getJSONObject(i).getJSONObject("location").getString("country"));
                                        }
                                        if (jsonArray.getJSONObject(i).has(
                                                "categories")) {
                                            if (jsonArray.getJSONObject(i).getJSONArray("categories").length() > 0) {
                                                if (jsonArray.getJSONObject(i).getJSONArray("categories").getJSONObject(0).has("name")) {
                                                    poi.setCategory(jsonArray.getJSONObject(i).getJSONArray("categories").getJSONObject(0).getString("name"));
                                                }
                                                if (jsonArray.getJSONObject(i).getJSONArray("categories").getJSONObject(0).has("id")) {
                                                    poi.setCategoryID(jsonArray.getJSONObject(i).getJSONArray("categories").getJSONObject(0).getString("id"));
                                                }
                                                if (jsonArray.getJSONObject(i).getJSONArray("categories").getJSONObject(0).has("icon")) {
                                                    poi.setCategoryIcon(jsonArray.getJSONObject(i).getJSONArray("categories").getJSONObject(0).getJSONObject("icon").getString("prefix") + "bg_88.png");
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
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                new fourquaresearch(newText).execute();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                share = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                share.edit().remove(getString(R.string.back)).commit();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new fourquare().execute();
    }
}
