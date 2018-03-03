package eccomerce.com;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static eccomerce.com.SignIN.MyPREFERENCES;


public class Utensils extends AppCompatActivity {
    ListView listView;

    String getter="http://ecommerce.masomobora.com/android/item_fetcher2.php";
    String getter2="http://ecommerce.masomobora.com/android/item_fetcher22.php";

    String content;
    String price;
    String uID;
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {


        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utensils);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        getSupportActionBar().setTitle("My orders");
        listView=(ListView)findViewById(R.id.listview);
        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
                final  String itemid =   map.get("id");


            }});
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        uID = sharedpreferences.getString("email", "null");
        //Toast.makeText(this, uID, Toast.LENGTH_SHORT).show();

        getJSON();
    }

    public void getJSON()
    {
        class GetJSON extends AsyncTask<Void, Void, String> {

            ProgressDialog progressDialog = new ProgressDialog(Utensils.this);


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setMessage("Loading items");
                progressDialog.show();
            }
            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                HashMap<String, String> employees = new HashMap<>();
                employees.put("owner_id", uID);
                String res=rh.sendPostRequest(getter,employees);
                return res;

            }
            @Override
            protected void onPostExecute(String s)
            {
                super.onPostExecute(s);
                progressDialog.dismiss();
              //  new android.app.AlertDialog.Builder(Utensils.this).setMessage(s).show();
                showthem(s);
            }


        }
        GetJSON jj =new GetJSON();
        jj.execute();
    }



    private void showthem(String s)
    {

        Log.d("TAG_",s);
        JSONObject jsonObject = null;
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        try {

            jsonObject = new JSONObject(s);
            JSONArray result = jsonObject.getJSONArray("result");

            String itemID="";
            String succes="0";
            for (int i = 0; i < result.length(); i++)
            {  JSONObject jo = result.getJSONObject(i);


                succes=jo.getString("success");
                if (succes.equals("1")) {
                    String dateexe = jo.getString("date");
                    content = jo.getString("content");
                    price = jo.getString("price");
                    String trxID = jo.getString("trxID");
                    String name = jo.getString("content");


                    HashMap<String, String> employees = new HashMap<>();
                    employees.put("date", dateexe);
                    employees.put("name", name);
                    employees.put("price", price);
                    employees.put("id", trxID);
                    list.add(employees);
                }
                else
                {

                }





            }


        } catch (JSONException e)
        {

            Toast.makeText(this,String.valueOf(e), Toast.LENGTH_SHORT).show();

        }

        ListAdapter adapter = new SimpleAdapter(Utensils.this, list, R.layout.shoppinglists,
                new String[]{"name", "price","id","date"}, new int[]{R.id.date, R.id.price,R.id.id, R.id.textView26});
        listView.setAdapter(adapter);
    }


}
