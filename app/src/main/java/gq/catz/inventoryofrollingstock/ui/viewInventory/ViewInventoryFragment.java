package gq.catz.inventoryofrollingstock.ui.viewInventory;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
	private RecyclerView stockView;
	private List<RollingStockItem> rollingStockItems; // Items for stockView
	private StockAdapter stockAdapter;
	private RollingStockManager rsm;
	private MenuItem menuItemDelete;
	private boolean selectionMode = false;
	private int selectedItemsNum = 0;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((MainActivity) requireActivity()).activeFragment = "viewInventory";
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
		menuItemDelete = menu.findItem(R.id.menuitem_delete);
	}

	@Override
	public boolean onOptionsItemSelected(@NotNull MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.menuitem_addRollingStock:
				((MainActivity) requireActivity()).navController.navigate(R.id.navigation_home);
				break;
			case R.id.menuitem_import:
				openFile();
				updateList(rsm.getRollingStocks());
				stockAdapter = null;
				stockAdapter = new StockAdapter(rsm.getRollingStocks());
				stockView.setAdapter(stockAdapter);
				break;
			case R.id.menuitem_delete:
				List<StockHolder> stockHolderList = stockAdapter.getStockHolders();

				for (StockHolder stockHolder : stockHolderList) {
					if (stockHolder.isCardChecked()) {
						rsm.removeRollingStock(stockHolder.item.getId());
						stockHolder.cardView.findViewById(R.id.cardExpansion).setVisibility(View.GONE);
						Objects.requireNonNull(stockView.getAdapter()).notifyItemRemoved(stockView.getChildAdapterPosition((View) stockHolder.cardView));
						((StockAdapter) stockView.getAdapter()).setRollingStockItems(RollingStockManager.get(getActivity()).getRollingStocks());
					}
				}
				menuItemDelete.setEnabled(false);
				menuItemDelete.setVisible(false);
				break;
			case R.id.menuitem_export:
				exportFile();
		}
		return false;
	} // inflate and handle option menu

	private void openFile() {
		((MainActivity) requireActivity()).importRollingStock();
	} // openFile

	private void exportFile() {
		((MainActivity) requireActivity()).exportRollingStock();
	}

	public boolean handleOnBackPressed() {
		Toast.makeText(getActivity(), "Back Pressed", Toast.LENGTH_LONG).show();
		boolean cardChecked = false;
		/*for (int i = 0; i < stockAdapter.getItemCount(); i++) {
			StockHolder stockHolder = (StockHolder) stockView.findViewHolderForAdapterPosition(i);
			if (stockHolder != null && stockHolder.cardView.isChecked()) {
				cardChecked = true;
				stockHolder.cardView.setChecked(false);
			}
		}
		return cardChecked;*/

		List<StockHolder> stockHolderList = stockAdapter.getStockHolders();
		for (StockHolder stockHolder : stockHolderList) {
			if (stockHolder.isCardChecked()) {
				cardChecked = true;
				stockHolder.uncheckCard();
			}
		}
		if (cardChecked) {
			menuItemDelete.setVisible(false);
			menuItemDelete.setEnabled(false);
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
		CardViewCheckListener cvcl;

		public StockHolder(View v) {
			super(v);
			cvcl = new CardViewCheckListener();
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
					//Toast.makeText(getActivity(), "MEOW", Toast.LENGTH_LONG).show();
					if (!selectionMode) {
						final LinearLayout cardViewExpansion = cardView.findViewById(R.id.cardExpansion);
						if (cardViewExpansion.getVisibility() == View.VISIBLE) {
							cardViewExpansion.setVisibility(View.GONE);
						} else {
							cardViewExpansion.setVisibility(View.VISIBLE);
						}
					} else {
						cardView.setChecked(!cardView.isChecked());
					}
				}
			});
			cardView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					cardView.setChecked(!cardView.isChecked());
					selectionMode = true;
					menuItemDelete.setEnabled(true);
					menuItemDelete.setVisible(true);
					return true;
				}
			});
			cardView.setOnCheckedChangeListener(cvcl);
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
					Objects.requireNonNull(stockView.getAdapter()).notifyItemRemoved(stockView.getChildAdapterPosition((View) view.getParent().getParent().getParent())); // deleteBtn.cardExpansion.cardContainer.MaterialCardView
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
			cardView.setOnCheckedChangeListener(null);
			cardView.setChecked(rollingStockItem.isChecked());
			cardView.setOnCheckedChangeListener(cvcl);
			//item = rollingStockItem;
		}

		public boolean isCardChecked() {
			return item.isChecked();
		} // isCardChecked

		public void uncheckCard() {
			cardView.setChecked(false);
		}

		private class CardViewCheckListener implements MaterialCardView.OnCheckedChangeListener {
			@Override
			public void onCheckedChanged(MaterialCardView card, boolean isChecked) {
				item.setChecked(cardView.isChecked());
				if (isChecked)
					selectedItemsNum++;
				else
					selectedItemsNum--;
				if (selectedItemsNum == 0) {
					menuItemDelete.setVisible(false);
					menuItemDelete.setEnabled(false);
					selectionMode = false;
				}
			}
		}
	}

	private class StockAdapter extends RecyclerView.Adapter<StockHolder> {
		private List<RollingStockItem> rollingStockItems;
		private List<StockHolder> stockHolders;


		public StockAdapter(List<RollingStockItem> rollingStockItemsList) {
			rollingStockItems = rollingStockItemsList;
			stockHolders = new ArrayList<>();
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
			StockHolder stockHolder = new StockHolder(inflater.inflate(R.layout.rolling_stock_listitem, parent, false));
			stockHolders.add(stockHolder);
			return stockHolder;
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

		public List<StockHolder> getStockHolders() {
			return stockHolders;
		}
	}
}

