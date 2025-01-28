package com.lawerens.race.model;

import es.pollitoyeye.vehicles.interfaces.Vehicle;
import lombok.Data;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Data
public class RaceTrack {

    //global data
    private final List<Location> points = new ArrayList<>();

    //local data
    private final List<Vehicle> vehicles = new ArrayList<>();

}
