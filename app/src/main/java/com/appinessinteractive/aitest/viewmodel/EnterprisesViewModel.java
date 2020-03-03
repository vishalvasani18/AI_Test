package com.appinessinteractive.aitest.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.appinessinteractive.aitest.wb.model.EnterpriseModelResponse;

import java.util.List;

public class EnterprisesViewModel extends ViewModel {

    private MutableLiveData<List<EnterpriseModelResponse>> enterpriselist;

    public void init() {

        if (enterpriselist != null) {
            return;

        }
    }

}
