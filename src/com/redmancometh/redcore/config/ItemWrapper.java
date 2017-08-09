package com.redmancometh.redcore.config;

import com.redmancometh.redcore.spigotutils.*;
import com.redmancometh.redcore.util.ItemUtil;
import lombok.Data;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;

import java.util.*;

/**
 * A complete, featureful wrapper for items having all the important usability features, including the possibility to be saved in the config.
 */
@Data
public class ItemWrapper implements Cloneable {
    private int amount;
    private String color, owner, title, author;
    private short dataValue = 0;
    private String displayName;
    private LinkedHashSet<PotionEffect> effects;
    private LinkedHashMap<String, Integer> enchants, storedEnchants;
    private List<FireworkEffect> fireworkEffects;
    private LinkedHashSet<ItemFlag> flags;
    private List<String> lore;
    private Material material;
    private List<String> pages;
    private List<Pattern> patterns;

    /**
     * Constructs an item from String
     *
     * @param in - The input item String
     */
    public ItemWrapper(String in) {
        this(ItemUtils.stringToItemStack(in));
    }

    /**
     * Constructs a new ItemWrapper from an ItemStack
     *
     * @param is - Convertable ItemStack
     */
    public ItemWrapper(ItemStack is) {
        if (is == null || is.getType() == Material.AIR) {
            material = Material.AIR;
            dataValue = -1;
            return;
        }
        try {
            material = is.getType();
            dataValue = is.getDurability();
            amount = is.getAmount();
            ItemMeta meta = is.getItemMeta();
            if (meta.hasDisplayName())
                displayName = meta.getDisplayName();
            if (meta.hasLore())
                lore = meta.getLore();
            Map<Enchantment, Integer> m = meta.getEnchants();
            if (m != null && !m.isEmpty()) {
                enchants = new LinkedHashMap<>();
                m.forEach((e, i) -> enchants.put(e.getName(), i));
            } else if (meta instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
                m = esm.getStoredEnchants();
                if (m != null) {
                    storedEnchants = new LinkedHashMap<>();
                    m.forEach((k, v) -> storedEnchants.put(k.getName(), v));
                }
            } else if (meta instanceof PotionMeta) {
                PotionMeta pm = (PotionMeta) meta;
                List<PotionEffect> ce = pm.getCustomEffects();
                if (ce != null)
                    effects.addAll(ce);
            } else if (meta instanceof SkullMeta)
                owner = ((SkullMeta) meta).getOwner();
            else if (meta instanceof BookMeta) {
                BookMeta bm = (BookMeta) meta;
                title = bm.getTitle();
                author = bm.getAuthor();
                pages = bm.getPages();
            } else if (fireworkEffects != null && meta instanceof FireworkMeta) {
                FireworkMeta fm = (FireworkMeta) meta;
                fireworkEffects = fm.getEffects();
            } else if (color != null && meta instanceof LeatherArmorMeta) {
                LeatherArmorMeta lm = (LeatherArmorMeta) meta;
                color = Integer.toHexString(lm.getColor().asBGR());
            } else if (meta instanceof BannerMeta) {
                BannerMeta bm = (BannerMeta) meta;
                color = Integer.toHexString(bm.getBaseColor().getColor().asRGB());
                patterns = bm.getPatterns();
            }
        } catch (Throwable e) {
            System.err.println("Failed to convert " + material + " to ItemStack");
            SU.error(SU.cs, e, "RedCore", "com.redmancometh");
        }
    }

    /**
     * Creates a copy of this ItemWrapper, without leaving any reference towards the original one.
     *
     * @return The cloned ItemWrapper
     */
    @Override
    public ItemWrapper clone() {
        return ConfigManager.gson.fromJson(ConfigManager.gson.toJson(this), ItemWrapper.class);
    }

    /**
     * Converts the ItemWrapper to a one line user readable String.
     *
     * @return The conversion result
     */
    @Override
    public String toString() {
        return ItemUtils.itemToString(getItem());
    }

    /**
     * Get the item with extra lore added and filled with the given variables
     *
     * @param extraLore - The addable extra lore
     * @param vars      - The fillable variables
     * @return The ItemStack with the added variable filled lore.
     */
    public ItemStack getItem(Iterable<String> extraLore, Object... vars) {
        ItemStack is = getItem();
        if (is == null || is.getType() == Material.AIR)
            return is;
        ItemMeta meta = is.getItemMeta();
        List<String> cur = meta.getLore();
        if (cur == null)
            cur = new ArrayList<>();
        cur.addAll(SU.fillVariables(extraLore, vars));
        meta.setLore(cur);
        is.setItemMeta(meta);
        return is;
    }

    /**
     * Get the Bukkit ItemStack from this ItemWrapper
     *
     * @return The Bukkit ItemStack
     */
    public ItemStack getItem() {
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
            SU.error(SU.cs, e, "RedCore", "com.redmancometh");
        }
        return ItemUtil.buildItem(Material.REDSTONE, "§cFailed to load item", lore);
    }
}
