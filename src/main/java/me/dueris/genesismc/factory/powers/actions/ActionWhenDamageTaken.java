package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionWhenDamageTaken extends CraftPower implements Listener {
    @Override
    public void run() {

    }

    @EventHandler
    public void d(EntityDamageEvent e){
        Entity actor = e.getEntity();
        if(!(actor instanceof Player)) return;
        for (OriginContainer origin : OriginPlayer.getOrigin((Player) actor).values()) {
            PowerContainer power = origin.getPowerFileFromType(getPowerFile());
            if (power == null) continue;

            if(!getPowerArray().contains(actor)) return;
            setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
            ActionTypes.EntityActionType(actor, power.getBiEntityAction());
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(!getPowerArray().contains(actor)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                }
            }.runTaskLater(GenesisMC.getPlugin(), 2L);
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:action_when_damage_taken";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_when_damage_taken;
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }
}
