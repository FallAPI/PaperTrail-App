package com.group2.papertrail;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.group2.papertrail.database.DatabaseManager;
import com.group2.papertrail.databinding.ActivityMainBinding;
import com.group2.papertrail.ui.standalone.AddPDFActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        setSupportActionBar(binding.myToolbar);

//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        binding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddPDFActivity.class);
            startActivity(intent);
        });

        binding.myToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.reset) {
                DatabaseManager.getInstance(getApplicationContext()).resetDatabaseAndAddStaticValues(() -> {
                    Toast.makeText(this, "Database reset and static values added!", Toast.LENGTH_SHORT).show();
                });
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);

        return super.onCreateOptionsMenu(menu);
    }
}