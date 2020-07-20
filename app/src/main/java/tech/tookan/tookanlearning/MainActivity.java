package tech.tookan.tookanlearning;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import tech.tookan.tookanlearning.adapters.MyRecyclerAdapter;
import tech.tookan.tookanlearning.network.MyRetrofitInstance;
import tech.tookan.tookanlearning.network.RetroDataService;
import tech.tookan.tookanlearning.objects.User;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAnalytics mFireBaseAnalytics;
    public static final String URL = "https://api.github.com/users";
    final String TAG = "TAG";
    RequestQueue queue;
    RecyclerView recyclerView;
    NavigationView navigationView;
    MyRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR)
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        recyclerView = findViewById(R.id.main_recycler_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open
                , R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /** Uncomment below to use volley instead and comment retrofit method*/
        //populateUIWithVolley();
        populateUIWithRetrofit();
        mFireBaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    private void populateUIWithVolley() {
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                response -> {
                    //instantiating Gson
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();

                    //populate an array of our Json object. I used
                    //http://www.jsonschema2pojo.org/ to create a POJO file for it in
                    // objects package, called "User.java"
                    User[] users = gson.fromJson(response, User[].class);

                    //here i populated the recycler view using the adapter class i made
                    populateOurRecyclerView(users);
                }, error -> Toast.makeText(MainActivity.this, error.getMessage(),
                Toast.LENGTH_LONG).show());
        queue = Volley.newRequestQueue(this);
        queue.add(request);
        request.setTag(TAG);
        Toast.makeText(MainActivity.this, "Volley Successfully Worked!", Toast.LENGTH_SHORT).show();
    }

    private void populateUIWithRetrofit() {
        RetroDataService service = MyRetrofitInstance.getRetrofitInstance()
                .create(RetroDataService.class);
        Call<User[]> call = service.getUsersData();
        call.enqueue(new Callback<User[]>() {
            @Override
            public void onResponse(@NonNull Call<User[]> call, @NonNull retrofit2.Response<User[]> response) {
                populateOurRecyclerView(response.body());
                Toast.makeText(MainActivity.this, "Retrofit Successfully Worked!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<User[]> call, Throwable t) {
                call.cancel();
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateOurRecyclerView(User[] body) {
        recyclerView.setLayoutManager(new LinearLayoutManager
                (MainActivity.this));
        adapter = new MyRecyclerAdapter(MainActivity.this, body);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END))
            drawer.closeDrawer(GravityCompat.END);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_item)
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.forceCrash) {
            throw new RuntimeException("CRASH"); // force a crash for Crashlytics
        } else {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.END);
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }
}