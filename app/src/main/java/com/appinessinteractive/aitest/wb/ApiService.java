package com.appinessinteractive.aitest.wb;

import com.appinessinteractive.aitest.wb.model.EnterpriseModelResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ApiService {

    @GET("api.json")
    Observable<List<EnterpriseModelResponse>> enterpriselist();

}
