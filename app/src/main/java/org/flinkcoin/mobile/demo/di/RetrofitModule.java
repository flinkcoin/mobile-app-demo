package org.flinkcoin.mobile.demo.di;

import org.flinkcoin.mobile.demo.BuildConfig;
import org.flinkcoin.mobile.demo.data.service.MacgyverService;
import org.flinkcoin.mobile.demo.data.service.WalletService;
import org.flinkcoin.mobile.demo.interceptor.HttpInterceptor;
import org.flinkcoin.mobile.demo.util.MoshiHelper;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class RetrofitModule {

    @Provides
    WalletService provideWalletService(Retrofit retrofit) {
        return retrofit.create(WalletService.class);
    }

    @Provides
    MacgyverService provideMacgyverService(Retrofit retrofit) {
        return retrofit.create(MacgyverService.class);
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(new HttpUrl.Builder()
                        .scheme("http")
                        .host(BuildConfig.WALLET_PROXY_HOST)
                        .port(BuildConfig.WALLET_PROXY_PORT)
                        .build())
                .addConverterFactory(MoshiConverterFactory.create(MoshiHelper.getMoshi()))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .callTimeout(0, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new HttpInterceptor())
                .build();
    }

}
