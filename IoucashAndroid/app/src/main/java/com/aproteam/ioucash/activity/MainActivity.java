package com.aproteam.ioucash.activity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.aproteam.ioucash.App;
import com.aproteam.ioucash.R;
import com.aproteam.ioucash.adapter.TransactionsAdapter;
import com.aproteam.ioucash.databinding.ActivityMainBinding;
import com.aproteam.ioucash.model.Transaction;
import com.aproteam.ioucash.viewmodel.MainViewModel;

public class MainActivity extends BaseActivity implements MainViewModel.MainModelCallback {

	ActivityMainBinding binding;
	TransactionsAdapter transactionsAdapter;

	@Override
	public void createUI() {
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
		MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
		mainViewModel.setActivity(this);
		mainViewModel.setMainModelCallback(this);
		binding.setViewModel(mainViewModel);
		binding.setLifecycleOwner(this);

		transactionsAdapter = new TransactionsAdapter(this);
		binding.transactionList.setAdapter(transactionsAdapter);
		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
			@Override
			public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
				return false;
			}

			@Override
			public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
				Transaction transaction = transactionsAdapter.getItem(viewHolder.getBindingAdapterPosition());
				mainViewModel.removeTransaction(transaction);
			}
		});
		itemTouchHelper.attachToRecyclerView(binding.transactionList);

		mainViewModel.getTransations().observe(this, userArrayList -> {
			if (userArrayList == null)
				App.toast(R.string.errorUnknown);
			else
				transactionsAdapter.updateData(userArrayList);
		});
		mainViewModel.onRefresh();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.settings)
			startActivity(new Intent(this, SettingsActivity.class));
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean shouldShowBackButton() {
		return false;
	}

	@Override
	public void onLogout() {
		getSessionManager().logout();
		finish();
		startActivity(new Intent(this, LoginActivity.class));
	}

	@Override
	public void onAddTransaction() {

	}

}