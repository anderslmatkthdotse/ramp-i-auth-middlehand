package ramp.auth.Rest.OutGoing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OkHttp {

    public Response sendGetOkHttp(String url) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + getJwt().getTokenValue())
                //.addHeader("Cookie", "JSESSIONID=E72EDD8BB5C8CA84D598E0D0DA7B106E.pcmasterrace; JSESSIONID=E72EDD8BB5C8CA84D598E0D0DA7B106E")
                .build();
        return client.newCall(request).execute();
    }

    public Response sendOkHttpMessageWithObject(String operation, String url, Object objectToSend) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        Gson gson = new GsonBuilder().setLenient().create();
        String json = gson.toJson(objectToSend);
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(url)
                .method(operation, body)
                .addHeader("Authorization", "Bearer " + getJwt().getTokenValue())
                .addHeader("Content-Type", "application/json")
                .build();
        return client.newCall(request).execute();
    }

    public Response sendOkHttpMessageBasedOnParameter(String operation,String url) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, " ");
        Request request = new Request.Builder()
                .url(url)
                .method(operation, body)
                .addHeader("Authorization", "Bearer " + getJwt().getTokenValue())
                .build();
        return client.newCall(request).execute();
    }

    private Jwt getJwt() {
        return (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
