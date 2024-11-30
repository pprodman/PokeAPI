package com.example.pokeapi.models;

public class Pokemon {
    private String name;
    private String height;
    private String weight;
    private String url;

    public String getName() {
        return name;
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String getUrl() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

