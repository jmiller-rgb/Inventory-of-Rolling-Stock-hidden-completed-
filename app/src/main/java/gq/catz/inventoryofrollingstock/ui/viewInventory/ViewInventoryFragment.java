package gq.catz.inventoryofrollingstock.ui.viewInventory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import gq.catz.inventoryofrollingstock.R;

public class ViewInventoryFragment extends Fragment {

//	private DashboardViewModel dashboardViewModel;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_viewinventory, container, false);
		return v;
		/*dashboardViewModel =
				ViewModelProviders.of(this).get(DashboardViewModel.class);
		View root = inflater.inflate(R.layout.fragment_viewinventory, container, false);
		final TextView textView = root.findViewById(R.id.text_dashboard);
		dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
			@Override
			public void onChanged(@Nullable String s) {
				textView.setText(s);
			}
		});
		return root;*/
	}
}