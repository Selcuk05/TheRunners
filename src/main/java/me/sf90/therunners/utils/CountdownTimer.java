package me.sf90.therunners.utils;

import me.sf90.therunners.TheRunners;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class CountdownTimer {

    private int countFrom;

    private final TheRunners instance;



    protected CountdownTimer(int countFrom, TheRunners instance) {
        this.countFrom = countFrom;
        this.instance = instance;
    }


    public abstract void count(int current);


    public final void start() {
        new BukkitRunnable() {

            @Override
            public void run() {
                count(countFrom);
                if (countFrom-- <= 0) cancel();
            }

        }.runTaskTimer(instance, 20L, 20L);
    }

}