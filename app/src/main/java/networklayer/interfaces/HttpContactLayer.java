package networklayer.interfaces;

import com.twitter.sdk.android.core.models.User;

import helpers.UserDetails;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HttpContactLayer {
    @POST("User/GetUserDetails/")
    @FormUrlEncoded
    Call<UserDetails> getUserDetails( @Field("Nickname") String nickname );
}
