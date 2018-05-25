package eccomerce.com;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SignIN extends AppCompatActivity {
    EditText _emailText,_passwordText;
    Button _loginButton;
    Switch checkBox;

    public static String useruser;

    public static final String MyPREFERENCES = "MyPrefs";
    TextView _signupLink;
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    SharedPreferences sharedpreferences;


   // http:///Maathai/new.php


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        getSupportActionBar().setTitle("Easy buy n sell");
        checkBox= (Switch)findViewById(R.id.switch1);
        checkBox.setVisibility(View.GONE);
        _emailText=(EditText)findViewById(R.id.input_email);
        _passwordText=(EditText)findViewById(R.id.input_password);
        _loginButton=(Button)findViewById(R.id.btn_login);
        _signupLink=(TextView)findViewById(R.id.link_signup);
        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SignIN.this, SignupActivity.class);
                startActivity(intent);
            }
        });
        String email= sharedpreferences.getString("email", "null");
        String password=sharedpreferences.getString("password","null");


        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                login();
            }
        });


    }




    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }


        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        loginnow(email,password);


    }

    public void rememberme(final String email,final String password)
    {

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.commit();

    }
    public void loginnow (final String email,final String password)

    {
        class AddEmployee extends AsyncTask<Void,Void,String> {
            ProgressDialog progressDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = new ProgressDialog(SignIN.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();

            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("email",email);
                params.put("password",password);
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(URLs.main_url+"login.php", params);
                return res;

            }

            @Override
            protected void onPostExecute(final String s) {
                super.onPostExecute(s);
                progressDialog.dismiss();
                login_verify(s,email,password);


            }
        }
        AddEmployee ae = new AddEmployee();
        ae.execute();


    }

    public void login_verify(String s,String email,String password)
    {

Log.d("TAG_loginresult", s);

        try {
            JSONObject json = new JSONObject(s);
            JSONArray array = json.getJSONArray("result");
            JSONObject c = array.getJSONObject(0);
            String succes =c.getString("succes");
            String phone = c.getString("phone");
            String location = c.getString("location");

            if (succes.equals("1"))
            {

                useruser=email;
                rememberme(email, password);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.putString("phone", phone);
                editor.putString("location", location);

                editor.commit();

                Intent intent=new Intent(SignIN.this,MainActivity.class);
                intent.putExtra("email",email);
                startActivity(intent);
                finish();


            }
            else if (succes.equals("0"))
            {
                Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_SHORT).show();
            }


        } catch (JSONException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }


    }


    public void onLoginFailed() {
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty()) {
            _emailText.setError("enter a valid email username");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Between 4 and ten characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

}
