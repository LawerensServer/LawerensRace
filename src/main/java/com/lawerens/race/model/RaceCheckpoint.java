package com.lawerens.race.model;

import lombok.Data;
import org.bukkit.Location;
import xyz.lawerens.utils.LawerensUtils;

@Data
public class RaceCheckpoint {

    private Location firstPoint, secondPoint;

    public boolean isInside(Location location) {
        return LawerensUtils.isLocationInCuboid(location, firstPoint, secondPoint);
    }
}
