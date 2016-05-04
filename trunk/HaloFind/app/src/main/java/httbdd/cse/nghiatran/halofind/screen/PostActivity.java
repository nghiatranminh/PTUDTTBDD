package httbdd.cse.nghiatran.halofind.screen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.greenrobot.event.EventBus;
import httbdd.cse.nghiatran.halofind.Model.Food;
import httbdd.cse.nghiatran.halofind.Model.User;
import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.foursquare.FoursquareModel;
import httbdd.cse.nghiatran.halofind.foursquare.SearchLocation;
import httbdd.cse.nghiatran.halofind.util.HaloFindAPI;
import httbdd.cse.nghiatran.halofind.util.HaloFindHelper;
import httbdd.cse.nghiatran.halofind.util.InterfaceHelper;

public class PostActivity extends BaseActivity {
    String district = null;
    String category = null;
    EditText title, body, address;
    Button location, post;
    ArrayList<Food> foods;
    User user;
    private SharedPreferences.Editor editor;
    FoursquareModel foursquareModel;
    public static final String FOURSQUARE = "foursquare";

    public static void startWithUri(@NonNull Context context, @NonNull Uri uri, FoursquareModel foursquareModel) {
        Intent intent = new Intent(context, PostActivity.class);
        intent.setData(uri);
        intent.putExtra(FOURSQUARE, foursquareModel);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        InterfaceHelper.addActivityToolbar(this);
        InterfaceHelper.addSupportActionBar(this);
        HaloFindAPI.getAllFood(this);
        foursquareModel = (FoursquareModel) getIntent().getSerializableExtra(FOURSQUARE);
        SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(this);
        editor = share.edit();
        title = (EditText) findViewById(R.id.title);
        body = (EditText) findViewById(R.id.body);
        address = (EditText) findViewById(R.id.address);
        post = (Button) findViewById(R.id.post);
        updateView(foursquareModel);
        Spinner spdistrict = (Spinner) findViewById(R.id.spdistrict);
        Spinner spcategory = (Spinner) findViewById(R.id.spcategory);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.district_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spdistrict.setAdapter(adapter);
        spdistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                district = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spcategory.setAdapter(adapter1);
        spcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ((ImageView) findViewById(R.id.image)).setImageURI(getIntent().getData());
        byte[] bytes = ImageView_To_Byte((ImageView) findViewById(R.id.image));
        final String image = Base64.encodeToString(bytes, Base64.DEFAULT);
        double by = bytes.length / 1024;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final Calendar cal = Calendar.getInstance();
        final String a = dateFormat.format(cal.getTime());
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InterfaceHelper.showDialog(PostActivity.this);
                post.setEnabled(false);
                if (foods != null) {
                    Firebase myFire = new Firebase(HaloFindHelper.serverUrl + HaloFindHelper.FOOD + "/");
                    Firebase fbuser = myFire.child(Long.toString(foods.size() + 1));
                    if (!title.getText().toString().equalsIgnoreCase("") && !address.getText().toString().equalsIgnoreCase("") && !body.getText().toString().equalsIgnoreCase("")) {
                        if (user != null) {
                            Food newuser = new Food(title.getText().toString(), image, foursquareModel.getCity(), category, body.getText().toString(), address.getText().toString(), foursquareModel.getLatitude(), foursquareModel.getLongtitude(), district, a, user.getName(), user.getImage(), "0");
                            fbuser.setValue(newuser);
                        }
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please fill in info !!!", Toast.LENGTH_LONG).show();
                        post.setEnabled(true);
                    }
                    if (InterfaceHelper.Dialog.isShowing()) {
                        InterfaceHelper.Dialog.dismiss();
                    }
                }
            }
        });
    }

    private void updateView(FoursquareModel foursquareModel) {
        if (foursquareModel != null) {
            this.foursquareModel = foursquareModel;
            address.setText(foursquareModel.getAddress());
            title.setText(foursquareModel.getName());

            TextView tv_name = (TextView) findViewById(R.id.tv_name);
            TextView tv_address = (TextView) findViewById(R.id.tv_address);
            TextView tv_distance = (TextView) findViewById(R.id.tv_distance);
            ImageView icon = (ImageView) findViewById(R.id.icon);

            tv_name.setText(foursquareModel.getName());
            tv_address.setText(foursquareModel.getAddress());
            tv_distance.setText("~" + foursquareModel.getDistance() + "m");
            Glide.with(PostActivity.this).load(foursquareModel.getCategoryIcon()).asBitmap().into(icon);
        }
    }

    public void LocationFoodSearch(View v) {
        editor.putString(getString(R.string.back), getString(R.string.back));
        editor.commit();
        Intent intent = new Intent(PostActivity.this, SearchLocation.class);
        startActivity(intent);
    }

    public byte[] ImageView_To_Byte(ImageView imageView) {
        //Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.chomuc);
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bmp = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccessAction(Object object, String key) {
        super.onSuccessAction(object, key);
        if (key.equalsIgnoreCase(HaloFindHelper.FOOD)) {
            ArrayList<Food> foods = (ArrayList<Food>) object;
            this.foods = foods;
        }
    }

    public void onEvent(FoursquareModel foursquareModelo) {
        super.onEvent(foursquareModelo);
        if (foursquareModelo != null) {
            updateView(foursquareModelo);
        }
    }

    public void onEvent(User user) {
        super.onEvent(user);
        if (user != null) {
            this.user = user;
        }
    }

    @Override
    protected boolean isStickyAvailable() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().removeAllStickyEvents();
    }
}
