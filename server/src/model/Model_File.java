package model;

public class Model_File {

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getBlurHash() {
        return blurHash;
    }

    public void setBlurHash(String blurHash) {
        this.blurHash = blurHash;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Model_File(int fileID, String fileExtension) {
        this.fileID = fileID;
        this.fileExtension = fileExtension;
    }

    public Model_File(int fileID, String fileExtension, String blurHash, Integer width, Integer height, Long fileSize) {
        this.fileID = fileID;
        this.fileExtension = fileExtension;
        this.blurHash = blurHash;
        this.width = width;
        this.height = height;
        this.fileSize = fileSize;
    }

    public Model_File() {
    }

    private int fileID;
    private String fileExtension;
    private String blurHash;
    private Integer width;
    private Integer height;
    private Long fileSize;
}
