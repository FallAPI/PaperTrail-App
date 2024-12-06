package com.group2.papertrail;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.group2.papertrail.dao.CategoryDAO;
import com.group2.papertrail.databinding.ActivityMainBinding;
import com.group2.papertrail.model.Category;
import com.group2.papertrail.ui.auth.LoginActivity;
import com.group2.papertrail.ui.library.CategoryViewModel;
import com.group2.papertrail.ui.library.ViewPagerAdapter;
import com.group2.papertrail.ui.standalone.AddPDFActivity;
import com.group2.papertrail.util.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    private CategoryViewModel categoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        categoryViewModel = new ViewModelProvider(
                this,
                new CategoryViewModel.Factory(getApplication())
        ).get(CategoryViewModel.class);

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
        CategoryDAO categoryDAO = new CategoryDAO(this);
        PopupMenu popupMenu = new PopupMenu(this , view);

        popupMenu.getMenuInflater().inflate(R.menu.hamburger_menu_item, popupMenu.getMenu());

        popupMenu.setForceShowIcon(true);

        popupMenu.setOnMenuItemClickListener(menuItem ->{
            if (menuItem.getItemId() == R.id.action_delete_tabs){
                // do delete category tab
                showDeleteCategoriesDialog();

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

    private void showDeleteCategoriesDialog() {
        // Get the current categories from the ViewModel
        List<Category> categoryList = categoryViewModel.getCategories().getValue();
        if (categoryList == null || categoryList.isEmpty() || categoryList.size() <= 1) {
            Toast.makeText(this, "No categories available to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        // Remove the last "Add" category from the list for deletion
        List<Category> deletableCategoryList = new ArrayList<>(categoryList.subList(0, categoryList.size() - 1));

        String[] categoryNames = new String[deletableCategoryList.size()];
        for (int i = 0; i < deletableCategoryList.size(); i++) {
            categoryNames[i] = deletableCategoryList.get(i).getName();
        }

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Categories");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_delete_categories, null);
        builder.setView(dialogView);

        ListView listView = dialogView.findViewById(R.id.category_list_view);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, categoryNames));
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        Button btnDelete = dialogView.findViewById(R.id.btn_delete);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnDelete.setOnClickListener(v -> {
            List<Category> selectedCategories = new ArrayList<>();
            SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
            for (int i = 0; i < checkedItems.size(); i++) {
                if (checkedItems.valueAt(i)) {
                    int index = checkedItems.keyAt(i);
                    selectedCategories.add(deletableCategoryList.get(index));
                }
            }

            if (selectedCategories.isEmpty()) {
                Toast.makeText(this, "No categories selected", Toast.LENGTH_SHORT).show();
                return;
            }

            // Delete categories via ViewModel
            categoryViewModel.deleteCategories(selectedCategories);

            Toast.makeText(this, "Selected categories deleted", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }


}