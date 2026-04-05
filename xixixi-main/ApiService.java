package com.moyan.network;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST("user/login")
    Call<BaseResponse> login(
            @Field("phone") String phone,
            @Field("code") String code
    );

    @FormUrlEncoded
    @POST("user/register")
    Call<BaseResponse> register(
            @Field("phone") String phone,
            @Field("nickname") String nickname
    );
}
