package com.lawerens.race.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import xyz.lawerens.utils.DefaultFontInfo;
import xyz.lawerens.utils.LawerensUtils;

import java.awt.*;

import static xyz.lawerens.Lawerens.COLOR;
import static xyz.lawerens.utils.LawerensUtils.sendMessage;

public class CommonsUtils {

    public static void writeLocation(ConfigurationSection config, String path, @Nullable  Location location){
        if(location == null) return;

        config.set(path+".world", location.getWorld().getName());
        config.set(path+".x", location.getX());
        config.set(path+".y", location.getY());
        config.set(path+".z", location.getZ());
        config.set(path+".yaw", location.getPitch());
        config.set(path+".pitch", location.getYaw());
    }

    public static Location stringToLocation(String str){
        if(str.split(";").length != 6) return null;
        double x = Double.parseDouble(str.split(";")[0]);
        double y = Double.parseDouble(str.split(";")[1]);
        double z = Double.parseDouble(str.split(";")[2]);
        float yaw = (float) Double.parseDouble(str.split(";")[3]);
        float pitch = (float) Double.parseDouble(str.split(";")[4]);
        World world = Bukkit.getWorld(str.split(";")[5]);

        return new Location(world, x, y, z, yaw, pitch);
    }

    @Nullable
    public static Location readLocation(ConfigurationSection config, String path){
        if(config.getString(path+".world") == null) return null;

        return new Location(
                Bukkit.getWorld(config.getString(path+".world")),
                config.getDouble(path+".x"),
                config.getDouble(path+".y"),
                config.getDouble(path+".z"),
                (float) config.getDouble(path+".yaw"),
                (float) config.getDouble(path+".pitch")
        );
    }

    public static void sendMessageWithPrefix(CommandSender sender, String prefix, String message){
        sendMessage(sender, COLOR+prefix+" &8» &f"+message);
    }

}
