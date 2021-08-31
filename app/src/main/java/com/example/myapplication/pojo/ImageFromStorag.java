package com.example.myapplication.pojo;

public class ImageFromStorag {

    private Image image;
    private String imageTimeInMillis;
    private Boolean Checked;

    public ImageFromStorag(Image image, String imageTimeInMillis, Boolean checked) {
        this.image = image;
        this.imageTimeInMillis = imageTimeInMillis;
        Checked = checked;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getImageTimeInMillis() {
        return imageTimeInMillis;
    }

    public void setImageTimeInMillis(String imageTimeInMillis) {
        this.imageTimeInMillis = imageTimeInMillis;
    }

    public Boolean getChecked() {
        return Checked;
    }

    public void setChecked(Boolean checked) {
        Checked = checked;
    }

    @Override
    public String toString() {
        return "ImageFromStorag{" +
                "image=" + image +
                ", imageTimeInMillis='" + imageTimeInMillis + '\'' +
                ", Checked=" + Checked +
                '}';
    }
}
