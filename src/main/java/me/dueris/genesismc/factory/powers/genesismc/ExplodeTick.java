package me.dueris.genesismc.factory.powers.genesismc;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ExplodeTick extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool){
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }

    

    private final HashMap<UUID, Long> cooldown;

    public ExplodeTick() {
        this.cooldown = new HashMap<>();
    }


    @EventHandler
    public void onShiftCreep(PlayerToggleSneakEvent e) {
        for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
            Player p = e.getPlayer();
            if (explode_tick.contains(e.getPlayer()) && !p.isFlying() && !p.isGliding()) {

                cooldown.remove(p.getUniqueId());
                new BukkitRunnable() {
                    final Material block = e.getPlayer().getLocation().getBlock().getType();

                    @Override
                    public void run() {
                        ConditionExecutor executor = new ConditionExecutor();
                        if(executor.check("condition", "conditions", p, origin, getPowerFile(), null, p)){
                            if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                            if (p.isSneaking()) {
                                if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) > 3300)) {
                                    if (p.isSneaking()) {
                                        cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                                    } else {
                                        this.cancel();
                                    }

                                }
                                for(HashMap<String, Object> modifier : origin.getPowerFileFromType("genesis:explode_tick").getConditionFromString("modifier", "modifiers")){
                                    int power = Math.toIntExact((Long) modifier.get("power"));
                                    int resistance = Math.toIntExact((Long) modifier.get("resistance"));
                                    int charge = Math.toIntExact((Long) modifier.get("charge"));
                                    boolean fire = (boolean) modifier.get("fire");
                                    boolean break_blocks = (boolean) modifier.get("break_blocks");

                                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 2, false, false, false));
                                    if (!cooldown.containsKey(p.getUniqueId()) || (System.currentTimeMillis() - cooldown.get(p.getUniqueId()) >= 2900)) {

                                        List<Entity> nearby = p.getNearbyEntities(2, 2, 2);
                                        for (Entity tmp : nearby)
                                            if (tmp instanceof Damageable && tmp != p)
                                                ((Damageable) tmp).damage(15);
                                        List<Entity> nearby2 = p.getNearbyEntities(3, 3, 3);
                                        for (Entity tmp2 : nearby2)
                                            if (tmp2 instanceof Damageable && tmp2 != p)
                                                ((Damageable) tmp2).damage(10);
                                        List<Entity> nearby3 = p.getNearbyEntities(5, 5, 5);
                                        for (Entity tmp3 : nearby3)
                                            if (tmp3 instanceof Damageable && tmp3 != p)
                                                ((Damageable) tmp3).damage(5);
                                        e.setCancelled(true);
                                        cancel();

                                        if (origin.getPowerFileFromType("genesis:explode_tick").getThunderModifier() != null) {
                                            if (p.getWorld().isThundering()) {
                                                int power_thunder = Math.toIntExact((Long) origin.getPowerFileFromType("genesis:explode_tick").getThunderModifier().get("power"));
                                                int resistance_thunder = Math.toIntExact((Long) origin.getPowerFileFromType("genesis:explode_tick").getThunderModifier().get("resistance"));
                                                boolean fire_thunder = (boolean) origin.getPowerFileFromType("genesis:explode_tick").getThunderModifier().get("fire");
                                                boolean break_blocks_thunder = (boolean) origin.getPowerFileFromType("genesis:explode_tick").getThunderModifier().get("break_blocks");
                                                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, resistance_thunder, true, false, false));
                                                p.getWorld().createExplosion(p.getLocation(), power_thunder, fire_thunder, break_blocks_thunder, p);
                                                p.teleportAsync(p.getLocation());
                                                p.damage(5);
                                                e.setCancelled(true);
                                                this.cancel();
                                            } else {
                                                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, resistance, true, false, false));
                                                p.getWorld().createExplosion(p.getLocation(), (float) power, fire, break_blocks, p);
                                                cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                                                p.teleportAsync(p.getLocation());
                                                p.damage(10);
                                                e.setCancelled(true);
                                                this.cancel();
                                            }
                                        } else {
                                            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, resistance, true, false, false));
                                            p.getWorld().createExplosion(p.getLocation(), power, fire, break_blocks, p);
                                            cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                                            p.teleportAsync(p.getLocation());
                                            p.damage(10);
                                            e.setCancelled(true);
                                            this.cancel();
                                        }

                                    } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) >= charge)) {
                                        p.sendActionBar(ChatColor.RED + "[]");
                                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6, 4, false, false, false));
                                    } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) >= (charge * 3L / 4))) {
                                        p.sendActionBar(ChatColor.YELLOW + "----");
                                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6, 3, false, false, false));
                                    } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) >= (charge * 2L / 4))) {
                                        p.sendActionBar(ChatColor.GREEN + "------");
                                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6, 2, false, false, false));
                                    } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) <= (charge / 4))) {
                                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6, 1, false, false, false));
                                        p.sendActionBar(ChatColor.BLUE + "--------");
                                    }
                                }
                            } else {
                                this.cancel();
                            }
                        }else{
                            if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                        }
                    }
                }.runTaskTimer(GenesisMC.getPlugin(), 0L, 5L);
            }
        }
    }


    @Override
    public void run() {

    }

    @Override
    public String getPowerFile() {
        return "genesis:explode_tick";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return explode_tick;
    }
}
