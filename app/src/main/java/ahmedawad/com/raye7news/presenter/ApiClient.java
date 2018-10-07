package ahmedawad.com.raye7news.presenter;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String baseUrl="https://newsapi.org/v2/";
    private static Retrofit retrofit;

    // make instance from class
    public static Retrofit getInstance(){
        if(retrofit==null){
            retrofit=new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }

}
