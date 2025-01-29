package com.lawerens.race.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import static com.lawerens.race.LawerensRace.checkPlayer;

public class PreGameListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if(checkPlayer(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        if(checkPlayer(e.getPlayer())) {
            e.setCancelled(true);
            e.setBuild(false);
        }
    }

    @EventHandler
    public void onAttack(EntityDamageEvent e){
        if(!(e.getEntity() instanceof Player)) return;
        if(checkPlayer((Player) e.getEntity())) e.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        if(checkPlayer(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e){
        if(e.getCause() != PlayerTeleportEvent.TeleportCause.UNKNOWN) return;
        if(checkPlayer(e.getPlayer())) {
            e.setCancelled(true);
            e.setTo(e.getFrom());
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e){
        if(e.getPlayer().hasPermission("evento.bypass-command")) return;
        if(checkPlayer(e.getPlayer())) e.setCancelled(true);
    }
}
