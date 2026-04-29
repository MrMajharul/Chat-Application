package model;

public class Model_Update_Avatar {

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public Model_Update_Avatar(int userID, String imageBase64) {
        this.userID = userID;
        this.imageBase64 = imageBase64;
    }

    public Model_Update_Avatar() {
    }

    private int userID;
    private String imageBase64;
}
