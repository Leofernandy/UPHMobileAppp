package com.example.uphmobileappp.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    @GET("api/provinces.json")
    Call<ApiResponse> getProvinsi();

    @GET("api/regencies/{provinceCode}.json")
    Call<ApiResponseKabupaten> getKabupaten(@Path("provinceCode") String provinceCode);
}
