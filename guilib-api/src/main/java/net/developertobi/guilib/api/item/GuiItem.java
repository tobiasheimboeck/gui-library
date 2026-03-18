package net.developertobi.guilib.api.item;

import net.developertobi.guilib.api.gui.GuiController;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface GuiItem {

    ItemStack getItem();
    void setItem(ItemStack item);

    void runAction(GuiPos position, GuiItem guiItem, InventoryClickEvent event);

    void update(GuiController controller, Modification modification, Object... values);

    enum Modification {
        TYPE,
        DISPLAY_NAME,
        LORE,
        AMOUNT,
        INCREMENT,
        ENCHANTMENTS,
        GLOWING
    }
}
