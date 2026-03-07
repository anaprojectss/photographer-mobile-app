package com.example.photographyapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirebaseRest {

    // Your DB base URL (no / at the end is also ok)
    private static final String BASE_URL =
            "https://photographer-app-c89b4-default-rtdb.europe-west1.firebasedatabase.app";

    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public interface ResultCallback {
        void onSuccess(String responseBody);
        void onError(String message);
    }

    // POST /users.json  -> Firebase generates an auto ID
    public static void createUser(String fullName, String email, String role, String password, ResultCallback cb) {
        String url = BASE_URL + "/users.json";

        JSONObject bodyJson = new JSONObject();
        try {
            bodyJson.put("fullName", fullName);
            bodyJson.put("email", email);
            bodyJson.put("role", role);

            // ⚠️ Not secure for real apps. OK for a simple faculty demo.
            bodyJson.put("password", password);

        } catch (JSONException e) {
            cb.onError("JSON error: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(bodyJson.toString(), JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                cb.onError("Network error: " + e.getMessage());
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    cb.onError("HTTP " + response.code() + ": " + resp);
                    return;
                }
                cb.onSuccess(resp); // looks like: {"name":"-NxyzAutoId"}
            }
        });
    }

    public interface LoginCallback {
        void onSuccess(String userId, String role);
        void onInvalidCredentials();
        void onError(String message);
    }

    public static void login(String email, String password, LoginCallback cb) {
        String url = BASE_URL + "/users.json";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                cb.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    cb.onError("HTTP " + response.code() + ": " + resp);
                    return;
                }

                try {
                    // Ako nema nijednog usera, Firebase vrati "null"
                    if (resp == null || resp.equals("null") || resp.trim().isEmpty()) {
                        cb.onInvalidCredentials();
                        return;
                    }

                    JSONObject root = new JSONObject(resp);

                    String emailNorm = email.trim().toLowerCase();
                    String passNorm = password.trim();

                    // root = { "autoId1": {...}, "autoId2": {...} }
                    java.util.Iterator<String> keys = root.keys();
                    while (keys.hasNext()) {
                        String userId = keys.next();
                        JSONObject user = root.optJSONObject(userId);
                        if (user == null) continue;

                        String dbEmail = user.optString("email", "").trim().toLowerCase();
                        String dbPass  = user.optString("password", "").trim();

                        if (emailNorm.equals(dbEmail) && passNorm.equals(dbPass)) {
                            String role = user.optString("role", "client");
                            cb.onSuccess(userId, role);
                            return;
                        }
                    }

                    cb.onInvalidCredentials();

                } catch (Exception e) {
                    cb.onError("Parse error: " + e.getMessage());
                }
            }
        });
    }

    public interface UserCallback {
        void onSuccess(String fullName, String studioName, String bio, String avatarUrl);
        void onError(String message);
    }

    public static void getUser(String userId, UserCallback cb) {
        String url = BASE_URL + "/users/" + userId + ".json";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                cb.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body() != null ? response.body().string() : "";

                if (!response.isSuccessful()) {
                    cb.onError("HTTP " + response.code());
                    return;
                }

                try {
                    JSONObject user = new JSONObject(resp);

                    String fullName = user.optString("fullName", "Photographer");
                    String studio = user.optString("studioName", "Studio");
                    String bio = user.optString("bio", "");
                    String avatarUrl = user.optString("avatarUrl", "");

                    cb.onSuccess(fullName, studio, bio, avatarUrl);

                } catch (Exception e) {
                    cb.onError("Parse error: " + e.getMessage());
                }
            }
        });
    }

    public interface UserBasicCallback {
        void onSuccess(String fullName, String email);
        void onError(String message);
    }

    public static void getUserBasic(String userId, UserBasicCallback cb) {
        String url = BASE_URL + "/users/" + userId + ".json";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                cb.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body() != null ? response.body().string() : "";

                if (!response.isSuccessful()) {
                    cb.onError("HTTP " + response.code());
                    return;
                }

                try {
                    JSONObject user = new JSONObject(resp);

                    String fullName = user.optString("fullName", "Client");
                    String email = user.optString("email", "");

                    cb.onSuccess(fullName, email);

                } catch (Exception e) {
                    cb.onError("Parse error: " + e.getMessage());
                }
            }
        });
    }

    public static void updateProfile(String userId, String fullName, String studioName, String avatarUrl, ResultCallback cb) {
        String url = BASE_URL + "/users/" + userId + ".json";

        JSONObject bodyJson = new JSONObject();
        try {
            bodyJson.put("fullName", fullName);
            bodyJson.put("studioName", studioName);
            bodyJson.put("avatarUrl", avatarUrl);
        } catch (Exception e) {
            cb.onError("JSON error: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(bodyJson.toString(), JSON);

        Request request = new Request.Builder()
                .url(url)
                .patch(body)   // PATCH = update fields
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                cb.onError("Network error: " + e.getMessage());
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    cb.onError("HTTP " + response.code() + ": " + resp);
                    return;
                }
                cb.onSuccess(resp);
            }
        });
    }

    public static void createPhoto(String photographerId, String url, String title, ResultCallback cb) {
        String endpoint = BASE_URL + "/photos.json";

        JSONObject bodyJson = new JSONObject();
        try {
            bodyJson.put("photographerId", photographerId);
            bodyJson.put("url", url);
            bodyJson.put("title", title);
            bodyJson.put("createdAt", System.currentTimeMillis());
        } catch (JSONException e) {
            cb.onError("JSON error: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(bodyJson.toString(), JSON);

        Request request = new Request.Builder()
                .url(endpoint)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                cb.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    cb.onError("HTTP " + response.code() + ": " + resp);
                    return;
                }
                cb.onSuccess(resp); // {"name":"-NphotoAutoId"}
            }
        });
    }

    public static void getPhotos(String photographerId, PhotoListCallback cb) {
        HttpUrl url = HttpUrl.parse(BASE_URL + "/photos.json")
                .newBuilder()
                // Firebase expects quotes in these values:
                .addQueryParameter("orderBy", "\"photographerId\"")
                .addQueryParameter("equalTo", "\"" + photographerId + "\"")
                .build();

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                cb.onError("Network error: " + e.getMessage());
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    cb.onError("HTTP " + response.code() + ": " + resp);
                    return;
                }

                try {
                    // Firebase returns "null" if no results
                    if (resp == null || resp.equals("null")) {
                        cb.onSuccess(new ArrayList<>());
                        return;
                    }

                    JSONObject data = new JSONObject(resp);
                    ArrayList<Photo> photos = new ArrayList<>();

                    Iterator<String> keys = data.keys();
                    while (keys.hasNext()) {
                        String id = keys.next();
                        JSONObject p = data.optJSONObject(id);
                        if (p == null) continue;

                        photos.add(new Photo(
                                id,
                                p.optString("title", ""),
                                p.optString("url", ""),
                                p.optString("photographerId", "")
                        ));
                    }

                    cb.onSuccess(photos);
                } catch (Exception e) {
                    cb.onError("Parse error: " + e.getMessage());
                }
            }
        });
    }

    public interface PhotoListCallback {
        void onSuccess(List<Photo> photos);
        void onError(String message);
    }

    public static void deletePhoto(String photoId, ResultCallback cb) {
        String url = BASE_URL + "/photos/" + photoId + ".json";

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                cb.onError("Network error: " + e.getMessage());
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    cb.onError("HTTP " + response.code() + ": " + resp);
                    return;
                }
                cb.onSuccess(resp);
            }
        });
    }

    public static void updatePhotoTitle(String photoId, String newTitle, ResultCallback cb) {
        String url = BASE_URL + "/photos/" + photoId + ".json";

        JSONObject bodyJson = new JSONObject();
        try {
            bodyJson.put("title", newTitle);
        } catch (Exception e) {
            cb.onError("JSON error: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(bodyJson.toString(), JSON);

        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                cb.onError("Network error: " + e.getMessage());
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    cb.onError("HTTP " + response.code() + ": " + resp);
                    return;
                }
                cb.onSuccess(resp);
            }
        });
    }

    public interface PhotographersCallback {
        void onSuccess(List<Photographer> photographers);
        void onError(String message);
    }

    public static void getPhotographers(PhotographersCallback cb) {
        String url = BASE_URL + "/users.json";

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                cb.onError("Network error: " + e.getMessage());
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    cb.onError("HTTP " + response.code() + ": " + resp);
                    return;
                }

                try {
                    if (resp == null || resp.equals("null")) {
                        cb.onSuccess(new ArrayList<>());
                        return;
                    }

                    JSONObject data = new JSONObject(resp);
                    ArrayList<Photographer> list = new ArrayList<>();

                    Iterator<String> keys = data.keys();
                    while (keys.hasNext()) {
                        String id = keys.next();
                        JSONObject u = data.optJSONObject(id);
                        if (u == null) continue;

                        String role = u.optString("role", "");
                        if (!"admin".equals(role)) continue;

                        String fullName = u.optString("fullName", "");
                        String studioName = u.optString("studioName", "");
                        String avatarUrl = u.optString("avatarUrl", "");

                        list.add(new Photographer(id, fullName, studioName, avatarUrl));
                    }

                    cb.onSuccess(list);

                } catch (Exception e) {
                    cb.onError("Parse error: " + e.getMessage());
                }
            }
        });
    }

    public static void createBooking(String clientId,
                                     String photographerId,
                                     String date,
                                     String location,
                                     String shootType,
                                     String hours,
                                     ResultCallback cb) {

        String endpoint = BASE_URL + "/bookings.json";

        JSONObject bodyJson = new JSONObject();
        try {
            bodyJson.put("clientId", clientId);
            bodyJson.put("photographerId", photographerId);
            bodyJson.put("date", date);
            bodyJson.put("location", location);
            bodyJson.put("shootType", shootType);
            bodyJson.put("hours", hours);
            bodyJson.put("status", "pending");
            bodyJson.put("createdAt", System.currentTimeMillis());
        } catch (JSONException e) {
            cb.onError("JSON error: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(bodyJson.toString(), JSON);

        Request request = new Request.Builder()
                .url(endpoint)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                cb.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    cb.onError("HTTP " + response.code() + ": " + resp);
                    return;
                }
                cb.onSuccess(resp);
            }
        });
    }
}