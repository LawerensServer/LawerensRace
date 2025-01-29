package com.lawerens.race.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class RaceInfo {

    private @Nullable Location startLocation, lobbyLocation;
    private @NotNull List<Location> vehiclesPositions;
    private List<RaceCuboid> points;
    private @Nullable RaceCuboid finishCuboid;

}
