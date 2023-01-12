package me.zerobugs.plotspickupaddon;

import com.google.common.eventbus.Subscribe;
import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.events.PlayerEnterPlotEvent;
import com.plotsquared.core.events.PlayerLeavePlotEvent;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class PlotsPickupAddon extends JavaPlugin implements Listener {


    private HashMap<UUID, Plot> plots;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("PlotSquared") == null) {
            getServer().getPluginManager().disablePlugin(this);
        }
        this.plots = new HashMap<>();
        new PlotAPI().registerListener(this);
        getServer().getPluginManager().registerEvents(this, this);
    }


    @Subscribe
    public void onPlayerEnterPlot(final PlayerEnterPlotEvent event) {
        plots.put(event.getPlotPlayer().getUUID(), event.getPlot());
    }

    @Subscribe
    public void onPlayerLeavePlot(final PlayerLeavePlotEvent event) {
        plots.remove(event.getPlotPlayer().getUUID());
    }


    @EventHandler
    private void onPickup(final PlayerPickupItemEvent event){

        final Player player = event.getPlayer();

        if (!plots.containsKey(player.getUniqueId())){
            return;
        }

        final Plot plot = plots.get(player.getUniqueId());
        final Set<UUID> set = new HashSet<UUID>(){
            {
                addAll(plot.getMembers());
                addAll(plot.getOwners());
                addAll(plot.getTrusted());
            }
        };

        if (!set.contains(player.getUniqueId())){
            event.setCancelled(true);
        }

    }

    @EventHandler
    private void onQuit(final PlayerQuitEvent event){
        plots.remove(event.getPlayer().getUniqueId());
    }





}
