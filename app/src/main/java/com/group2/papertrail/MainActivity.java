package com.group2.papertrail;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.group2.papertrail.database.DatabaseManager;
import com.group2.papertrail.databinding.ActivityMainBinding;
import com.group2.papertrail.ui.auth.LoginActivity;
import com.group2.papertrail.ui.standalone.AddPDFActivity;
import com.group2.papertrail.util.SharedPreferencesManager;

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
            if (item.getItemId() == R.id.Hamburger) {
                showPopMenu(binding.myToolbar.findViewById(R.id.Hamburger));
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);

        return super.onCreateOptionsMenu(menu);
    }


    private void showPopMenu(View view){
        PopupMenu popupMenu = new PopupMenu(this , view);

        popupMenu.getMenuInflater().inflate(R.menu.hamburger_menu_item, popupMenu.getMenu());

        popupMenu.setForceShowIcon(true);

        popupMenu.setOnMenuItemClickListener(menuItem ->{
            if (menuItem.getItemId() == R.id.action_delete_tabs){
                // do delete tabs action
                Toast.makeText(this, "Delete tabs clicked", Toast.LENGTH_SHORT).show();
            } else if (menuItem.getItemId() == R.id.action_logout) {
                // do logout action
                try {
                    logout();
                }catch (Exception e){
                    Toast.makeText(this, "Failed to logout", Toast.LENGTH_SHORT).show();
                }
            }

            return true;
        });
        popupMenu.show();
    }

    private void logout(){
        SharedPreferencesManager.getInstance(getApplicationContext()).saveUserId(0);

        Intent intent = new Intent(this, LoginActivity.class);

        startActivity(intent);

        finish();
    }
}