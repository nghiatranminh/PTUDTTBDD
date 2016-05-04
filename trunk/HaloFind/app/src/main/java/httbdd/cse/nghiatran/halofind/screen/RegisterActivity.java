package httbdd.cse.nghiatran.halofind.screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import httbdd.cse.nghiatran.halofind.Model.User;
import httbdd.cse.nghiatran.halofind.R;
import httbdd.cse.nghiatran.halofind.util.HaloFindAPI;
import httbdd.cse.nghiatran.halofind.util.HaloFindHelper;
import httbdd.cse.nghiatran.halofind.util.InterfaceHelper;


public class RegisterActivity extends BaseActivity {
    TextInputLayout pass, email, confirm;
    Button signin;
    ArrayList<User> users;
    boolean a = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
        InterfaceHelper.addActivityToolbar(this);
        InterfaceHelper.addSupportActionBar(this);
        pass = (TextInputLayout) findViewById(R.id.edtPassword);
        confirm = (TextInputLayout) findViewById(R.id.edtConfirmPassword);
        email = (TextInputLayout) findViewById(R.id.edtEmail);
        signin = (Button) findViewById(R.id.btnRegister);
        HaloFindAPI.getAllUser(RegisterActivity.this);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a = false;
                if (users != null) {
                    for (User user : users) {
                        if (user.getEmail().equalsIgnoreCase(email.getEditText().getText().toString())) {
                            Toast.makeText(getApplicationContext(), R.string.exis, Toast.LENGTH_LONG).show();
                            a = true;
                        }
                    }
                    if (pass.getEditText().getText().toString().equalsIgnoreCase(confirm.getEditText().getText().toString()) && a == false) {
                        HaloFindAPI.register("", "", pass.getEditText().getText().toString(), email.getEditText().getText().toString(), users.size(), RegisterActivity.this);
                        Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
                        intent.putExtra(HaloFindHelper.USERS, email.getEditText().getText().toString());
                        startActivity(intent);
                        finish();
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
