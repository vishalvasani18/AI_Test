package com.appinessinteractive.aitest.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appinessinteractive.aitest.R;
import com.appinessinteractive.aitest.viewmodel.EnterprisesViewModel;
import com.appinessinteractive.aitest.wb.ApiService;
import com.appinessinteractive.aitest.wb.ApiUtils;
import com.appinessinteractive.aitest.wb.model.EnterpriseModelResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class EnterprisesListActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView rvEnterprisesList;
    EnterprisesListAdapter enterprisesListAdapter;
    EnterprisesViewModel enterprisesViewModel;
    RecyclerView.LayoutManager layoutManager;
    List<EnterpriseModelResponse> enterprisedatalist;
    ApiService mAPIService;
    RelativeLayout customProgress;
    EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterprises_list);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        enterprisedatalist = new ArrayList<>();
        mAPIService = ApiUtils.getAPIService();
        rvEnterprisesList = (RecyclerView) findViewById(R.id.rvEnterprisesList);
        customProgress = (RelativeLayout) findViewById(R.id.customProgress);
        etSearch=(EditText)findViewById(R.id.etSearch);

        enterprisesViewModel = ViewModelProviders.of(this).get(EnterprisesViewModel.class);
        enterprisesViewModel.init();

        if (isInternetCheck()) {
            getenterpriseList();
        } else {
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();

                alertDialog.setTitle("Appiness Interactive");
                alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                alertDialog.show();
            } catch (Exception e) {
                Log.d("alert", "Show Dialog: " + e.getMessage());
            }
        }

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

    }

    private void filter(String text) {
        ArrayList<EnterpriseModelResponse> filteredList = new ArrayList<>();

        for (EnterpriseModelResponse item : enterprisedatalist) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        enterprisesListAdapter.filterList(filteredList);
    }


    public class EnterprisesListAdapter extends RecyclerView.Adapter<EnterprisesListAdapter.MyViewHolder>  {
        Context context;
        List<EnterpriseModelResponse> enterpriselist;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tvEnterpriseTitle, tvNoofBackers, tvBy;


            public MyViewHolder(View view) {
                super(view);

                tvEnterpriseTitle = (TextView) view.findViewById(R.id.tvEnterpriseTitle);
                tvNoofBackers = (TextView) view.findViewById(R.id.tvNoofBackers);
                tvBy = (TextView) view.findViewById(R.id.tvBy);
            }
        }

        public EnterprisesListAdapter(List<EnterpriseModelResponse> enterpriselist, Context context) {
            this.enterpriselist = enterpriselist;
            this.context = context;
        }

        @Override
        public EnterprisesListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_enterprise_list, parent, false);

            return new EnterprisesListAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(EnterprisesListAdapter.MyViewHolder holder, final int position) {
            final EnterpriseModelResponse model = enterpriselist.get(position);

            holder.tvEnterpriseTitle.setText(model.getTitle());
            holder.tvNoofBackers.setText(model.getNumBackers());
            holder.tvBy.setText(model.getBy());
        }

        @Override
        public int getItemCount() {
            return enterpriselist.size();
        }

        public void filterList(ArrayList<EnterpriseModelResponse> filteredList) {
            enterpriselist = filteredList;
            notifyDataSetChanged();
        }
    }

    private void getenterpriseList() {
        customProgress.setVisibility(View.VISIBLE);

        mAPIService.enterpriselist().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(this::getEnterpriseListResponse, this::handleErrorQuotation);

    }

    private void getEnterpriseListResponse(List<EnterpriseModelResponse> enterpriseModelResponses) {
        customProgress.setVisibility(View.GONE);
        enterprisedatalist.clear();
        enterprisedatalist.addAll(enterpriseModelResponses);
        enterprisesListAdapter = new EnterprisesListAdapter(enterprisedatalist, EnterprisesListActivity.this);
        layoutManager = new LinearLayoutManager(this);
        rvEnterprisesList.setLayoutManager(layoutManager);
        rvEnterprisesList.setAdapter(enterprisesListAdapter);
        enterprisesListAdapter.notifyDataSetChanged();

    }

    private void handleErrorQuotation(Throwable throwable) {
        customProgress.setVisibility(View.GONE);
        Log.d("Error", throwable.getMessage());
        Toast.makeText(EnterprisesListActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    public boolean isInternetCheck() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            return false;
        }
        return true;
    }

}
