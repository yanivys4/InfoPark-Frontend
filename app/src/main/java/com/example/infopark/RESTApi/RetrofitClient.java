package com.example.infopark.RESTApi;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit m_instance;

    public static Retrofit getInstance(){
        if(m_instance == null){
            m_instance= new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:3000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return m_instance;
    }
}
