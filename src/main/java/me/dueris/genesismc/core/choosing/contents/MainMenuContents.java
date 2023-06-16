package me.dueris.genesismc.core.choosing.contents;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.factory.CraftApoli;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import me.dueris.genesismc.core.utils.OriginContainer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import static me.dueris.genesismc.core.choosing.ChoosingCORE.itemProperties;
import static me.dueris.genesismc.core.choosing.ChoosingCORE.itemPropertiesMultipleLore;
import static me.dueris.genesismc.core.choosing.ChoosingCUSTOM.cutStringIntoLists;
import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;
import static org.bukkit.ChatColor.*;

public class MainMenuContents {

    private static ItemStack applyProperties(ItemStack icon) {
        for (OriginContainer origin : CraftApoli.getCoreOrigins()) {
            if (origin.getMaterialIcon() != icon.getType()) continue;
            ItemMeta meta = icon.getItemMeta();
            NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "originTag");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, origin.getTag());
            icon.setItemMeta(meta);
        }
        return icon;
    }

    public static @Nullable ItemStack @NotNull [] GenesisMainMenuContents(Player p) {

        HashMap<String, String> originNames = new HashMap<>();
        for (OriginContainer origin : CraftApoli.getCoreOrigins()) {
            originNames.put(origin.getTag(), origin.getName());
        }

        HashMap<String, String> originDescriptions = new HashMap<>();
        for (OriginContainer origin : CraftApoli.getCoreOrigins()) {
            originDescriptions.put(origin.getTag(), origin.getDescription());
        }


        ItemStack human = applyProperties(new ItemStack(Material.PLAYER_HEAD));
        ItemStack enderian = applyProperties(new ItemStack(Material.ENDER_PEARL));
        ItemStack shulk = applyProperties(new ItemStack(Material.SHULKER_SHELL));
        ItemStack arachnid = applyProperties(new ItemStack(Material.COBWEB));
        ItemStack creep = applyProperties(new ItemStack(Material.GUNPOWDER));
        ItemStack phantom = applyProperties(new ItemStack(Material.PHANTOM_MEMBRANE));
        ItemStack slimeling = applyProperties(new ItemStack(Material.SLIME_BALL));
        ItemStack feline = applyProperties(new ItemStack(Material.ORANGE_WOOL));
        ItemStack blazeborn = applyProperties(new ItemStack(Material.BLAZE_POWDER));
        ItemStack starborne = applyProperties(new ItemStack(Material.END_CRYSTAL));
        ItemStack merling = applyProperties(new ItemStack(Material.COD));
        ItemStack allay = applyProperties(new ItemStack(Material.COOKIE));
        ItemStack rabbit = applyProperties(new ItemStack(Material.CARROT));
        ItemStack bumblebee = applyProperties(new ItemStack(Material.HONEYCOMB));
        ItemStack elytrian = applyProperties(new ItemStack(Material.ELYTRA));
        ItemStack avian = applyProperties(new ItemStack(Material.FEATHER));
        ItemStack piglin = applyProperties(new ItemStack(Material.GOLD_INGOT));
        ItemStack sculkling = applyProperties(new ItemStack(Material.ECHO_SHARD));
        ItemStack custom_originmenu = applyProperties(new ItemStack(Material.TIPPED_ARROW));
        ItemStack background = applyProperties(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        ItemStack filler = applyProperties(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        ItemStack close = applyProperties(new ItemStack(Material.BARRIER));


        SkullMeta skull_p = (SkullMeta) human.getItemMeta();
        skull_p.setOwningPlayer(p);
        skull_p.setOwner(p.getName());
        skull_p.setPlayerProfile(p.getPlayerProfile());
        skull_p.setOwnerProfile(p.getPlayerProfile());
        human.setItemMeta(skull_p);
        human = itemProperties(human, WHITE + originNames.get("origins:human"), null, null, originDescriptions.get("origins:human"));
        enderian = itemPropertiesMultipleLore(enderian, LIGHT_PURPLE + originNames.get("origins:enderian"), null, null, cutStringIntoLists(originDescriptions.get("origins:enderian")));
        shulk = itemPropertiesMultipleLore(shulk, DARK_PURPLE + originNames.get("origins:shulk"), null, null, cutStringIntoLists(originDescriptions.get("origins:shulk")));
        arachnid = itemPropertiesMultipleLore(arachnid, RED + originNames.get("origins:arachnid"), null, null, cutStringIntoLists(originDescriptions.get("origins:arachnid")));
        creep = itemPropertiesMultipleLore(creep, GREEN + originNames.get("origins:creep"), null, null, cutStringIntoLists(originDescriptions.get("origins:creep")));
        phantom = itemPropertiesMultipleLore(phantom, BLUE + originNames.get("origins:phantom"), null, null, cutStringIntoLists(originDescriptions.get("origins:phantom")));
        slimeling = itemProperties(slimeling, GREEN + originNames.get("origins:slimeling"), null, null, originDescriptions.get("origins:slimeling"));
        feline = itemProperties(feline, AQUA + originNames.get("origins:feline"), null, null, (originDescriptions.get("origins:feline")));
        blazeborn = itemPropertiesMultipleLore(blazeborn, GOLD + originNames.get("origins:blazeborn"), null, null, cutStringIntoLists(originDescriptions.get("origins:blazeborn")));
        starborne = itemProperties(starborne, LIGHT_PURPLE + originNames.get("origins:starborne"), null, null, originDescriptions.get("origins:starborne"));
        merling = itemProperties(merling, BLUE + originNames.get("origins:merling"), null, null, originDescriptions.get("origins:merling"));
        allay = itemProperties(allay, AQUA + originNames.get("origins:allay"), null, null, originDescriptions.get("origins:allay"));
        rabbit = itemPropertiesMultipleLore(rabbit, GOLD + originNames.get("origins:rabbit"), null, null, cutStringIntoLists(originDescriptions.get("origins:rabbit")));
        bumblebee = itemProperties(bumblebee, YELLOW + originNames.get("origins:bee"), null, null, originDescriptions.get("origins:bee"));
        elytrian = itemPropertiesMultipleLore(elytrian, GRAY + originNames.get("origins:elytrian"), null, null, cutStringIntoLists(originDescriptions.get("origins:elytrian")));
        avian = itemPropertiesMultipleLore(avian, DARK_AQUA + originNames.get("origins:avian"), null, null, cutStringIntoLists(originDescriptions.get("origins:avian")));
        piglin = itemPropertiesMultipleLore(piglin, GOLD + originNames.get("origins:piglin"), null, null, cutStringIntoLists(originDescriptions.get("origins:piglin")));
        sculkling = itemProperties(sculkling, BLUE + originNames.get("origins:sculkling"), null, null, originDescriptions.get("origins:sculkling"));

        custom_originmenu = itemProperties(custom_originmenu, ChatColor.YELLOW + "Custom Origins", ItemFlag.HIDE_ENCHANTS, null, null);
        close = itemProperties(close, RED + "Close", null, null, RED + "Cancel choosing");

        ItemStack randomOrb = itemProperties(orb.clone(), LIGHT_PURPLE + "Random", null, null, null);
        NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "orb");
        ItemMeta randomOrbmeta = randomOrb.getItemMeta();
        randomOrbmeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "orb");
        randomOrb.setItemMeta(randomOrbmeta);

        ItemStack[] mainmenucontents = {avian, arachnid, elytrian, shulk, feline, enderian, merling, blazeborn, phantom,
                background, background, background, background, human, background, background, background, background,
                filler, filler, filler, filler, filler, filler, filler, filler, filler,
                starborne, allay, rabbit, bumblebee, background, sculkling, creep, slimeling, piglin,
                background, background, background, background, background, background, background, background, background,
                filler, filler, filler, randomOrb, close, custom_originmenu, filler, filler, filler};

        return mainmenucontents;

    }

}
