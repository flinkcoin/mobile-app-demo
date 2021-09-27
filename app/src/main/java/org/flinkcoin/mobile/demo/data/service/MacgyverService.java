package org.flinkcoin.mobile.demo.data.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MacgyverService {

    String BASE_URL = "resource/macgyver/";

    @GET(BASE_URL + "pay-to/{accountId}/{amount}")
    Call<ResponseBody> payTo(@Path("accountId") String accountId, @Path("amount") String amount);

}
