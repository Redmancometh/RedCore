package com.redmancometh.redcore.config;

import com.redmancometh.redcore.util.ItemUtil;
import lombok.*;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class ItemWrapper {
    @Getter
    @Setter
    private int amount;
    @Getter
    @Setter
    private String color, owner, title, author;
    @Getter
    @Setter
    private short dataValue = 0;
    @Getter
    @Setter
    private String displayName;
    @Getter
    @Setter
    private LinkedHashSet<PotionEffect> effects;
    @Getter
    @Setter
    private LinkedHashMap<String, Integer> enchants, storedEnchants;
    @Getter
    @Setter
    private LinkedHashSet<FireworkEffect> fireworkEffects;
    @Getter
    @Setter
    private LinkedHashSet<ItemFlag> flags;
    @Getter
    @Setter
    private ArrayList<String> lore;
    @Getter
    @Setter
    private Material material;
    @Getter
    @Setter
    private ArrayList<String> pages;
    private ArrayList<Pattern> patterns;

    private ItemStack toItem() {
        try {
            ItemStack is = new ItemStack(material, amount, dataValue);
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            if (enchants != null)
                enchants.forEach((k, v) -> meta.addEnchant(Enchantment.getByName(k), v, true));
            if (storedEnchants != null && meta instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
                storedEnchants.forEach((k, v) -> esm.addStoredEnchant(Enchantment.getByName(k), v, true));
            }
            if (effects != null && meta instanceof PotionMeta) {
                PotionMeta pm = (PotionMeta) meta;
                effects.forEach((e) -> pm.addCustomEffect(e, true));
            }
            if (owner != null && meta instanceof SkullMeta)
                ((SkullMeta) meta).setOwner(owner);
            if (meta instanceof BookMeta) {
                BookMeta bm = (BookMeta) meta;
                if (title != null)
                    bm.setTitle(title);
                if (author != null)
                    bm.setAuthor(author);
                if (pages != null)
                    bm.setPages(pages);
            }
            if (fireworkEffects != null && meta instanceof FireworkMeta) {
                FireworkMeta fm = (FireworkMeta) meta;
                fireworkEffects.forEach(fm::addEffect);
            }
            if (color != null && meta instanceof LeatherArmorMeta) {
                LeatherArmorMeta lm = (LeatherArmorMeta) meta;
                lm.setColor(Color.fromRGB(Integer.valueOf(color, 16)));
            }
            if (meta instanceof BannerMeta) {
                BannerMeta bm = (BannerMeta) meta;
                if (color != null)
                    bm.setBaseColor(DyeColor.valueOf(color));
                if (patterns != null)
                    bm.setPatterns(patterns);
            }
            is.setItemMeta(meta);
            return is;
        } catch (Throwable e) {
            System.err.println("Failed to convert " + material + " to ItemStack");
            e.printStackTrace();
        }
        return ItemUtil.buildItem(Material.REDSTONE, "§cFailed to load item", lore);
    }
}
