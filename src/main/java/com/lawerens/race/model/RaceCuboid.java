package com.lawerens.race.model;

import org.bukkit.Location;
import xyz.lawerens.utils.LawerensUtils;

public record RaceCuboid(Location firstPoint, Location secondPoint) {

    public boolean isInCuboid(Location location) {
        return LawerensUtils.isLocationInCuboid(location, firstPoint, secondPoint);
    }

}
