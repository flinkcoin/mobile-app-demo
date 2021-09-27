package org.flinkcoin.mobile.demo.data.service;

import org.flinkcoin.mobile.demo.data.service.dto.WalletBlock;
import org.flinkcoin.mobile.demo.data.service.dto.WalletPaymentRequest;
import org.flinkcoin.mobile.demo.data.service.dto.WalletTransaction;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WalletService {

    String BASE_URL = "resource/wallet/";

    @GET(BASE_URL + "last-block/{accountId}")
    Observable<WalletBlock> lastBlock(@Path("accountId") String accountId);

    @GET(BASE_URL + "list-blocks/{accountId}")
    Observable<List<WalletBlock>> listBlocks(@Path("accountId") String accountId);

    @GET(BASE_URL + "block/{blockHash}")
    Observable<WalletBlock> getBlock(@Path("blockHash") String blockHash);

    @GET(BASE_URL + "list-unclaimed-blocks/{accountId}")
    Observable<List<WalletBlock>> listUnclaimedBlocks(@Path("accountId") String accountId);

    @Headers("Content-Type: application/json")
    @POST(BASE_URL + "transaction")
    Observable<ResponseBody> addTransaction(@Body WalletTransaction walletTransaction);

    @Headers("Content-Type: application/json")
    @POST(BASE_URL + "payment-request")
    Observable<ResponseBody> paymentRequest(@Body WalletPaymentRequest walletPaymentRequest);

    /***/

    @Deprecated
    @GET(BASE_URL + "last-block/{accountId}")
    Call<WalletBlock> lastBlockCall(@Path("accountId") String accountId);

    @Headers("Content-Type: application/json")
    @POST(BASE_URL + "transaction")
    Call<ResponseBody> createTransactionCall(@Body WalletTransaction walletTransaction);

}
