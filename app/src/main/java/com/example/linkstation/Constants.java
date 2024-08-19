package com.example.linkstation;

import com.example.linkstation.station.Station;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Station st1 = new Station("dipakkumarkalwar", "Dipak Kumar Kalwar - WebDev Portfolio");
        stations.add(st1);
        Station st2 = new Station("dipakaffiliatelinks", "Amazon Links to all my Gears");
        stations.add(st2);
        Station st3 = new Station("dipakkcourses","Dipak Kumar Kalwar - Udemy Courses");
        stations.add(st3);
        Station st4 = new Station("dipakkaboutme","Dipak Kumar Kalwar - About Me");
        stations.add(st4);
        Station st5 = new Station("dipakkprojects","Dipak Kumar Kalwar - Projects");
        stations.add(st5);
        return stations;
    }

}
