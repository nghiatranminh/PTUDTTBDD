package httbdd.cse.nghiatran.halofind.screen;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.nineoldandroids.view.ViewHelper;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import httbdd.cse.nghiatran.halofind.Model.User;
import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.fragment.ProductTourFragment;
import httbdd.cse.nghiatran.halofind.util.HaloFindAPI;
import httbdd.cse.nghiatran.halofind.util.HaloFindHelper;
import httbdd.cse.nghiatran.halofind.util.UpdateUIsHandler;


public class StartActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, UpdateUIsHandler {

    static final int NUM_PAGES = 4;

    ViewPager pager;
    PagerAdapter pagerAdapter;
    boolean isOpaque = true;
    LinearLayout circles;
    Button skip;
    Button done;
    ImageButton next;
    long a = 0;
    private static final String USER_SKIPPED_LOGIN_KEY = "user_skipped_login";
    private static final String KEY_IS_RESOLVING = "is_resolving";
    private static final String KEY_SHOULD_RESOLVE = "should_resolve";
    private boolean mIsResolving = false;
    private boolean userSkippedLogin = false;
    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;
    boolean loggedIn = false;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    ConnectivityManager connMgr;
    NetworkInfo networkInfo;
    Intent intent;
    private SharedPreferences.Editor editor;
    private SharedPreferences global;
    String obId = null;
    public static Boolean REMEMBER_ME = true;
    ArrayList<User> users;

    //    https://graph.facebook.com/" + loginResult.getAccessToken().getUserId() + "/picture?type=large&width=1080
    @Override
    protected void onStart() {
        super.onStart();
        if (REMEMBER_ME) {
            global = PreferenceManager.getDefaultSharedPreferences(this);
            if (global.getString(this.getString(R.string.email), null) != null) {
                obId = global.getString(this.getString(R.string.email), "");
                if (obId != null) {
                    REMEMBER_ME = true;
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra(MainActivity.OBJECTID, obId);
                    startActivity(intent);
                    finish();
                }
            }
        }
        mGoogleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (savedInstanceState != null) {
            userSkippedLogin = savedInstanceState.getBoolean(USER_SKIPPED_LOGIN_KEY);
            mIsResolving = savedInstanceState.getBoolean(KEY_IS_RESOLVING);
            mShouldResolve = savedInstanceState.getBoolean(KEY_SHOULD_RESOLVE);
        }
        LoginManager.getInstance().logOut();
        if (AccessToken.getCurrentAccessToken() != null && loggedIn) {
        }
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("httbdd.cse.nghiatran.halofind", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_tutorial);
        HaloFindAPI.getAllUser(StartActivity.this);
        skip = Button.class.cast(findViewById(R.id.skip));
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTutorial();
            }
        });

        next = ImageButton.class.cast(findViewById(R.id.next));
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(pager.getCurrentItem() + 1, true);
            }
        });

        done = Button.class.cast(findViewById(R.id.done));
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTutorial();
            }
        });
        SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(this);
        editor = share.edit();
        Button loginwithemail = (Button) findViewById(R.id.btnSignIn);
        loginwithemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        Button register = (Button) findViewById(R.id.btnRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                AccessToken.getCurrentAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            final JSONObject object,
                                            GraphResponse response) {
                                        if (isNetworkOnline()) {
                                            try {
                                                String id = object.getString("id");
                                                String name = object.getString("name");
                                                String email = object.getString("email");
                                                byte[] uri = downloadUrl(ConvertToUrl("https://graph.facebook.com/" + id + "/picture?type=large&width=1080"));
                                                String image = Base64.encodeToString(uri, Base64.DEFAULT);

                                                SharedPreferences share;
                                                share = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                                share.edit().remove(getString(R.string.email)).commit();

                                                editor.putString(getString(R.string.email), email);
                                                editor.commit();

                                                if (users != null) {
                                                    int a = users.size();
                                                    for (User user : users) {
                                                        a = a - 1;
                                                        if (email.equalsIgnoreCase(user.getEmail())) {
                                                            intent = new Intent(StartActivity.this, MainActivity.class);
                                                            intent.putExtra(MainActivity.OBJECTID, email);
                                                            startActivity(intent);
                                                            finish();
                                                            break;
                                                        } else {
                                                            if (a == 0) {
                                                                HaloFindAPI.register(name, image, "", email, users.size(), StartActivity.this);
                                                                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                                                                intent.putExtra(HaloFindHelper.USERS, email);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            AlertDialog.Builder alert;
                                            alert = new AlertDialog.Builder(StartActivity.this);
                                            alert.setTitle("Wifi");
                                            alert.setMessage(getString(R.string.no_internet_go_wifi_settings));
                                            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    finish();
                                                    Intent wifiSetting = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                                    startActivity(wifiSetting);
                                                }
                                            });
                                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    finish();
                                                }
                                            });
                                            alert.show();
                                        }
                                    }
                                }

                        );
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,link,gender,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException e) {
                    }
                }

        );

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        ((SignInButton) findViewById(R.id.sign_in_button)).setSize(SignInButton.SIZE_WIDE);
        // Set up view instances
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(true, new CrossfadePageTransformer());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                          @Override
                                          public void onPageScrolled(int position, float positionOffset,
                                                                     int positionOffsetPixels) {
                                              if (position == NUM_PAGES - 2 && positionOffset > 0) {
                                                  if (isOpaque) {
                                                      pager.setBackgroundColor(Color.TRANSPARENT);
                                                      isOpaque = false;
                                                  }
                                              } else {
                                                  if (!isOpaque) {
                                                      pager.setBackgroundColor(getResources().getColor(R.color.primary_material_light));
                                                      isOpaque = true;
                                                  }
                                              }
                                          }

                                          @Override
                                          public void onPageSelected(int position) {
                                              setIndicator(position);
                                              if (position == NUM_PAGES - 2) {
                                              } else if (position < NUM_PAGES - 2) {
                                              } else if (position == NUM_PAGES - 1) {
                                                  endTutorial();
                                              }
                                          }

                                          @Override
                                          public void onPageScrollStateChanged(int state) {

                                          }
                                      }

        );

        buildCircles();
    }

    public URL ConvertToUrl(String urlStr) {
        try {
            URL url = new URL(urlStr);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(),
                    url.getHost(), url.getPort(), url.getPath(),
                    url.getQuery(), url.getRef());
            url = uri.toURL();
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] downloadUrl(URL toDownload) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            byte[] chunk = new byte[4096];
            int bytesRead;
            InputStream in = (InputStream) toDownload.openConnection().getInputStream();
            InputStream stream = toDownload.openStream();

            while ((bytesRead = stream.read(chunk)) > 0) {
                outputStream.write(chunk, 0, bytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return outputStream.toByteArray();
    }

    public boolean isNetworkOnline() {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isNetworkOnline()) {
            super.onActivityResult(requestCode, resultCode, data);

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            } else {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        } else {

            AlertDialog.Builder alert;
            alert = new AlertDialog.Builder(StartActivity.this);
            alert.setTitle("Wifi");
            alert.setMessage(getString(R.string.no_internet_go_wifi_settings));
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    finish();
                    Intent wifiSetting = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(wifiSetting);
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    finish();
                }
            });
            alert.show();
        }
    }

    InputMethodManager imm;

    private void hideSoftKeyboard() {
        if (imm == null) {
            imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        hideSoftKeyboard();
        AppEventsLogger.deactivateApp(this);
//        saveConfiguration();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pager != null) {
            pager.clearOnPageChangeListeners();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(USER_SKIPPED_LOGIN_KEY, userSkippedLogin);
        outState.putBoolean(KEY_IS_RESOLVING, mIsResolving);
        outState.putBoolean(KEY_SHOULD_RESOLVE, mShouldResolve);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


    @Override
    public void onSuccessAction(Object object, String key) {
        if (key.equalsIgnoreCase(HaloFindHelper.USERS)) {
            ArrayList<User> users = (ArrayList<User>) object;
            this.users = users;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                if (isNetworkOnline()) {
                    if (mGoogleApiClient.isConnected()) {
                        signIn();
                    }
                } else {

                    AlertDialog.Builder alert;
                    alert = new AlertDialog.Builder(StartActivity.this);
                    alert.setTitle("Wifi");
                    alert.setMessage(getString(R.string.no_internet_go_wifi_settings));
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                            Intent wifiSetting = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(wifiSetting);
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                        }
                    });
                    alert.show();
                }
                mShouldResolve = true;
                mGoogleApiClient.connect();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String personName = acct.getDisplayName();
            String email = acct.getEmail();
            Uri personPhotoUrl = acct.getPhotoUrl();
            byte[] uri;
            if (personPhotoUrl != null) {
                uri = downloadUrl(ConvertToUrl(personPhotoUrl.toString()));
            } else {
                uri = downloadUrl(ConvertToUrl("https://avatars3.githubusercontent.com/u/1476232?v=3&s=460"));
            }
            String image = Base64.encodeToString(uri, Base64.DEFAULT);
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // ...
                        }
                    });
            /////////
            SharedPreferences share;
            share = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            share.edit().remove(getString(R.string.email)).commit();

            editor.putString(getString(R.string.email), email);
            editor.commit();
            if (users != null) {
                int a = users.size();
                for (User user : users) {
                    a = a - 1;
                    if (email.equalsIgnoreCase(user.getEmail())) {
                        intent = new Intent(StartActivity.this, MainActivity.class);
                        intent.putExtra(MainActivity.OBJECTID, email);
                        startActivity(intent);
                        finish();
                        break;
                    } else {
                        if (a == 0) {
                            HaloFindAPI.register(personName, image, "", email, users.size(), StartActivity.this);
                            Intent intent = new Intent(StartActivity.this, MainActivity.class);
                            intent.putExtra(HaloFindHelper.USERS, email);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Person information is null", Toast.LENGTH_LONG).show();
        }
    }

    private void showErrorDialog(ConnectionResult connectionResult) {
        int errorCode = connectionResult.getErrorCode();

        if (GooglePlayServicesUtil.isUserRecoverableError(errorCode)) {
            // Show the default Google Play services error dialog which may still start an intent
            // on our behalf if the user can resolve the issue.
            GooglePlayServicesUtil.getErrorDialog(errorCode, this, RC_SIGN_IN,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            mShouldResolve = false;
//                            updateUI(false);
                        }
                    }).show();
        } else {
            // No default Google Play Services error, display a message to the user.
//            updateUI(false);
            mShouldResolve = false;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                showErrorDialog(connectionResult);
            }
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {


            mGoogleApiClient.connect();
        } else {
            Toast.makeText(this, "network connection error", Toast.LENGTH_LONG).show();
        }
    }


    private void buildCircles() {
        circles = LinearLayout.class.cast(findViewById(R.id.circles));

        float scale = getResources().getDisplayMetrics().density;
        int padding = (int) (5 * scale + 0.5f);

        for (int i = 0; i < NUM_PAGES - 1; i++) {
            ImageView circle = new ImageView(this);
            circle.setImageResource(R.drawable.ic_swipe_indicator_white_18dp);
            circle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            circle.setAdjustViewBounds(true);
            circle.setPadding(padding, 0, padding, 0);
            circles.addView(circle);
        }

        setIndicator(0);
    }

    private void setIndicator(int index) {
        if (index < NUM_PAGES) {
            for (int i = 0; i < NUM_PAGES - 1; i++) {
                ImageView circle = (ImageView) circles.getChildAt(i);
                if (i == index) {
                    circle.setColorFilter(getResources().getColor(R.color.text_selected));
                } else {
                    circle.setColorFilter(getResources().getColor(android.R.color.transparent));
                }
            }
        }
    }

    private void endTutorial() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ProductTourFragment tp = null;
            switch (position) {
                case 0:
                    tp = ProductTourFragment.newInstance(R.layout.welcome_fragment01);
                    break;
                case 1:
                    tp = ProductTourFragment.newInstance(R.layout.welcome_fragment02);
                    break;
                case 2:
                    tp = ProductTourFragment.newInstance(R.layout.welcome_fragment03);
                    break;
                case 3:
                    tp = ProductTourFragment.newInstance(R.layout.welcome_fragment5);
                    break;
            }

            return tp;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public class CrossfadePageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();

            View backgroundView = page.findViewById(R.id.welcome_fragment);
            View text_head = page.findViewById(R.id.heading);
            View text_content = page.findViewById(R.id.content);

            if (0 <= position && position < 1) {
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }
            if (-1 < position && position < 0) {
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }

            if (position <= -1.0f || position >= 1.0f) {
            } else if (position == 0.0f) {
            } else {
                if (backgroundView != null) {
                    ViewHelper.setAlpha(backgroundView, 1.0f - Math.abs(position));

                }

                if (text_head != null) {
                    ViewHelper.setTranslationX(text_head, pageWidth * position);
                    ViewHelper.setAlpha(text_head, 1.0f - Math.abs(position));
                }

                if (text_content != null) {
                    ViewHelper.setTranslationX(text_content, pageWidth * position);
                    ViewHelper.setAlpha(text_content, 1.0f - Math.abs(position));
                }
            }
        }
    }
}