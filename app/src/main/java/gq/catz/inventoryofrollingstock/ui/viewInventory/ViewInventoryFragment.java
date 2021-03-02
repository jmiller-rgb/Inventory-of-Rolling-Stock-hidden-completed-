package gq.catz.inventoryofrollingstock.ui.viewInventory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import gq.catz.inventoryofrollingstock.MainActivity;
import gq.catz.inventoryofrollingstock.R;
import gq.catz.inventoryofrollingstock.RollingStockItem;
import gq.catz.inventoryofrollingstock.RollingStockManager;
import gq.catz.inventoryofrollingstock.database.RollingStockCursorWrapper;
import gq.catz.inventoryofrollingstock.ui.addEntry.AddEntryFragment;

public class ViewInventoryFragment extends Fragment {

	//private static final int PICKFILE_RESULT_CODE = ;
	public static final int PICKFILE_RESULT_CODE = 1;
	//	private DashboardViewModel dashboardViewModel;
	private RecyclerView stockView;
	private List<RollingStockItem> rollingStockItems; // Items for stockView
	private StockAdapter stockAdapter;
	private RollingStockManager rsm;


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rsm = RollingStockManager.get(getActivity());
		rollingStockItems = rsm.getRollingStocks();
		setHasOptionsMenu(true);
	}

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_viewinventory, container, false);
		stockView = v.findViewById(R.id.rollingStockView);
		stockView.setLayoutManager(new LinearLayoutManager(getActivity()));
		stockAdapter = new StockAdapter(rollingStockItems);
		stockView.setAdapter(stockAdapter);

		final EditText searchEdit = v.findViewById(R.id.searchBox);
		Button searchBtn = v.findViewById(R.id.searchBtn);
		final TextView searchError = v.findViewById(R.id.searchError);
		searchBtn.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("TryFinallyCanBeTryWithResources")
			@Override
			public void onClick(View view) {
				String searchString = searchEdit.getText().toString();
				if (searchString.length() == 0) {
					searchError.setVisibility(View.VISIBLE);
				} else {
					List<RollingStockItem> rollingStockItemsSearch = new ArrayList<>();
					RollingStockCursorWrapper cursor = RollingStockManager.get(getActivity()).queryRollingStock(
							"lower(reportingMark)=? OR lower(fleetID)=? OR lower(stockType)=? OR lower(owningCompany)=?",
							new String[]{
									searchString.toLowerCase(),
									searchString.toLowerCase(),
									searchString.toLowerCase(),
									searchString.toLowerCase()
							}
					);
					try {
						cursor.moveToFirst();
						while (!cursor.isAfterLast()) {
							rollingStockItemsSearch.add(cursor.getRollingStock());
							cursor.moveToNext();
						}
					} finally {
						cursor.close();
					}
					updateList(rollingStockItemsSearch);
				}
			}
		});
		//getActivity().;
		return v;
	}

	@Override
	public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
		inflater.inflate(R.menu.menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NotNull MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.menuitem_addRollingStock:
				((MainActivity) requireActivity()).navController.navigate(R.id.navigation_home);
				break;
			case R.id.menuitem_import:
				openFile(null);
		}
		return false;
	} // inflate and handle option menu

	// Request code for selecting a PDF document.
	private static final int PICK_PDF_FILE = 2;

	private void openFile(@Nullable Uri pickerInitialUri) {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension("csv"));

		// Optionally, specify a URI for the file that should appear in the
		// system file picker when it loads.
		if (pickerInitialUri != null)
			intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

		startActivityForResult(intent, PICK_PDF_FILE);
//		Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
//
//		chooseFile = Intent.createChooser(chooseFile, "Choose a file");
//		startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
	} // openFile

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK){
			Uri content_describer = data.getData();
			String src = content_describer.getPath();
			File source = new File(src);
			Log.d("src is ", source.toString());
			Log.d("####@$@####", "Path is " + content_describer.getPath());
			String filename = content_describer.getLastPathSegment();
			Toast.makeText(getActivity(), filename, Toast.LENGTH_SHORT).show();
			Log.d("FileName is ", filename);
			File destination = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/copy/" + filename);
			Log.d("Destination is ", destination.toString());
			DirectoryExist(destination);
			try {
				copy(source, destination);
				rsm.importRollingStock(content_describer.getPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

	private void DirectoryExist(File destination) {

		if (!destination.isDirectory()) {
			if (destination.mkdirs()) {
				Log.d("####@$@####", "Directories created");
			} else {
				Log.d("####@$@####", "Directories not created");
			}
		}
	}
	
	private void copy(File source, File destination) throws IOException {

		FileChannel in = new FileInputStream(source).getChannel();
		FileChannel out = new FileOutputStream(destination).getChannel();

		try {
			in.transferTo(0, in.size(), out);
		} catch(Exception e){
			Log.d("Exception", e.toString());
		} finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
	}
	/*@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
		if (requestCode == PICK_PDF_FILE
				&& resultCode == Activity.RESULT_OK) {
			// The result data contains a URI for the document or directory that
			// the user selected.

			if (resultData != null) {
				Log.d("############ -> ", Objects.requireNonNull(resultData.getDataString()));
				Log.d("############ -> ", Objects.requireNonNull(Objects.requireNonNull(URI.parse(resultData.getDataString())).getPath()));
				/*File importCopy = null;
				try {
					InputStream inStream = requireActivity().getContentResolver().openInputStream(Objects.requireNonNull(resultData.getData()));
					importCopy = File.createTempFile("import", "meow", requireActivity().getCacheDir());
					importCopy.createNewFile();
					BufferedWriter writer = Files.newBufferedWriter(Paths.get(importCopy.toURI()));
					assert inStream != null;
					while (inStream.available() != 0) {
						writer.write(inStream.read());
					}
				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText(getActivity(), "Something went wrong whilst importing CSV file.", Toast.LENGTH_LONG).show();
				}*\/

				//assert importCopy != null;
				Log.d("$$#$#$#%#@%", Boolean.toString(resultData.getData() == null));
				String filePath = Commons.getPath(Objects.requireNonNull(resultData.getData()), getActivity());
				Log.d("#########$## -> ", filePath);

				RollingStockManager.get(getActivity()).importRollingStock(filePath);
				updateList(RollingStockManager.get(getActivity()).getRollingStocks());
				// Perform operations on the document using its URI.
			}
		}
	}*/ // onActivityResult

	public boolean handleOnBackPressed() {
		Toast.makeText(getActivity(), "Back Pressed", Toast.LENGTH_LONG).show();
		boolean cardChecked = false;
		for (int i = 0; i < stockAdapter.getItemCount(); i++) {
			StockHolder stockHolder = (StockHolder) stockView.findViewHolderForAdapterPosition(i);
			if (stockHolder != null && stockHolder.cardView.isChecked()) {
				cardChecked = true;
				stockHolder.cardView.setChecked(false);
			}
		}
		return cardChecked;
	}

	public void updateList(List<RollingStockItem> rollingStockItems) {
		Objects.requireNonNull((StockAdapter) stockView.getAdapter()).setRollingStockItems(rollingStockItems);
	}


	private class StockHolder extends ViewHolder {
		private MaterialCardView cardView;
		private TextView rollingStockInfoTitle, isEngine, stockType, isLoaded, owningCompany, isRented;
		public RollingStockItem item;

		public StockHolder(View v) {
			super(v);
			cardView = (MaterialCardView) v;
			rollingStockInfoTitle = itemView.findViewById(R.id.rollingStockInfoTitle);
			isEngine = itemView.findViewById(R.id.isEngine);
			stockType = itemView.findViewById(R.id.stockType);
			isLoaded = itemView.findViewById(R.id.isLoaded);
			owningCompany = itemView.findViewById(R.id.owningComany);
			isRented = itemView.findViewById(R.id.isRented);
			cardView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(getActivity(), "MEOW", Toast.LENGTH_LONG).show();
					LinearLayout cardViewExpansion = cardView.findViewById(R.id.cardExpansion);
					if (cardViewExpansion.getVisibility() == View.VISIBLE) {
						cardView.findViewById(R.id.cardExpansion).setVisibility(View.GONE);
					} else {
						cardView.findViewById(R.id.cardExpansion).setVisibility(View.VISIBLE);
					}
				}
			});
			cardView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					cardView.setChecked(true);
					return true;
				}
			});
			MaterialButton editBtn = cardView.findViewById(R.id.editCardBtn);
			editBtn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Toast.makeText(getActivity(), item.getId().toString(), Toast.LENGTH_LONG).show();
					AddEntryFragment fragment = AddEntryFragment.newEditingInstance(item.getId());
					FragmentManager fm = ViewInventoryFragment.this.getParentFragmentManager(); //requireActivity().getSupportFragmentManager();
					fm.beginTransaction().addToBackStack(null).hide(ViewInventoryFragment.this).replace(R.id.nav_host_fragment, fragment).commit();
				}
			});
			MaterialButton deleteBtn = cardView.findViewById(R.id.deleteCardBtn);
			deleteBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					rsm.removeRollingStock(item.getId());
					cardView.findViewById(R.id.cardExpansion).setVisibility(View.GONE);
					Objects.requireNonNull(stockView.getAdapter()).notifyItemRemoved(stockView.getChildAdapterPosition((View) view.getParent().getParent().getParent()));
					((StockAdapter) stockView.getAdapter()).setRollingStockItems(RollingStockManager.get(getActivity()).getRollingStocks());
				}
			});
		}

		@SuppressLint("SetTextI18n")
		public void bind(RollingStockItem rollingStockItem) {
			String title = rollingStockItem.getReportingMark() + " " + rollingStockItem.getFleetID();
			rollingStockInfoTitle.setText(title);
			isEngine.setText("Is Engine: " + rollingStockItem.isEngine());
			stockType.setText("Stock Type: " + rollingStockItem.getStockType());
			isLoaded.setText("Has Load: " + rollingStockItem.isLoaded());
			owningCompany.setText("Owning Company: " + rollingStockItem.getOwningCompany());
			isRented.setText("Is Rented: " + rollingStockItem.isRented());
		}

		/*public boolean isCardChecked() {
			return cardView.isChecked();
		}*/ // isCardChecked
	}

	private class StockAdapter extends RecyclerView.Adapter<StockHolder> {
		private List<RollingStockItem> rollingStockItems;

		public StockAdapter(List<RollingStockItem> rollingStockItemsList) {
			rollingStockItems = rollingStockItemsList;
		}

		/**
		 * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
		 * an item.
		 * <p>
		 * This new ViewHolder should be constructed with a new View that can represent the items
		 * of the given type. You can either create a new View manually or inflate it from an XML
		 * layout file.
		 * <p>
		 * The new ViewHolder will be used to display items of the adapter using
		 * . Since it will be re-used to display
		 * different items in the data set, it is a good idea to cache references to sub views of
		 * the View to avoid unnecessary {@link View#findViewById(int)} calls.
		 *
		 * @param parent   The ViewGroup into which the new View will be added after it is bound to
		 *                 an adapter position.
		 * @param viewType The view type of the new View.
		 * @return A new ViewHolder that holds a View of the given view type.
		 * @see #getItemViewType(int)
		 */
		@NonNull
		@Override
		public StockHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			return new StockHolder(inflater.inflate(R.layout.rolling_stock_listitem, parent, false));
		}

		/**
		 * Called by RecyclerView to display the data at the specified position. This method should
		 * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
		 * position.
		 * <p>
		 * Note that unlike {@link ListView}, RecyclerView will not call this method
		 * again if the position of the item changes in the data set unless the item itself is
		 * invalidated or the new position cannot be determined. For this reason, you should only
		 * use the <code>position</code> parameter while acquiring the related data item inside
		 * this method and should not keep a copy of it. If you need the position of an item later
		 * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
		 * have the updated adapter position.
		 * <p>
		 * Override  instead if Adapter can
		 * handle efficient partial bind.
		 *
		 * @param holder   The ViewHolder which should be updated to represent the contents of the
		 *                 item at the given position in the data set.
		 * @param position The position of the item within the adapter's data set.
		 */
		@Override
		public void onBindViewHolder(@NonNull StockHolder holder, int position) {
			RollingStockItem rsi = rollingStockItems.get(position);
			holder.item = rsi;
			holder.bind(rsi);
		}

		/**
		 * Returns the total number of items in the data set held by the adapter.
		 *
		 * @return The total number of items in this adapter.
		 */
		@Override
		public int getItemCount() {
			return rollingStockItems.size();
		}

		//@Override
		public void setRollingStockItems(List<RollingStockItem> newRollingStockItems) {
			rollingStockItems = newRollingStockItems;
			this.notifyDataSetChanged();
		}
	}
}