package gq.catz.inventoryofrollingstock;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import gq.catz.inventoryofrollingstock.ui.viewInventory.ViewInventoryFragment;


public class MainActivity extends AppCompatActivity {
	private String activeFragment;
	public NavController navController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		BottomNavigationView navView = findViewById(R.id.nav_view);
		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
				R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
				.build();
		activeFragment = "dashboard";
		navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
		NavigationUI.setupWithNavController(navView, navController);
	}

	@Override
	public void onBackPressed() {
		if (activeFragment.equals("dashboard")) {
			Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.navigation_dashboard);
			if (fragment instanceof ViewInventoryFragment) {
				if (((ViewInventoryFragment) fragment).handleOnBackPressed()) {

				} else {
					super.onBackPressed();
				}
			}
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onNightModeChanged(int mode) {
		//getResources().getColor(R.color.colorPrimary, getTheme());
		if (mode == AppCompatDelegate.MODE_NIGHT_YES) {
			getTheme().applyStyle(R.style.AppThemeDark, true);
		} else if (mode == AppCompatDelegate.MODE_NIGHT_NO) {
			getTheme().applyStyle(R.style.AppThemeLight, true);
		}
		super.onNightModeChanged(mode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
}