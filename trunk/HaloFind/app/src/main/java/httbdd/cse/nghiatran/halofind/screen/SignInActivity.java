package httbdd.cse.nghiatran.halofind.screen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import httbdd.cse.nghiatran.halofind.Model.User;
import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.util.HaloFindAPI;
import httbdd.cse.nghiatran.halofind.util.HaloFindHelper;
import httbdd.cse.nghiatran.halofind.util.InterfaceHelper;


public class SignInActivity extends BaseActivity {
    TextInputLayout pass, email;
    Button signin;
    Intent intent;
    ArrayList<User> users;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen);
        InterfaceHelper.addActivityToolbar(this);
        InterfaceHelper.addSupportActionBar(this);
        pass = (TextInputLayout) findViewById(R.id.edtPassword);
        email = (TextInputLayout) findViewById(R.id.edtEmail);
        if (getIntent().getStringExtra(HaloFindHelper.USERS) != null) {
            email.getEditText().setText(getIntent().getStringExtra(HaloFindHelper.USERS));
        }
        signin = (Button) findViewById(R.id.btnSignIn);
        HaloFindAPI.getAllUser(this);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (users != null) {
                    int a = users.size();
                    for (User user : users) {
                        if (user.getEmail().equalsIgnoreCase(email.getEditText().getText().toString()) && user.getPass().equalsIgnoreCase(pass.getEditText().getText().toString())) {
                            SharedPreferences share;
                            share = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            share.edit().remove(getString(R.string.email)).commit();
                            SharedPreferences.Editor editor;
                            editor = share.edit();
                            editor.putString(getString(R.string.email), email.getEditText().toString());
                            editor.commit();
                            intent = new Intent(SignInActivity.this, MainActivity.class);
                            intent.putExtra(MainActivity.OBJECTID, user.getEmail());
                            startActivity(intent);
                            finish();
                            break;
                        }
                        a = a - 1;
                        if (a == 0) {
                            email.setError("Email or password is invalid");
                        }
                    }

                }
            }
        });
    }

    @Override
    public void onSuccessAction(Object object, String key) {
        super.onSuccessAction(object, key);
        if (key.equalsIgnoreCase(HaloFindHelper.USERS)) {
            ArrayList<User> users = (ArrayList<User>) object;
            this.users = users;
        }
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
}
