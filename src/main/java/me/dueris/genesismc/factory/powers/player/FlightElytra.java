package me.dueris.genesismc.factory.powers.player;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.protocol.SendStringPacketPayload;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

import static me.dueris.genesismc.entity.OriginPlayer.launchElytra;

public class FlightElytra extends CraftPower implements Listener {
    public static ArrayList<UUID> glidingPlayers = new ArrayList<>();

    @Override
    public void setActive(String tag, Boolean bool){
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }

    

    @EventHandler
    @SuppressWarnings("unchecked")
    public void ExecuteFlight(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        if (elytra.contains(e.getPlayer())) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                ConditionExecutor executor = new ConditionExecutor();
                if(executor.check("condition", "conditions", p, origin, getPowerFile(), null, p)){
                    if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                    if (origin.getPowerFileFromType("origins:elytra_flight").getShouldRender()) {
                        SendStringPacketPayload.sendCustomPacket(p, "ExecuteGenesisOriginsElytraRenderID:12232285");
                        CraftPlayer player = (CraftPlayer) p;
                        player.getWorld().setGameRule(GameRule.DISABLE_ELYTRA_MOVEMENT_CHECK, false);
                    }
                    if (!p.isOnGround() && !p.isGliding()) {
                        glidingPlayers.add(p.getUniqueId());
                        if (p.getGameMode() == GameMode.SPECTATOR) return;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (p.isOnGround() || p.isFlying()) {
                                    this.cancel();
                                    glidingPlayers.remove(p.getUniqueId());
                                }
                                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5, 3, false, false, false));
                                p.setGliding(true);
                                p.setFallDistance(0);
                            }
                        }.runTaskTimer(GenesisMC.getPlugin(), 0L, 1L);
                    }
                }else{
                    if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                }

            }
        }
    }

    @EventHandler
    public void BoostHandler(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (elytra.contains(e.getPlayer()) && e.getAction().equals(Action.LEFT_CLICK_AIR)) {
            if (!glidingPlayers.contains(p)) return;
            if (p.getGameMode() == GameMode.CREATIVE) return;
            if (e.getItem() != null) {
                ItemStack rocket = new ItemStack(Material.FIREWORK_ROCKET);
                if (e.getItem().isSimilar(rocket)) {
                    launchElytra(p);
                    e.getItem().setAmount(e.getItem().getAmount() - 1);
                    p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 10, 1);
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void ElytraDamageHandler(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (elytra.contains(p)) {
                if (glidingPlayers.contains(p)) {
                    if (e.getCause().equals(EntityDamageEvent.DamageCause.FLY_INTO_WALL) || e.getCause().equals(EntityDamageEvent.DamageCause.CONTACT)) {
                        e.setDamage(e.getDamage() * 0.25);
                    }
                }
            }
            if (more_kinetic_damage.contains(p)) {
                if (!glidingPlayers.contains(p)) {
                    if (e.getCause().equals(EntityDamageEvent.DamageCause.FLY_INTO_WALL) || e.getCause().equals(EntityDamageEvent.DamageCause.CONTACT) || e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                        e.setDamage(e.getDamage() * 1.5);
                    }
                }
            }
        }
    }


    @Override
    public void run() {

    }

    @Override
    public String getPowerFile() {
        return "origins:elytra_flight";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return elytra;
    }
}
