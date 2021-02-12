package gq.catz.inventoryofrollingstock.ui.addEntry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import gq.catz.inventoryofrollingstock.R;

public class AddEntryFragment extends Fragment {

//	private HomeViewModel homeViewModel;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_addentry, container, false);
		return v;
	}
}