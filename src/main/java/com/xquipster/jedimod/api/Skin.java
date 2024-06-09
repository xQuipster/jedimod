package com.xquipster.jedimod.api;

public class Skin {
    private final String name;
    private String skin = "";
    private String cape = "";
    public Skin(String name){
        this.name = name;
    }
    public Skin(String name, String skin){
        this.name = name;
        this.skin = skin;
    }
    public Skin(String name, String skin, String cape){
        this.name = name;
        this.skin = skin;
        this.cape = cape;
    }

    public String getName() {
        return name;
    }

    public String getCape() {
        return cape;
    }

    public String getSkin() {
        return skin;
    }

    public void setCape(String cape) {
        this.cape = cape;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }
}
