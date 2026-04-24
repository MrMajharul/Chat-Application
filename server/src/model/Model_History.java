package model;

public class Model_History {

    public int getFromUserID() {
        return fromUserID;
    }

    public void setFromUserID(int fromUserID) {
        this.fromUserID = fromUserID;
    }

    public int getToUserID() {
        return toUserID;
    }

    public void setToUserID(int toUserID) {
        this.toUserID = toUserID;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Model_History(int fromUserID, int toUserID, int limit) {
        this.fromUserID = fromUserID;
        this.toUserID = toUserID;
        this.limit = limit;
    }

    public Model_History() {
    }

    private int fromUserID;
    private int toUserID;
    private int limit;
}
