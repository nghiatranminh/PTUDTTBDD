package httbdd.cse.nghiatran.halofind.placepicker;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.foursquare.api.types.Venue;
import com.foursquare.placepicker.PlacePicker;
import com.foursquare.placepicker.PlacePickerSdk;

import httbdd.cse.nghiatran.halofind.R;

public class MainActivity extends AppCompatActivity {

    // User your own keys. This is just a sample app.
    private static final String CONSUMER_KEY = "4SKJYWXMAKOAUIOGXOUF0U15RZ3X52XZ34UDTON24TEOJ1J1";
    private static final String CONSUMER_SECRET = "5QLH4Y5YCHGXLL15HDQA5KRPDB1F0DCB2SDZSG4WVEOTDYR3";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PlacePickerSdk.with(new PlacePickerSdk.Builder(this)
                .consumer(CONSUMER_KEY, CONSUMER_SECRET)
                .imageLoader(new PlacePickerSdk.ImageLoader() {
                    @Override
                    public void loadImage(Context context, ImageView v, String url) {
                        Glide.with(context)
                                .load(url)
                                .placeholder(R.drawable.category_none)
                                .dontAnimate()
                                .into(v);
                    }
                })
                .build());

        setContentView(R.layout.picker);

        Button findPlace = (Button) findViewById(R.id.btnPlacePick);
        findPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPlace();
            }
        });

        Button currentPlace = (Button) findViewById(R.id.btnCurrentPlace);
        currentPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getClosestPlace();
            }
        });

        Button starbucks = (Button) findViewById(R.id.btnStarbucksSearch);
        starbucks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lookForStarbucks();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == PlacePicker.PLACE_PICKED_RESULT_CODE) {
            Venue place = data.getParcelableExtra(PlacePicker.EXTRA_PLACE);
            Toast.makeText(this, place.getName(), Toast.LENGTH_LONG).show();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void pickPlace() {
        Intent i = new Intent(this, PlacePicker.class);
        startActivityForResult(i, 9001);
    }

    private void lookForStarbucks() {
        Intent i = new Intent(this, PlacePicker.class);
        i.putExtra(PlacePicker.EXTRA_QUERY, "starbucks");
        startActivityForResult(i, 9001);
    }

    private void getClosestPlace() {
        PlacePickerSdk.get().getCurrentPlace(new PlacePickerSdk.CurrentPlaceResult() {
            @Override
            public void success(Venue venue, boolean confident) {
                Toast.makeText(MainActivity.this,"Got closest place " + venue.getName() + " Confident? " + confident, Toast.LENGTH_LONG).show();
            }

            @Override
            public void fail() {
            }
        });
    }
}
