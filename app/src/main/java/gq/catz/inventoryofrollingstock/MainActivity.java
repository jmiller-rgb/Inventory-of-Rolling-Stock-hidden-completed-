package gq.catz.inventoryofrollingstock;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

import gq.catz.inventoryofrollingstock.ui.viewInventory.ViewInventoryFragment;


public class MainActivity extends AppCompatActivity {
	private static final int OPEN_FILE = 0;
	public NavController navController;
	public AppBarConfiguration appBarConfiguration;
	Intent requestFileIntent;
	ParcelFileDescriptor inputPFD;
	boolean importSuccessful = true;
	private static final int CREATE_FILE = 1;
	public String activeFragment;

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
		activeFragment = "viewInventory";
		navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
		NavigationUI.setupWithNavController(navView, navController);
	}

	@Override
	public void onBackPressed() {
		if (activeFragment.equals("viewInventory")) {
			Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.navigation_dashboard);
			if (fragment instanceof ViewInventoryFragment) {
				if (((ViewInventoryFragment) fragment).handleOnBackPressed()) {
					Toast.makeText(this, "Items unselected", Toast.LENGTH_SHORT).show();
				} else {
					super.onBackPressed();
				}
			} else {
				super.onBackPressed();
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

	public void importRollingStock() {
		requestFileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		requestFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
		requestFileIntent.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension("csv"));
		requestFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		startActivityForResult(requestFileIntent, OPEN_FILE);
	}

	public void exportRollingStock() {
		Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension("csv"));
		intent.putExtra(Intent.EXTRA_TITLE, "Inventory of Rolling Stock - Rolling Stock Export.csv");

		// Optionally, specify a URI for the directory that should be opened in
		// the system file picker when your app creates the document.
		//intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

		startActivityForResult(intent, CREATE_FILE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent returnIntent) {
		if (requestCode == CREATE_FILE) {
			if (resultCode != RESULT_OK) { // if creating the file failed, then:
				Toast.makeText(this, "Failed to export csv file. Reason is unknown. Sorry! :(\n - whiskersOfDeath", Toast.LENGTH_LONG).show();
				return;
			} else { // otherwise:
				Uri returnUri;
				//noinspection UnusedAssignment
				if (returnIntent != null && (returnUri = returnIntent.getData()) != null) {
					returnUri = returnIntent.getData();

					try {
						inputPFD = getContentResolver().openFileDescriptor(returnUri, "rw");
					} catch (FileNotFoundException e) {
						Toast.makeText(this, "Failed to export csv file. Reason: File wasn't found. Sorry! :(\n - whiskersOfDeath", Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
					FileDescriptor fd;
					fd = inputPFD != null ? inputPFD.getFileDescriptor() : null;
					boolean exportSuccessful = RollingStockManager.get(this).exportRollingStock(this, fd);
					if (exportSuccessful) {
						Toast.makeText(this, "Exported to csv file successfully. Reason: Nothing failed. Yay! :)\n - whiskersOfDeath", Toast.LENGTH_LONG).show();
					}
				}
			}
		} else if (requestCode == OPEN_FILE) {
			// If the selection didn't work
			if (resultCode != RESULT_OK) {
				// Exit without doing anything else
				return;
			} else {
				// Get the file's content URI from the incoming Intent
				Uri returnUri;
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
					FileDescriptor fd;
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
		}
		super.onActivityResult(requestCode, resultCode, returnIntent);
	}

	@Override
	protected void onDestroy() {
		RollingStockManager.get(this).finish();
		super.onDestroy();
	}
}