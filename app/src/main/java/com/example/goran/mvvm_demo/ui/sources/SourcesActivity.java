package com.example.goran.mvvm_demo.ui.sources;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.goran.mvvm_demo.R;
import com.example.goran.mvvm_demo.data.model.Source;
import com.example.goran.mvvm_demo.ui.BaseActivity;
import com.example.goran.mvvm_demo.ui.adapters.SourceAdapter;
import com.example.goran.mvvm_demo.ui.articles.ArticlesActivity;
import com.example.goran.mvvm_demo.util.Category;
import com.example.goran.mvvm_demo.util.Code;

public class SourcesActivity extends BaseActivity {

    private SourceAdapter adapter;
    private SourcesViewModel viewModel;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sources);

        progressBar = findViewById(R.id.progress_sources);

        initSpinner();

        initAdapter();

        initRecyclerView();

        viewModel = ViewModelProviders.of(this).get(SourcesViewModel.class);

        viewModel.getSourcesFromApi().observe(this, sources -> {
            hideProgressBar();
            adapter.submitList(sources);
        });

        viewModel.getErrorCode().observe(this, errorCode -> {
            if (errorCode != null && errorCode == Code.NETWORK_ERROR) {
                showNetworkError();
                hideProgressBar();
            }
        });
    }

    private void initSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                Category.getCategories());

        Spinner spinner = findViewById(R.id.spinner_sources);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String category = spinnerAdapter.getItem(i);
                viewModel.getSourcesByCategory(category).observe(SourcesActivity.this,
                        sources -> adapter.submitList(sources));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initAdapter() {
        adapter = new SourceAdapter();
        adapter.setListener(this::navigateToMainActivity);
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration divider = new DividerItemDecoration(this, layoutManager.getOrientation());

        RecyclerView recyclerView = findViewById(R.id.recycler_sources);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(divider);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void navigateToMainActivity(Source source) {
        Intent intent = new Intent(this, ArticlesActivity.class);
        intent.putExtra("source_id", source.getId());
        intent.putExtra("source_name", source.getName());
        startActivity(intent);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
}
