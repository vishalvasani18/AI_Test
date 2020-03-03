package com.appinessinteractive.aitest.wb;

public class ApiUtils {

    private static ApiService service;
    public static final String BASE_URL = "https://www.enterprisesmail.com/json/";

    public static ApiService getAPIService() {
        if (service == null) {
            service = RetrofitManager.getClient().create(ApiService.class);
        }

        return service;
    }
}
