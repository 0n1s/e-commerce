package eccomerce.com;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static int item_id=0;
    public static JSONArray   jsonArray ;
    ListView listView;
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    private Context mContext;
    RecyclerView.LayoutManager layoutManager;
    String actvity_title;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    String uID;
    SharedPreferences sharedpreferences;
    public List<ItemData> listitems;
    public String fetched_data_foods;
    private FirebaseAnalytics mFirebaseAnalytics;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Easy buy n sell");


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                fetch_items();
            }
        });


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String email= "new_user";
        try
        {
            email = sharedPreferences.getString("email", "new _ user");
        }
        catch (Exception ex)
        {

        }

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "User trackers");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "User "+email+" opened app!");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "text");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);



        findViewById(R.id.button3).setVisibility(View.GONE);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent=getIntent();
        uID=intent.getStringExtra("email");
        fetch_items();





    }

    public  void Search(final String search_string)
    {

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);//done here
        layoutManager = new GridLayoutManager(mContext, 1);
        recyclerView.setLayoutManager(layoutManager);

        JSONObject jsonObject = null;
        ItemData itemdata;


        try {
            jsonObject = new JSONObject(fetched_data_foods);
            JSONArray result = jsonObject.getJSONArray("result");
            listitems = new ArrayList<>();
            for (int i = 0; i < result.length(); i++)
            {
                JSONObject jo = result.getJSONObject(i);
                String item_name=jo.getString("name");
                String item_price=jo.getString("price");
                String image_url=jo.getString("image");
                String quantity_remaining = jo.getString("quantity_remaining");
                String item_description=jo.getString("contents");
                String category=jo.getString("category");
                String qntytype=jo.getString("qntytype");

                if(search_string.toLowerCase().equals(item_name.toLowerCase()))
                {
                    itemdata = new ItemData(item_price, image_url, item_name, item_description,category,quantity_remaining, qntytype);
                    listitems.add(itemdata);

                }


            }

            if(listitems.size() < 0)
            {
                adapter = new MyAdapter(listitems,MainActivity.this);
                recyclerView.setAdapter(adapter);
            }
            else {
                Toast.makeText(MainActivity.this, "Not found", Toast.LENGTH_SHORT).show();
            }





        } catch (JSONException e) {

//
            Toast.makeText(MainActivity.this, String.valueOf(e), Toast.LENGTH_SHORT).show();

        }







    }


    public  void fetch_items()
    {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching items...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.main_url+"item_fetcher.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        progressDialog.cancel();

                        if(mSwipeRefreshLayout.isRefreshing())
                        {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        Log.d("foods", response);
                        fetched_data_foods=response;
                        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);//done here
                        layoutManager = new GridLayoutManager(mContext, 1);
                        recyclerView.setLayoutManager(layoutManager);

                        JSONObject jsonObject = null;
                        ItemData itemdata;


                        try {
                            jsonObject = new JSONObject(response);

                            JSONArray result = jsonObject.getJSONArray("result");

                            listitems = new ArrayList<>();
                            for (int i = 0; i < result.length(); i++)
                            {
                                JSONObject jo = result.getJSONObject(i);
                                String item_price=jo.getString("price");
                                String image_url=jo.getString("image");
                                String item_name=jo.getString("name");
                                String quantity_remaining = jo.getString("quantity_remaining");
                                String item_description=jo.getString("contents");
                                String category=jo.getString("category");

                                String qntytype=jo.getString("qntytype");

                                itemdata = new ItemData(item_price, image_url, item_name, item_description,category,quantity_remaining, qntytype);
                                listitems.add(itemdata);
                            }
                            adapter = new MyAdapter(listitems,MainActivity.this);
                            recyclerView.setAdapter(adapter);



                        } catch (JSONException e)
                        {


                            Toast.makeText(MainActivity.this, String.valueOf(e), Toast.LENGTH_SHORT).show();

                        }














                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
               // Toast.makeText(mContext, String.valueOf(error), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(MainActivity.this, Cart.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_camera)
        {
            Intent intent = new Intent(MainActivity.this, Utensils.class);
            intent.putExtra("category", "offers");
            intent.putExtra("uDI",uID);
            startActivity(intent);
        }

        if (id == R.id.logout)
        {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("email", "");
            editor.putString("password", "");
            editor.putString("phone", "");
            editor.putString("location", "");
            editor.commit();

            startActivity(new Intent(MainActivity.this, SignIN.class));
            this.finish();
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

