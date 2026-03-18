package net.developertobi.guilib.api;

import net.developertobi.guilib.api.gui.GuiHandler;
import net.developertobi.guilib.api.item.GuiItem;
import net.developertobi.guilib.api.item.GuiPos;
import net.developertobi.guilib.api.pagination.GuiPagination;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface GuiApi {

    GuiHandler getGuiHandler();

    GuiItem placeholder(Material type);
    GuiItem nextPage(ItemStack item, GuiPagination pagination);
    GuiItem previousPage(ItemStack item, GuiPagination pagination);
    GuiItem navigator(ItemStack item, String guiKey);
    GuiItem of(ItemStack item);
    GuiItem of(ItemStack item, GuiItemAction action);

    void openConfirmationGui(
        Player holder,
        Component title,
        ItemStack displayItem,
        Consumer<ItemStack> onAccept,
        Consumer<ItemStack> onDeny
    );
}
