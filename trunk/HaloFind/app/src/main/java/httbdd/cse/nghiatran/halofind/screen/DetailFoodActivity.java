package httbdd.cse.nghiatran.halofind.screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import httbdd.cse.nghiatran.halofind.Model.Food;
import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.util.HaloFindHelper;
import httbdd.cse.nghiatran.halofind.util.InterfaceHelper;

/**
 * Created by TranMinhNghia_512023 on 4/13/2016.
 */
public class DetailFoodActivity extends BaseActivity {
    CardView cvaddress;
    Food food;
    ImageView image, avatar;
    TextView title, address, time, body, name;
//    GifImageView gifView;

    @Override
    protected void onStart() {
        super.onStart();
//        if (food.getGif().equalsIgnoreCase(HaloFindHelper.GIF)) {
//            gifView.startAnimation();
//        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_food);
        InterfaceHelper.addActivityToolbar(this);
        InterfaceHelper.addSupportActionBar(this);
        food = (Food) getIntent().getSerializableExtra(HaloFindHelper.FOOD);
        setTitle(food.getTitle());
        image = (ImageView) findViewById(R.id.image);
        avatar = (ImageView) findViewById(R.id.avatar);
        cvaddress = (CardView) findViewById(R.id.cvaddress);
//        gifView = (GifImageView) findViewById(R.id.gifImageView);
        title = (TextView) findViewById(R.id.title);
        body = (TextView) findViewById(R.id.body);
        name = (TextView) findViewById(R.id.name);
        address = (TextView) findViewById(R.id.address);
        time = (TextView) findViewById(R.id.time);
        title.setText(food.getTitle());
        address.setText(food.getAddress());
//        time.setText(food.getTime());
        body.setText(food.getBody());
        name.setText(food.getUsername());
        byte[] images = Base64.decode(food.getImage(), Base64.DEFAULT);
//        byte[] imagess = Base64.decode(food.getUserimage(), Base64.DEFAULT);
//        if (food.getGif().equalsIgnoreCase(HaloFindHelper.GIF)) {
//            gifView.setVisibility(View.VISIBLE);
//            image.setVisibility(View.GONE);
//            gifView.setBytes(images);
//        } else {
//            gifView.setVisibility(View.GONE);
//            image.setVisibility(View.VISIBLE);
//        Bitmap bmp = BitmapFactory.decodeByteArray(images, 0, images.length);
//        image.setImageBitmap(bmp);
        Glide.with(this).load(images).asBitmap().fitCenter().into(image);
        byte[] avatars = Base64.decode(food.getUserimage(), Base64.DEFAULT);
        Glide.with(this).load(avatars).asBitmap().fitCenter().into(avatar);
//        }
//        Bitmap bmp = BitmapFactory.decodeByteArray(imagess, 0, imagess.length);
//        avatar.setImageBitmap(bmp);
        cvaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra(HaloFindHelper.FOOD, food);
                startActivity(intent);
            }
        });

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

    @Override
    protected void onStop() {
        super.onStop();
//        if (food.getGif().equalsIgnoreCase(HaloFindHelper.GIF)) {
//            gifView.stopAnimation();
//        }
    }
}
