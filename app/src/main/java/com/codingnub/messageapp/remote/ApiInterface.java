package com.codingnub.messageapp.remote;

import com.codingnub.messageapp.notification.MyResponse;
import com.codingnub.messageapp.notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAr7JNyZA:APA91bEiipZWrx_abyttUXxJBwOlxctZVVDlydRw1fFNgQ5w9W8LgW5DdtuWtPN7wNLj7jBWg2wXq2nLHFHFDYjwhoKpgYSYfRwmpIdPeK3QEc2uALGd4dXjhUXC0QRf9Q-9uif_6X6S"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender sender);

}
