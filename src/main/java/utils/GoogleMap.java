package utils;

import javafx.scene.control.ListView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class GoogleMap {

    public static List<PlaceInfo> fetchPlaceSuggestions(String query) {
        List<PlaceInfo> placeInfoList = new ArrayList<>();

        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String apiUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
                    + encodedQuery + "&key=" + ConfigReader.get("HAYTHEM_GOOGLE_MAPS");

            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray predictions = jsonResponse.getJSONArray("predictions");

            for (int i = 0; i < predictions.length(); i++) {
                String placeName = predictions.getJSONObject(i).getString("description");
                String placeId = predictions.getJSONObject(i).getString("place_id");
                placeInfoList.add(new PlaceInfo(placeName, placeId)); // Add to the list
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return placeInfoList;
    }

    public static  List<Double> fetchPlaceDetails(String placeId) {
        List<Double> coordinates = new ArrayList<>();
        try {
            String apiUrl = "https://maps.googleapis.com/maps/api/place/details/json?place_id="
                    + placeId + "&key=" + ConfigReader.get("HAYTHEM_GOOGLE_MAPS");

            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject result = jsonResponse.getJSONObject("result");
            JSONObject geometry = result.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");

            double latitude = location.getDouble("lat");
            double longitude = location.getDouble("lng");

            // Store or display the coordinates
            coordinates.add(latitude);
            coordinates.add(longitude);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return coordinates;
    }
}
