package net.developertobi.guilib.api;

import net.developertobi.guilib.api.item.GuiItem;
import net.developertobi.guilib.api.item.GuiPos;
import org.bukkit.event.inventory.InventoryClickEvent;

@FunctionalInterface
public interface GuiItemAction {

    void run(GuiPos position, GuiItem guiItem, InventoryClickEvent event);
}
