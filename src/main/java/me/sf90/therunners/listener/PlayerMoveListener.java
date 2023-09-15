package me.sf90.therunners.listener;

import me.sf90.therunners.TheRunners;
import me.sf90.therunners.event.TheEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private final TheRunners instance;

    public PlayerMoveListener(TheRunners instance){
        this.instance = instance;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        TheEvent currentEvent = instance.getCurrentEvent();
        if(currentEvent == null) return;
        if(currentEvent.playerIsInEvent(e.getPlayer()) && !currentEvent.isEventStarted()){
            e.setCancelled(true);
        }
    }
}
