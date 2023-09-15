package me.sf90.therunners.listener;

import me.sf90.therunners.TheRunners;
import me.sf90.therunners.event.TheEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerFinishRaceListener implements Listener {

    private final TheRunners instance;

    public PlayerFinishRaceListener(TheRunners instance){
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerFinishRace(PlayerMoveEvent e){
        TheEvent currentEvent = instance.getCurrentEvent();
        if(currentEvent == null) return;
        if(!currentEvent.playerIsInEvent(e.getPlayer())) return;

        Player p = e.getPlayer();
        Location eventWorld = instance.getConfig().getLocation("location.spawn");
        if(!p.getWorld().getName().equals(eventWorld.getWorld().getName())) return;
        if(currentEvent.isEventComplete()) return;

        if(isInRegion(p.getLocation(), instance.getConfig().getLocation("location.pointA"), instance.getConfig().getLocation("location.pointB"))){
            currentEvent.endEvent(p);
        }
    }

    private boolean isInRegion(Location source, Location pointA, Location pointB) {
        return source.getX() >= Math.min(pointA.getX(), pointB.getX()) &&
                source.getY() >= Math.min(pointA.getY(), pointB.getY()) &&
                source.getZ() >= Math.min(pointA.getZ(), pointB.getZ()) &&
                source.getX() <= Math.max(pointA.getX(), pointB.getX()) &&
                source.getY() <= Math.max(pointA.getY(), pointB.getY()) &&
                source.getZ() <= Math.max(pointA.getZ(), pointB.getZ());
    }
}
