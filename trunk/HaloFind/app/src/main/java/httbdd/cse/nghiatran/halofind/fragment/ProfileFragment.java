package httbdd.cse.nghiatran.halofind.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import java.util.ArrayList;

import httbdd.cse.nghiatran.halofind.Model.User;
import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.screen.MainActivity;
import httbdd.cse.nghiatran.halofind.screen.StartActivity;
import httbdd.cse.nghiatran.halofind.util.HaloFindAPI;
import httbdd.cse.nghiatran.halofind.util.HaloFindHelper;

/**
 * Created by TranMinhNghia_512023 on 4/12/2016.
 */
public class ProfileFragment extends BaseFragment {
    private static ProfileFragment fragment = null;
    String objectId;
    RecyclerView recyclerView;
    ImageView avatar;
    TextView email;
    Intent intent;
    CardView cvemail, cvlogout;

    public static ProfileFragment newInstance() {
        if (fragment == null)
            fragment = new ProfileFragment();
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        HaloFindAPI.getDatasnapshotUsers(this);
        objectId = getActivity().getIntent().getStringExtra(MainActivity.OBJECTID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //variables
        avatar = (ImageView) view.findViewById(R.id.avatar);
        email = (TextView) view.findViewById(R.id.mail);
        cvemail = (CardView) view.findViewById(R.id.cvemail);
        cvlogout = (CardView) view.findViewById(R.id.cvLogout);
        cvlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences share;
                share = PreferenceManager.getDefaultSharedPreferences(getActivity());
                share.edit().remove(getString(R.string.email)).commit();
                LoginManager.getInstance().logOut();
                StartActivity.REMEMBER_ME = false;
                getActivity().finish();
                intent = new Intent(getActivity(), StartActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }


    @Override
    public void onSuccessAction(Object object, String key) {
        super.onSuccessAction(object, key);
        if (key.equalsIgnoreCase(HaloFindHelper.USERS)) {
            ArrayList<User> users = (ArrayList<User>) object;
            for (User user : users) {
                if (user.getEmail().equalsIgnoreCase(objectId)) {
                    email.setText(user.getEmail());
                    byte[] im = Base64.decode(user.getImage(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(im, 0, im.length);
//                    if (im != null) {
//                        Glide.with(getActivity()).load(im).asBitmap().into(avatar);
//                    }
                    avatar.setImageBitmap(bitmap);
                    break;
                }
            }
        }
    }
}
