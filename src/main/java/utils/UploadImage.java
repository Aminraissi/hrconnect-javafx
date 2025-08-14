package utils;

import okhttp3.*;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class UploadImage {

    private static final String API_KEY = ConfigReader.get("HAYTHEM_IMGBB");
    private static final String UPLOAD_URL = "https://api.imgbb.com/1/upload";

    private String convertToBase64(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String uploadImage(File file) {
        String base64Image = convertToBase64(file);
        if (base64Image == null) {
            return null;
        }

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("key", API_KEY)
                .add("image", base64Image)
                .build();

        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);
                return jsonResponse.getJSONObject("data").getString("url"); // Return the Image URL
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // Return null if the upload fails
    }
}
