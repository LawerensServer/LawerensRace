package com.lawerens.race.model;

import com.lawerens.race.LawerensRace;
import lombok.Data;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.lawerens.race.utils.CommonsUtils.sendMessageWithPrefix;
import static xyz.lawerens.utils.LawerensUtils.*;
import static xyz.lawerens.utils.LawerensUtils.sendUnderline;

@Data
public class GameManager {

    private boolean enable = false;
    private final List<Player> players = new ArrayList<>();
    private int countdown = 90;
    private RaceState state = RaceState.WAITING;
    private int taskID = -1;

    public void scheduler(){
        countdown = 90;
        taskID = Bukkit.getScheduler().runTaskTimer(LawerensRace.get(), () -> {
            if(players.isEmpty() && countdown < 90){
                countdown = 90;
            }

            if(players.size() > 10){
                countdown = 15;
            }

            if (!players.isEmpty()) {
                if (countdown <= 0) {
                    start();
                    cancelTask();
                    return;
                }

                if (countdown % 30 == 0) {
                    for (Player player : players) {
                        sendMessageWithPrefix(player, "EVENTO", "&fEmpezando en &e" + countdown + " &fsegundos.");
                    }
                }
                countdown--;
            }
        }, 20L, 20L).getTaskId();
    }

    public void setEnable(boolean enable) {
        if(enable){
            scheduler();
            for (Player p : Bukkit.getOnlinePlayers()) {
                sendUnderline(p, "#00ff00");
                sendMessage(p, " ");
                sendCenteredMessage(p, "&f¡El Evento &aCarrera&f ha empezado!");
                p.sendMessage(
                        Component.text("             ¡HAS CLICK AQUÍ PARA UNIRTE!")
                                .color(TextColor.color(0x11f111))
                                .decorate(TextDecoration.BOLD)
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/evento"))
                                .hoverEvent(HoverEvent.showText(Component.text("Click para ir al evento.").color(TextColor.color(0xff8000))))
                );                sendMessage(p, " ");
                sendUnderline(p, "#00ff00");
            }
        }else{
            for (Player p : Bukkit.getOnlinePlayers()) {
                sendUnderline(p, '6');
                sendMessage(p, " ");
                sendCenteredMessage(p, "&6¡El Evento &eCarrera&6 ha sido deshabilitado!");
                sendMessage(p, " ");
                sendUnderline(p, '6');
            }
        }
        this.enable = enable;
    }

    public void setEnable(boolean enable, CommandSender sender) {
        if(enable){
            scheduler();
            for (Player p : Bukkit.getOnlinePlayers()) {
                sendUnderline(p, "#00ff00");
                sendMessage(p, " ");
                sendCenteredMessage(p, "&f¡El Evento &aCarrera&f ha empezado!");
                p.sendMessage(
                        Component.text("             ¡HAS CLICK AQUÍ PARA UNIRTE!")
                                .color(TextColor.color(0x11f111))
                                .decorate(TextDecoration.BOLD)
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/evento"))
                                .hoverEvent(HoverEvent.showText(Component.text("Click para ir al evento.").color(TextColor.color(0xff8000))))
                );
                sendMessage(p, " ");
                sendUnderline(p, "#00ff00");
            }
        }else{
            for (Player p : Bukkit.getOnlinePlayers()) {
                sendUnderline(p, '6');
                sendMessage(p, " ");
                sendCenteredMessage(p, "&6¡El Evento &eCarrera&6 ha sido deshabilitado");
                sendCenteredMessage(p, "&6por: &c"+sender.getName()+"&f!");
                sendMessage(p, " ");
                sendUnderline(p, '6');
            }
        }
        this.enable = enable;
    }

    public void start() {
        state = RaceState.INGAME;
        for (Player player : getPlayers()) {
            player.teleport(LawerensRace.get().getRaceInfo().getStartLocation());
            sendMessageWithPrefix(player, "EVENTO", "&f¡El evento de carreras ha comenzado!");
            player.playSound(player, Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1f, 1f);

            Audience.audience(player).showTitle(Title.title(
                    Component.text("¡A TODA MARCHA!")
                            .color(TextColor.fromHexString("#00ff00"))
                            .decorate(TextDecoration.BOLD),
                    Component.text("El evento ha comenzado")
                            .color(TextColor.color(0xffffff)),
                    Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO)
            ));
        }
    }

    public void cancelTask(){
        Bukkit.getScheduler().cancelTask(taskID);
        taskID = -1;
    }

    public void win(@NotNull Player player) {
        state = RaceState.FINISHING;
        for (Player p : getPlayers()) {
            sendMessageWithPrefix(p, "EVENTO", "&f¡&e"+player.getName()+"&f ha ganado el evento!");
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.5f);

            Audience.audience(p).showTitle(Title.title(
                    Component.text("¡EVENTO TERMINADO!")
                            .color(TextColor.fromHexString("#00ff00"))
                            .decorate(TextDecoration.BOLD),
                    Component.text("¡")
                            .color(TextColor.color(0xffffff))
                            .append(Component.text(player.getName())
                                    .color(TextColor.color(0xffff00)))
                            .append(Component.text(" ha ganado el evento!")
                                    .color(TextColor.color(0xffffff))),
                    Title.Times.times(Duration.ZERO, Duration.ofSeconds(5), Duration.ZERO)
            ));
        }
        finish();
        new BukkitRunnable() {
            int i = 9;
            @Override
            public void run() {
                if(i == 2) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crates key give "+player.getName()+" epica 1");
                if(i < 0) {
                    cancel();
                    return;
                }
                Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);

                FireworkMeta fireworkMeta = firework.getFireworkMeta();

                FireworkEffect effect = FireworkEffect.builder()
                        .withColor(Color.RED)
                        .withFade(Color.YELLOW)
                        .with(FireworkEffect.Type.BALL)
                        .trail(true)
                        .flicker(true)
                        .build();

                fireworkMeta.addEffect(effect);

                fireworkMeta.setPower(2);

                firework.setFireworkMeta(fireworkMeta);
                i--;
            }
        }.runTaskTimer(LawerensRace.get(), 0L, 20L);
    }

    public void finish() {
        for (Player player : players) {
            LawerensRace.get().getRollbacks().get(player.getUniqueId()).give(true);
            LawerensRace.get().getRollbacks().remove(player.getUniqueId());
            sendMessageWithPrefix(player, "EVENTO", "&f¡El evento ha finalizado! Nos vemos pronto...");
        }
        players.clear();
        state = RaceState.WAITING;
        enable = false;
        countdown = 90;

    }

    public void join(Player player) {
        players.add(player);

        Rollback rollback = new Rollback(player);
        LawerensRace.get().getRollbacks().put(player.getUniqueId(), rollback);

        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(LawerensRace.get().getRaceInfo().getLobbyLocation());
        player.clearActivePotionEffects();
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setFoodLevel(24);
        player.setFlying(false);
        player.setAllowFlight(false);

        for (Player p : players) {
            sendMessageWithPrefix(p, "EVENTO", "&e" + player.getName() + " &fse ha unido. &f(&e" + players.size() + "&f/&e30&f)");
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.8f);
        }
    }

    public void finish(@NotNull CommandSender sender) {
        finish();
        for (Player p : Bukkit.getOnlinePlayers()) {
            sendUnderline(p, "#cf0011");
            sendMessage(p, " ");
            sendCenteredMessage(p, "&fEl Evento &cParkour&f se detuvo forzadamente");
            sendCenteredMessage(p, "&fpor: &c"+sender.getName()+".");
            sendMessage(p, " ");
            sendUnderline(p, "#cf0011");
        }
    }
}
