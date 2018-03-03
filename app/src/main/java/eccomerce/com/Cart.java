package eccomerce.com;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static eccomerce.com.Details.MyPREFERENCES;


public class Cart extends AppCompatActivity
    {
    ListView list_item;
    TextView textView25;
    String shopping_list;
    int price=0;
    Button checkout;
    EditText location, phone_number;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        list_item=(ListView)findViewById(R.id.list_item);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
          shopping_list= sharedPreferences.getString("shopping_list", "null");
        textView25=(TextView)findViewById(R.id.textView25);
        showthem2(shopping_list);
        checkout=(Button)findViewById(R.id.button2);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





               final String email = sharedPreferences.getString("email", "null");

                if(email.equals("null"))
                {
                    startActivity(new Intent(Cart.this, SignIN.class));
                }
                else {


                    Calendar calendar;

                    try {
                        Log.d("TAG", String.valueOf(new JSONArray(shopping_list).length()));
                        if(price==0||new JSONArray(shopping_list).length()<1)
                        {
                            Toast.makeText(Cart.this, "Empty Shopping list", Toast.LENGTH_SHORT).show();
                        }
                        else

                            {


                                new AlertDialog.Builder(Cart.this)
                                        .setMessage("Are you sure you want to buy the selected good(s) for "+ String.valueOf(price)+" KES ?")
                                        .setTitle("Please confirm!")
                                        .setPositiveButton("Buy", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                makeorder( shopping_list, email, String.valueOf(Calendar.getInstance()), String.valueOf(price)
                                                        , phone_number.getText().toString(), location.getText().toString());
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .show();




                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }



            }
        });

                phone_number = (EditText)findViewById(R.id.editText3);
                phone_number.setText(sharedPreferences.getString("phone", ""));
                location = (EditText)findViewById(R.id.editText4);
                location.setText(sharedPreferences.getString("location", ""));
                list_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Cart.this);
                alert.setMessage("Are you sure you want to delete the selected item?");
                alert.setTitle("Please confirm");
                alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        try {
                            price=0;
                            JSONArray result = new JSONArray(shopping_list);
                            result.remove(position);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("shopping_list", String.valueOf(result));
                            editor.commit();
                            shopping_list= sharedPreferences.getString("shopping_list", "null");
                            showthem2(shopping_list);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                alert.show();



            }
        });

    }


    public void showthem2(String s)
    {
        textView25.setText(String.valueOf(price));
        JSONObject jsonObject = null;
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        try {

            JSONArray result = new JSONArray(s);
            for (int i = 0; i < result.length(); i++)
            {  JSONObject jo = result.getJSONObject(i);


                String name=jo.getString("food_name");
                String quantity=jo.getString("quantity");
                String final_amt=jo.getString("final_amt");
                String id=jo.getString("id");
                price=price+Integer.parseInt(final_amt);
                HashMap<String, String> employees = new HashMap<>();
                employees.put("name", name);
                employees.put("quantity", quantity);
                employees.put("final_amt", final_amt);
                employees.put("id", id);
                list.add(employees);

                textView25.setText(String.valueOf(price));
            }
            textView25.setText(String.valueOf(price));
            ListAdapter adapter = new SimpleAdapter(Cart.this, list, R.layout.events,
                    new String[]{"name", "quantity", "final_amt"}, new int[]{R.id.activity, R.id.descripption,R.id.textView22});
            list_item.setAdapter(adapter);


        } catch (JSONException e)
        {
            textView25.setText(String.valueOf(price));
            Toast.makeText(this, "Empty shopping list", Toast.LENGTH_SHORT).show();
        }}

    public void makeorder(final String contents, final String useruser, final String date, final  String price
    ,final String phone_number ,final String location
    )
    {
        class GetJSON extends AsyncTask<Void, Void, String> {

            ProgressDialog progressDialog = new ProgressDialog(Cart.this);

            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                progressDialog.setMessage("Placing your order..");
                progressDialog.show();

            }
            @Override
            protected String doInBackground(Void... params) {

                RequestHandler rh = new RequestHandler();
                HashMap<String,String> parabms = new HashMap<>();
                parabms.put("items",contents);
                parabms.put("price",price);
                parabms.put("phone_number",phone_number);
                parabms.put("location",location);
                parabms.put("date",date);
                parabms.put("trxID",String.valueOf(System.currentTimeMillis()));
                parabms.put("uid",useruser);

                Log.d("sent_data", String.valueOf(parabms));
                String res = rh.sendPostRequest(URLs.main_url+"saver.php", parabms);
                return res;

            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressDialog.dismiss();
                showthem3(s);
                Log.d("TAG", s);

            }


        }
        GetJSON jj =new GetJSON();
        jj.execute();
    }

    public void showthem3(String s)
    {
        JSONObject jsonObject = null;
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        try {
            jsonObject = new JSONObject(s);
            JSONArray result = jsonObject.getJSONArray("result");

            String itemID="";
            String succes="0";
            for (int i = 0; i < result.length(); i++)
            {
                JSONObject jo = result.getJSONObject(i);

                String success=jo.getString("status");

                if(success.equals("0"))
                {

                    new android.app.AlertDialog.Builder(Cart.this).setMessage("Oops! Something went wrong").show();
                }
                else{


                    new android.app.AlertDialog.Builder(Cart.this).setMessage("Your order has been received. You will a pop up prompting you to enter your bonga pin, do enter your pin and wil will charge you from your MPESA account. ").setTitle("Information.").setNegativeButton("Okay", null).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("shopping_list", "[]");
                    editor.commit();
                    shopping_list= sharedPreferences.getString("shopping_list", "null");
                    price=0;
                    showthem2(shopping_list);
                }

            }
        } catch (JSONException e) {

            Toast.makeText(this, "Ooops! Something went wrong", Toast.LENGTH_SHORT).show();
        }}











}
