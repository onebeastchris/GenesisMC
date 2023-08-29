package me.dueris.genesismc.factory.conditions.entity;

import me.dueris.genesismc.CooldownStuff;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.powers.player.Climbing;
import me.dueris.genesismc.factory.powers.player.RestrictArmor;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import static me.dueris.genesismc.factory.powers.player.RestrictArmor.compareValues;

public class EntityCondition {

    public static Optional<Boolean> check(HashMap<String, Object> condition, Player p, Entity entity, String powerfile) {
        // TODO: use inverted
        boolean inverted = (boolean) condition.getOrDefault("inverted", false);
        if (condition.get("type") == null) return Optional.empty();

        String type = condition.get("type").toString().toLowerCase();

        switch (type) {
            case "origins:ability" -> {
                String ability = condition.get("ability").toString().toLowerCase();

                switch (ability) {
                    case "minecraft:flying" -> {
                        if (entity instanceof Player player) {
                            return Optional.of(player.isFlying());
                        }
                    }
                    case "minecraft:instabuild" -> {
                        if (entity instanceof Player player) {
                            return Optional.of(player.getGameMode().equals(GameMode.CREATIVE));
                        }
                    }
                    case "minecraft:invulnerable" -> {
                        return Optional.of(entity.isInvulnerable());
                    }
                    case "minecraft:maybuild" -> {
                        return Optional.of(entity.hasPermission("minecraft.build"));
                    }
                    case "minecraft:mayfly" -> {
                        if (entity instanceof Player player) {
                            return Optional.of(player.getAllowFlight());
                        }
                    }
                }
            }

            case "origins:advancement" -> {
                String advancementString = condition.get("advancement").toString();

                if (entity instanceof Player player) {
                    World world = player.getWorld();
                    File worldFolder = world.getWorldFolder();
                    File advancementsFolder = new File(worldFolder, "advancements");
                    File playerAdvancementFile = new File(advancementsFolder, player.getUniqueId() + ".json");

                    if (playerAdvancementFile.exists()) {
                        try {
                            JSONParser parser = new JSONParser();
                            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(playerAdvancementFile));
                            JSONObject advancementJson = (JSONObject) jsonObject.get(advancementString);

                            if (advancementJson != null) {
                                Boolean done = (Boolean) advancementJson.get("done");
                                return Optional.of(Objects.requireNonNullElse(done, false));
                            } else {
                                return Optional.of(false);
                            }
                        } catch (IOException | ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        return Optional.of(false);
                    }
                }
            }

            case "origins:air" -> {
                if (entity instanceof Player player) {
                    if (compareValues(player.getRemainingAir(), condition.get("comparison").toString(), Integer.parseInt(condition.get("compare_to").toString()))) {
                        return Optional.of(true);
                    }
                }
            }

            case "origins:attribute" -> {
                if (entity instanceof Player player) {
                    String attributeString = condition.get("attribute").toString().split(":")[1].replace(".", "_").toUpperCase();
                    if (compareValues(player.getAttribute(Attribute.valueOf(attributeString)).getValue(), condition.get("comparison").toString(), Integer.parseInt(condition.get("compare_to").toString()))) {
                        return Optional.of(true);
                    }
                }
            }

            // TODO: continue entity_condition to use biome condition for origins:biome in some cases. see https://origins.readthedocs.io/en/latest/types/entity_condition_types/biome/

            case "origins:biome" -> {
                String biomeString = condition.get("biome").toString().split(":")[1].replace(".", "_").toUpperCase();
                if (entity.getLocation().getBlock().getBiome().equals(Biome.valueOf(biomeString))) {
                    return Optional.of(true);
                }
            }

            case "origins:block_collision" -> {
                // TODO: add block_condition check for origins:block_collision. see https://origins.readthedocs.io/en/latest/types/entity_condition_types/block_collision/
                String offsetX = condition.get("offset_x").toString();
                String offsetY = condition.get("offset_y").toString();
                String offsetZ = condition.get("offset_z").toString();
                if (entity instanceof Player player) {
                    Location playerLocation = player.getLocation();
                    World world = player.getWorld();

                    int blockX = playerLocation.getBlockX() + Integer.parseInt(offsetX);
                    int blockY = playerLocation.getBlockY() + Integer.parseInt(offsetY);
                    int blockZ = playerLocation.getBlockZ() + Integer.parseInt(offsetZ);

                    Block block = world.getBlockAt(blockX, blockY, blockZ);

                    if (block.getType() != Material.AIR) {
                        return Optional.of(true);
                    }

                }
            }

            case "origins:block_in_radius" -> {
                // TODO: add block_condition check for origins:block_collision. see https://origins.readthedocs.io/en/latest/types/entity_condition_types/block_collision/
                int radius = Math.toIntExact((Long) condition.get("radius"));
                String shape = condition.get("shape").toString();
                String comparison = condition.get("comparison").toString();
                int compare_to = Integer.parseInt(condition.get("compare_to").toString());

                Location center = entity.getLocation();
                int centerX = center.getBlockX();
                int centerY = center.getBlockY();
                int centerZ = center.getBlockZ();
                World world = center.getWorld();

                int minX = center.getBlockX() - radius;
                int minY = center.getBlockY() - radius;
                int minZ = center.getBlockZ() - radius;
                int maxX = center.getBlockX() + radius;
                int maxY = center.getBlockY() + radius;
                int maxZ = center.getBlockZ() + radius;

                int blockCount = 0;

                if (shape.equalsIgnoreCase("sphere")) {
                    blockCount = countBlocksInSphere(centerX, centerY, centerZ, radius, world);
                } else if (shape.equalsIgnoreCase("star")) {
                    blockCount = countBlocksInStar(centerX, centerY, centerZ, radius, world);
                } else if (shape.equalsIgnoreCase("cube")) {
                    blockCount = countBlocksInCube(minX, minY, minZ, maxX, maxY, maxZ, world);
                }
                if (compareValues(blockCount, comparison, compare_to)) {
                    return Optional.of(true);
                }
            }

            case "origins:brightness" -> {
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                double brightness;
                int lightLevel = entity.getLocation().getBlock().getLightLevel();
                int ambientLight = 0;

                //calculate ambient light
                if (entity.getWorld() == Bukkit.getServer().getWorlds().get(0)) { //is overworld
                    ambientLight = 0;
                } else if (entity.getWorld() == Bukkit.getServer().getWorlds().get(2)) {
                    ambientLight = 1;
                }
                brightness = ambientLight + (1 - ambientLight) * lightLevel / (60 - 3 * lightLevel);
                if (compareValues(brightness, comparison, compare_to)) {
                    return Optional.of(true);
                }
            }

            case "origins:climbing" -> {
                if (entity instanceof Player player) {
                    if (player.isClimbing()) {
                        return Optional.of(true);
                    }
                    Climbing climbing = new Climbing();
                    if (climbing.isActiveClimbing(player)) {
                        return Optional.of(true);
                    }
                }
            }

            case "origins:collided_horizontally" -> {
                if (entity instanceof Player player) {
                    Block block = player.getLocation().getBlock();
                    BoundingBox playerBoundingBox = player.getBoundingBox();
                    BoundingBox blockBoundingBox = block.getBoundingBox();

                    if (blockBoundingBox.overlaps(playerBoundingBox)) {
                        return Optional.of(true);
                    }

                }
            }

            case "origins:command" -> {
                // TODO - https://origins.readthedocs.io/en/latest/types/entity_condition_types/command/
            }

            case "origins:creative_flying" -> {
                if (entity instanceof Player player) {
                    if (player.isFlying()) {
                        return Optional.of(true);
                    }

                }
            }

            case "origins:daytime" -> {
                if (entity.getWorld().isDayTime()) {
                    return Optional.of(true);
                }
            }

            case "origins:dimension" -> {
                if (entity.getWorld().getEnvironment().toString().equals(condition.get("dimension").toString().split(":")[1].replace("the_", "").toUpperCase())) {
                    return Optional.of(true);
                }
            }

            case "origins:distance_from_coordinates" -> {
                // TODO: https://origins.readthedocs.io/en/latest/types/entity_condition_types/distance_from_coordinates/
            }

            case "origins:elytra_flight_possible" -> {
                // TODO: https://origins.readthedocs.io/en/latest/types/entity_condition_types/elytra_flight_possible/
            }

            case "origins:enchantment" -> {
                // TODO: https://origins.readthedocs.io/en/latest/types/entity_condition_types/enchantment/
            }

            case "origins:entity_group" -> {
                // TODO: https://origins.readthedocs.io/en/latest/types/entity_condition_types/entity_group/
            }

            case "origins:entity_type" -> {
                if (entity.getType().toString().equals(condition.get("entity_type").toString().split(":")[1].toUpperCase())) {
                    return Optional.of(true);
                }
            }

            case "origins:fluid_height" -> {
                String fluid = condition.get("fluid").toString().toLowerCase();

                if (fluid.equals("lava")) {
                    return Optional.of(entity.isInLava());
                } else if (fluid.equals("water")) {
                    return Optional.of(entity.isInWaterOrBubbleColumn());
                }
            }

            case "origins:in_rain" -> {
                return Optional.of(entity.isInRain());
            }

            case "origins:health" -> {
                if (RestrictArmor.compareValues(p.getHealth(), condition.get("comparison").toString(), Double.parseDouble(condition.get("compare_to").toString()))) {
                    return Optional.of(true);
                }
            }

            case "origins:exposed_to_sun" -> {
                if ((p.getLocation().getBlockY() + 1 > p.getWorld().getHighestBlockYAt(p.getLocation()))) {
                    if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
                        return Optional.of(p.getWorld().isDayTime());
                    }
                }
            }

            case "origins:sneaking" -> {
                return Optional.of(entity.isSneaking());
            }

            case "origins:resource" -> {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    return Optional.of(!CooldownStuff.isPlayerInCooldownFromTag(p, origin.getPowerFileFromType(powerfile).getTag()));
                }
            }
        }

        return Optional.of(false);
    }

    private static int countBlocksInCube(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, World world) {
        int blockCount = 0;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location location = new Location(world, x, y, z);
                    Block block = location.getBlock();
                    if (block.getType() != Material.AIR) {
                        blockCount++;
                    }
                }
            }
        }

        return blockCount;
    }

    private static int countBlocksInStar(int centerX, int centerY, int centerZ, int radius, World world) {
        int blockCount = 0;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2));

                    if (distance <= radius && distance >= radius / 2) {
                        Location location = new Location(world, x, y, z);
                        Block block = location.getBlock();

                        if (block.getType() != Material.AIR) {
                            blockCount++;
                        }
                    }
                }
            }
        }

        return blockCount;
    }

    public static int countBlocksInSphere(int centerX, int centerY, int centerZ, int radius, World world) {
        int blockCount = 0;
        int squaredRadius = radius * radius;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    if ((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY) + (z - centerZ) * (z - centerZ) <= squaredRadius) {
                        Location location = new Location(world, x, y, z);
                        if (location.getBlock().getType() != Material.AIR) {
                            blockCount++;
                        }
                    }
                }
            }
        }

        return blockCount;
    }


}
