package utils;


public class PlaceInfo {
    private String placeName;
    private String placeId;

    public PlaceInfo(String placeName, String placeId) {
        this.placeName = placeName;
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    @Override
    public String toString() {
        return placeName; // This is useful for displaying the place name in a ListView
    }
}