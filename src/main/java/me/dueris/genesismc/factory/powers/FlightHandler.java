package me.dueris.genesismc.factory.powers;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.factory.powers.Power.creative_flight;

public class FlightHandler extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "insideBlock"), PersistentDataType.BOOLEAN) != null && Boolean.TRUE.equals(p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "insideBlock"), PersistentDataType.BOOLEAN))) {
                if (p.getAllowFlight()) {
                    p.setFlying(true);
                }
            } else {
                if (p.getGameMode().equals(GameMode.CREATIVE)) {
                    p.setAllowFlight(true);
                } else {
                    if (creative_flight.contains(p) || OriginPlayer.isInPhantomForm(p)) {
                        p.setAllowFlight(true);
                        if(p.isFlying()){
                            p.setFlying(true);
                        }
                    } else {
                        if (p.getGameMode().equals(GameMode.CREATIVE)) {
                            p.setAllowFlight(true);
                        } else p.setAllowFlight(p.getGameMode().equals(GameMode.SPECTATOR));
                    }
                }
            }

            if (p.getEyeLocation().getBlock().isCollidable()) {
                p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "insideBlock"), PersistentDataType.BOOLEAN, true);
            } else {
                p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "insideBlock"), PersistentDataType.BOOLEAN, false);
            }
        }
    }
}
