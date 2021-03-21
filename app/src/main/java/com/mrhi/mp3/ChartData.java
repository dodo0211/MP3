package com.mrhi.mp3;

public class ChartData {
    private String rankNum;
    private String title;
    private String name;
    private String imageUrl;

    public ChartData() {
        this.rankNum = rankNum;
        this.title = title;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public void setRankNum(String rankNum) {this.rankNum = rankNum;}
    public void setTitle(String title) {this.title = title;}
    public void setName(String name) {this.name = name;}
    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}

    public String getRankNum() {return rankNum;}
    public String getTitle() {return title;}
    public String getName() {return name;}
    public String getImageUrl() {return imageUrl;}

}
