package net.developertobi.guilib.api.item;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public record ItemEnchantment(Enchantment enchantment, int strength, boolean isActive) {

    public void performAction(ItemStack item) {
        if (this.isActive) {
            item.addEnchantment(this.enchantment, this.strength);
        } else {
            item.removeEnchantment(this.enchantment);
        }
    }

    public static ItemEnchantment of(Enchantment enchantment, int strength, boolean isActive) {
        return new ItemEnchantment(enchantment, strength, isActive);
    }
}
