package gq.catz.inventoryofrollingstock.ui.Consists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import gq.catz.inventoryofrollingstock.R;

public class ConsistsFragment extends Fragment {

	// private NotificationsViewModel notificationsViewModel;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_quickstats, container, false);
	}
}