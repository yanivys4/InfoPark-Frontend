package com.example.infopark.RESTApi;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class is a singleton class that initiate a Retrofit instance and used
 * during the run of the app.
 */
public class RetrofitClient {

    private static Retrofit m_instance;

    public static Retrofit getInstance(){
        if(m_instance == null){
            m_instance= new Retrofit.Builder()
                    .baseUrl("http://Infoparkbackend-env.eba-avqkxmrt.us-east-2.elasticbeanstalk.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return m_instance;
    }
}

