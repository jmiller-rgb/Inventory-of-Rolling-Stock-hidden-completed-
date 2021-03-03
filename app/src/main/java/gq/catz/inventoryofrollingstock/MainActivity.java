package gq.catz.inventoryofrollingstock;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import gq.catz.inventoryofrollingstock.ui.viewInventory.ViewInventoryFragment;


public class MainActivity extends AppCompatActivity {
	private String activeFragment;
	public NavController navController;
	public AppBarConfiguration appBarConfiguration;
	Intent requestFileIntent;
	ParcelFileDescriptor inputPFD;
	boolean importSuccessful = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		BottomNavigationView navView = findViewById(R.id.nav_view);
		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		appBarConfiguration = new AppBarConfiguration.Builder(
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

	public boolean importRollingStock() {
		requestFileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		requestFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
		requestFileIntent.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension("csv"));
		requestFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		startActivityForResult(requestFileIntent, 0);
		return importSuccessful;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent returnIntent) {
		// If the selection didn't work
		if (resultCode != RESULT_OK) {
			// Exit without doing anything else
			return;
		} else {
			// Get the file's content URI from the incoming Intent
			Uri returnUri = null;
			if (returnIntent != null) {
				returnUri = returnIntent.getData();

				try {
					/*
					 * Get the content resolver instance for this context, and use it
					 * to get a ParcelFileDescriptor for the file.
					 */
					if (returnUri != null) {
						inputPFD = getContentResolver().openFileDescriptor(returnUri, "r");
					} else {
						importSuccessful = false;
						return;
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					Log.e("MainActivity", "File not found.");
					return;
				}
				// Get a regular file descriptor for the file
				FileDescriptor fd = null;
				if (inputPFD != null) {
					fd = inputPFD.getFileDescriptor();
					importSuccessful = RollingStockManager.get(this).importRollingStock(fd);
				} else {
					importSuccessful = false;
				}
			} else {
				importSuccessful = false;
				return;
			}
			//Uri returnUri = FileProvider.(this, this.getApplicationContext().getPackageName() + ".provider", returnIntent.getData());
			/*
			 * Try to open the file for "read" access using the
			 * returned URI. If the file isn't found, write to the
			 * error log and return.
			 */
		}
		super.onActivityResult(requestCode, resultCode, returnIntent);
	}
}