package com.lawerens.race.model;

import es.pollitoyeye.vehicles.interfaces.Vehicle;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class RaceInfo {

    //global data
    private final List<Location> vehiclesPoints = new ArrayList<>();
    private final List<RaceCheckpoint> raceCheckpoints = new ArrayList<>();
    private final Location startPosition, lobbyPosition;

    //local data
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final Map<Player, Vehicle> designedVehicles = new HashMap<>();


}
