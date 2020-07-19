package tech.tookan.tookanlearning.network;

import retrofit2.Call;
import retrofit2.http.GET;
import tech.tookan.tookanlearning.objects.User;

public interface RetroDataService {
    @GET("/users")
    Call<User[]> getUsersData();
}
