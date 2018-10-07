package ahmedawad.com.raye7news.presenter;

import ahmedawad.com.raye7news.model.NewsObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    // call endpoint everything from newsapi
    @GET("everything/")
    Call<NewsObject> getAllNewsData(@Query("q") String searchWord,@Query("sources") String sources,@Query("language") String language,@Query("apiKey") String apiKey);
}
