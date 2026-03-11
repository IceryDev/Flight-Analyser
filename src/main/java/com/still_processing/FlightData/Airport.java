package com.still_processing.FlightData;

public class Airport {
    long id;
    String name;
    float latitude;
    float longitude;
    String country;
    String region;
    String municipality;
    String iataCode;

    Airport(){}

    Airport(long id, String name, float latitude, float longitude, String country, String region,  String municipality, String iataCode){
      this.id = id;
      this.name = name;
      this.latitude = latitude;
      this.longitude = longitude;
      this.country = country;
      this.region = region;
      this.municipality = municipality;
      this.iataCode = iataCode;
    }

}
