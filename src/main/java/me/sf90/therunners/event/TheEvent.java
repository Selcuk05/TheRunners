package me.sf90.therunners.event;

import me.sf90.therunners.TheRunners;
import me.sf90.therunners.utils.CountdownTimer;
import me.sf90.therunners.utils.TextUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class TheEvent {

    private final ArrayList<UUID> players;
    private final TheRunners instance;

    private final Location spawnLocation;
    private final Location lobbyLocation;

    private boolean eventStarted;
    private boolean eventComplete;

    public TheEvent(TheRunners _instance, Location spawnLocation, Location lobbyLocation){
        players = new ArrayList<UUID>();
        instance = _instance;
        this.spawnLocation = spawnLocation;
        this.lobbyLocation = lobbyLocation;
        eventStarted = false;
        eventComplete = false;
    }

    public boolean isEventStarted(){
        return eventStarted;
    }

    public boolean isEventComplete() {
        return eventComplete;
    }

    public void startEvent(){
        instance.getServer().broadcastMessage(TextUtils.fc(instance.getMessagesConfig(), "messages.eventStarting"));

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable(){
            public void run(){
                new CountdownTimer(instance.getConfig().getInt("general.startCountdown"), instance) {

                    @Override
                    public void count(int current) {
                        if(current == 0){
                            String goMessage = TextUtils.fc(instance.getMessagesConfig(), "countdown.actionbarColorCode")
                                    + TextUtils.fc(instance.getMessagesConfig(), "countdown.go");
                            for(UUID i : players) {
                                Player curPlayer = Bukkit.getPlayer(i);
                                curPlayer.playSound(curPlayer.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f, 1f);
                                curPlayer.sendTitle(goMessage, "", 10, 30, 10);
                            }
                            eventStarted = true;
                            instance.getServer().broadcastMessage(TextUtils.fc(instance.getMessagesConfig(), "messages.eventStarted"));
                            return;
                        }

                        for(UUID i : players) {
                            Player curPlayer = Bukkit.getPlayer(i);
                            curPlayer.playSound(curPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                            curPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                                    TextUtils.fc(instance.getMessagesConfig(), "countdown.actionbarColorCode") + String.format("%d", current)
                            ));
                        }
                    }

                }.start();
            }
        }, instance.getConfig().getInt("general.eventJoinDelay") * 20L);
    }

    public void joinEvent(Player p){
        if(eventStarted) return;
        UUID playerUUID = p.getUniqueId();

        if(!players.contains(playerUUID)) {
            p.teleport(spawnLocation);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable(){
                public void run(){
                    players.add(p.getUniqueId());
                    p.sendMessage(TextUtils.fc(instance.getMessagesConfig(), "messages.joinedEvent"));
                }
            }, 2);
            return;
        }

        p.sendMessage(TextUtils.fc(instance.getMessagesConfig(), "messages.alreadyJoined"));
    }

    public void leaveEvent(Player p){
        if(eventStarted) return;
        UUID playerUUID = p.getUniqueId();

        if(players.contains(playerUUID)){
            players.remove(playerUUID);
            p.sendMessage(TextUtils.fc(instance.getMessagesConfig(), "messages.leftEvent"));
            return;
        }

        p.sendMessage(TextUtils.fc(instance.getMessagesConfig(), "messages.notJoined"));
    }

    public void endEvent(Player winner){
        eventComplete = true;
        Bukkit.broadcastMessage("\n" + TextUtils.fc(instance.getMessagesConfig(), "messages.winAnnouncement") + "\n" +
                String.format(TextUtils.fc(instance.getMessagesConfig(), "messages.winnerSubText"), winner.getName()) + "\n");
        for(Player p : instance.getServer().getOnlinePlayers()){
            p.sendTitle(TextUtils.fc(instance.getMessagesConfig(), "messages.winAnnouncement"),
                    String.format(TextUtils.fc(instance.getMessagesConfig(), "messages.winnerSubText"), winner.getName()), 10, 30, 10);
            if(players.contains(p.getUniqueId())){
                p.teleport(lobbyLocation);
                players.remove(p.getUniqueId());
            }
        }
        for(Object command : instance.getConfig().getList("rewardCommands")){
            String commandToExecute = (String) command;
            commandToExecute = String.format(commandToExecute, winner.getName());
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            Bukkit.dispatchCommand(console, commandToExecute);
        }
    }

    public boolean playerIsInEvent(Player p){
        return players.contains(p.getUniqueId());
    }
}
