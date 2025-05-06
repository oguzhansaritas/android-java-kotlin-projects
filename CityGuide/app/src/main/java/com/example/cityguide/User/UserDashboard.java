package com.example.cityguide.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.cityguide.Common.LoginSignup.RetailerStartUpScreen;
import com.example.cityguide.HelperClasses.HomeAdapter.FeaturedAdapter;
import com.example.cityguide.HelperClasses.HomeAdapter.FeaturedHelperClass;
import com.example.cityguide.HelperClasses.HomeAdapter.MvAdapter;
import com.example.cityguide.HelperClasses.HomeAdapter.MvHelperClass;
import com.example.cityguide.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;


public class UserDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static final float END_SCALE = 0.7f;

    RecyclerView featuredRecycler, mvRecycler;
    RecyclerView.Adapter adapter;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    LinearLayout contentView;

    ImageView menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_dashboard);

        featuredRecycler = findViewById(R.id.featured_recycler);
        mvRecycler = findViewById(R.id.mv_recycler);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);


        NavigationDraver();


        featuredRecycler();
        mvRecycler();

    }

    private void NavigationDraver() {

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        menuIcon.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                                                drawerLayout.closeDrawer(GravityCompat.START);
                                            } else drawerLayout.openDrawer(GravityCompat.START);

                                        }
                                    }
        );
        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {
        drawerLayout.setScrimColor(getResources().getColor(R.color.colorPrimary));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else

            super.onBackPressed();
    }

    private void mvRecycler() {

        mvRecycler.setHasFixedSize(true);
        mvRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<MvHelperClass> mvLocations = new ArrayList<>();

        mvLocations.add(new MvHelperClass(R.drawable.mc, "Mcdonald's", "Back in 1954, a man named Ray Kroc discovered a small burger restaurant in California, " +
                "and wrote the first page of our history. From humble beginnings as a small restaurant, we're proud to have become one of the world's leading food service brands with more than 36," +
                "000 restaurants in more than 100 countries."));

        mvLocations.add(new MvHelperClass(R.drawable.edenrobe, "Edenrobe", "It all started under the name of EDEN APPARELS – the effervescent company founded in 1988. " +
                "Always fascinated by distinguished styles, the company bent its efforts to bringing colors, designs, fabrics & magic together. " +
                "The initiative turned out to be a success and the company quickly became popular for its quality kids wear and later expanded its portfolio to rise as edenrobe – " +
                "offering customers wide variety of finest ready-to-wear crafted to meet the ever changing demands of a modern family wardrobe."));

        mvLocations.add(new MvHelperClass(R.drawable.subway, "Subway", "The story of the Subway brand started more than 50 years ago when Dr. Peter Buck, " +
                "a nuclear physicist, changed the life of a college student with a few simple words, “Let’s open a submarine sandwich shop.”"));

        adapter = new MvAdapter(mvLocations);
        mvRecycler.setAdapter(adapter);


    }

    private void featuredRecycler() {

        featuredRecycler.setHasFixedSize(true);
        featuredRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<FeaturedHelperClass> featuredLocations = new ArrayList<>();

        featuredLocations.add(new FeaturedHelperClass(R.drawable.mc, "Mcdonald's", "Back in 1954, a man named Ray Kroc discovered a small burger restaurant in California, " +
                "and wrote the first page of our history. From humble beginnings as a small restaurant, we're proud to have become one of the world's leading food service brands with more than 36," +
                "000 restaurants in more than 100 countries."));

        featuredLocations.add(new FeaturedHelperClass(R.drawable.edenrobe, "Edenrobe", "It all started under the name of EDEN APPARELS – the effervescent company founded in 1988. " +
                "Always fascinated by distinguished styles, the company bent its efforts to bringing colors, designs, fabrics & magic together. " +
                "The initiative turned out to be a success and the company quickly became popular for its quality kids wear and later expanded its portfolio to rise as edenrobe – " +
                "offering customers wide variety of finest ready-to-wear crafted to meet the ever changing demands of a modern family wardrobe."));

        featuredLocations.add(new FeaturedHelperClass(R.drawable.subway, "Subway", "The story of the Subway brand started more than 50 years ago when Dr. Peter Buck, " +
                "a nuclear physicist, changed the life of a college student with a few simple words, “Let’s open a submarine sandwich shop.”"));

        adapter = new FeaturedAdapter(featuredLocations);
        featuredRecycler.setAdapter(adapter);

        GradientDrawable gradient1 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffeff400, 0xffaff600});


    }

    public void callRetailerScreens(View view){
        startActivity(new Intent(getApplicationContext(), RetailerStartUpScreen.class));

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_all_categories:
                startActivity(new Intent(getApplicationContext(),AllCategories.class));
                break;
        }

        return true;
    }
}