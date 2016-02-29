package com.shaad.testtask;

public enum City {
    NOVOSIBIRSK("NOVOSIBIRSK"), OMSK("OMSK"), KEMEROVO("KEMEROVO"), NOVOKUZNETSK("NOVOKUZNETSK"), TOMSK("TOMSK");

    private String cityName;

    public String getCityName() {
        return cityName;
    }

    City(String cityName) {
        this.cityName = cityName;
    }
}
