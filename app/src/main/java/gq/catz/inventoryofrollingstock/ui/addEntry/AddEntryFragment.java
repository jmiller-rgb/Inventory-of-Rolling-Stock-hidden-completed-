package gq.catz.inventoryofrollingstock.ui.addEntry;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.Snackbar;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;
import java.util.UUID;

import gq.catz.inventoryofrollingstock.R;
import gq.catz.inventoryofrollingstock.RollingStockItem;
import gq.catz.inventoryofrollingstock.RollingStockManager;

public class AddEntryFragment extends Fragment {

	private EditText reportingMark, fleetID, owningCompany;
	private Spinner stockType;
	private MaterialCheckBox isEngine, isLoaded, isRented;
	private MaterialButton addBtn;
	private ArrayAdapter<CharSequence> spinnerAdapter;
	private UUID editingRollingStockUUID = null;
	private boolean isEditing = false;
	private RollingStockItem rollingStockItem;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_addentry, container, false);

		reportingMark = v.findViewById(R.id.editReportingMark);
		fleetID = v.findViewById(R.id.editFleetID);
		owningCompany = v.findViewById(R.id.editOwningCompany);
		stockType = v.findViewById(R.id.stockTypeSpinner);
		isEngine = v.findViewById(R.id.isEngineChkBox);
		isLoaded = v.findViewById(R.id.isLoadedChkBox);
		isRented = v.findViewById(R.id.isRentedChkBox);
		addBtn = v.findViewById(R.id.addRollingStockBtn);
		spinnerAdapter = ArrayAdapter.createFromResource(requireActivity(), R.array.stockTypes, com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
		stockType.setAdapter(spinnerAdapter);

		if (getArguments() != null && !getArguments().isEmpty()) {
			isEditing = getArguments().getBoolean("editing", false);
			if (isEditing) {
				addBtn.setText(R.string.addBtn_Edit);
				editingRollingStockUUID = (UUID) getArguments().getSerializable("rollingStockUUID");
				try {
					rollingStockItem = RollingStockManager.get(getActivity()).getRollingStock(editingRollingStockUUID);
				} catch (Exception e) {
					Snackbar.make(v, "Error: No rolling stock found!", Snackbar.LENGTH_INDEFINITE).show();
					getParentFragmentManager().popBackStack();
				}
				reportingMark.setText(rollingStockItem.getReportingMark());
				fleetID.setText(Integer.toString(rollingStockItem.getFleetID()));
				owningCompany.setText(rollingStockItem.getOwningCompany());
				int stockTypeSelectionPos = 0;
				for (int i = 0; i < stockType.getCount(); i++) {
					if (((String) stockType.getItemAtPosition(i)).equals(rollingStockItem.getStockType()))
						stockTypeSelectionPos = i;
				}
				stockType.setSelection(stockTypeSelectionPos);
				isEngine.setChecked(rollingStockItem.isEngine());
				isLoaded.setChecked(rollingStockItem.isLoaded());
				isRented.setChecked(rollingStockItem.isRented());
			}
		}

		{
			isEngine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
					if (compoundButton.isChecked()) {
						stockType.setEnabled(false);
					} else {
						stockType.setEnabled(true);
					}
				}
			});

		} // Is Engine checkbox onChange listener

		{
			isRented.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
					if (compoundButton.isChecked()) {
						owningCompany.setEnabled(false);
					} else {
						owningCompany.setEnabled(true);
					}
				}
			});
		} // Is Rented checkbox onChange listener

		{
			addBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String reportingMarkStr = reportingMark.getText().toString(),
							fleetIDStr = fleetID.getText().toString(),
							owningCompanyStr = owningCompany.getText().toString(),
							stockTypeStr = (String) stockType.getSelectedItem(),
							isEngineStr = Boolean.toString(isEngine.isChecked()),
							isLoadedStr = Boolean.toString(isLoaded.isChecked()),
							isRentedStr = Boolean.toString(isRented.isChecked());
					if (isEngine.isChecked()) {
						stockTypeStr = "engine";
					}
					if (isRented.isChecked()) {
						owningCompanyStr = "N/A";
					}
					if (stockTypeStr.equals("engine")) {
						isLoadedStr = "N/A";
					}
					if (!isEditing) {
						rollingStockItem = new RollingStockItem(reportingMarkStr, fleetIDStr, stockTypeStr, owningCompanyStr, isEngineStr, isLoadedStr, isRentedStr);
						RollingStockManager.get(getActivity()).addRollingStock(rollingStockItem);
					} else {
						rollingStockItem.setReportingMark(reportingMarkStr);
						rollingStockItem.setFleetID(fleetIDStr);
						rollingStockItem.setOwningCompany(owningCompanyStr);
						rollingStockItem.setStockType(stockTypeStr);
						rollingStockItem.setEngine(isEngine.isChecked());
						rollingStockItem.setLoaded(isLoaded.isChecked());
						rollingStockItem.setRented(isRented.isChecked());
						RollingStockManager.get(getActivity()).updateRollingStock(rollingStockItem);
						getParentFragmentManager().popBackStack();
					}
				}
			});
		} // addBtn onClickListener
		return v;
	}

	public static AddEntryFragment newEditingInstance(UUID rollingStockUUID) {
		Bundle args = new Bundle();
		args.putBoolean("editing", true);
		args.putSerializable("rollingStockUUID", rollingStockUUID);

		AddEntryFragment fragment = new AddEntryFragment();
		fragment.setArguments(args);
		return fragment;
	}
}