package com.lawerens.race.model;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class Rollback {

    private final @NotNull Player player;
    private final @Nullable ItemStack[] contents;
    private final @Nullable ItemStack helmet, chestplate, leggings, boots;
    private final double health;
    private final int hunger;
    private final boolean allowFly;
    private final @NotNull GameMode gameMode;
    private final @Nullable Location lastLocation;
    private final @NotNull Collection<PotionEffect> effects;

    public Rollback(@NotNull Player player) {
        this.player = player;
        this.contents = player.getInventory().getContents();
        this.helmet = player.getInventory().getHelmet();
        this.effects = player.getActivePotionEffects();
        this.chestplate = player.getInventory().getChestplate();
        this.leggings = player.getInventory().getLeggings();
        this.boots = player.getInventory().getBoots();
        this.health = player.getHealth();
        this.hunger = player.getFoodLevel();
        this.allowFly = player.getAllowFlight();
        this.gameMode = player.getGameMode();
        this.lastLocation = player.getLocation();
    }

    public void give(boolean sendLastLocation){
        if(sendLastLocation && lastLocation != null) player.teleport(lastLocation);
        player.setGameMode(gameMode);
        player.setHealth(health);
        player.setFoodLevel(hunger);
        player.setAllowFlight(allowFly);
        player.getInventory().setContents(contents);
        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        for (PotionEffect effect : effects) {
            player.addPotionEffect(effect);
        }
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
    }
}
