package net.developertobi.guilib.api;

import net.developertobi.guilib.api.gui.Gui;
import net.developertobi.guilib.api.gui.GuiView;
import net.developertobi.guilib.api.item.GuiItem;
import net.developertobi.guilib.api.pagination.GuiPagination;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Facade for GUI operations. Provides static methods for opening, caching,
 * and managing GUI views, as well as creating common GUI item types.
 */
public final class GuiHelper {

    private GuiHelper() {
    }

    // --- GUI view operations ---

    public static void openStaticGui(Player holder, Component title, Gui provider) {
        GuiProvider.getApi().getGuiHandler().openStaticView(holder, title, provider, true);
    }

    public static void openGui(Player holder, String key) {
        GuiView view = GuiProvider.getApi().getGuiHandler().getView(holder, key);
        if (view != null) {
            view.open(holder);
        }
    }

    public static GuiView getGui(Player holder, String key) {
        return GuiProvider.getApi().getGuiHandler().getView(holder, key);
    }

    public static void cacheGui(Player holder, Component title, Gui provider) {
        GuiProvider.getApi().getGuiHandler().cacheView(holder, title, provider);
    }

    public static void removeCachedGui(Player holder, GuiView gui) {
        GuiProvider.getApi().getGuiHandler().removeCachedView(holder, gui);
    }

    public static void clearCachedGuis(Player holder) {
        GuiProvider.getApi().getGuiHandler().clearCachedViews(holder);
    }

    public static void updateCachedGui(Player holder, String key) {
        GuiProvider.getApi().getGuiHandler().updateCachedView(holder, key);
    }

    // --- GUI item creation ---

    public static GuiItem item(ItemStack item) {
        return GuiProvider.getApi().of(item);
    }

    public static GuiItem item(ItemStack item, GuiItemAction action) {
        return GuiProvider.getApi().of(item, action);
    }

    public static GuiItem placeholder(Material type) {
        return GuiProvider.getApi().placeholder(type);
    }

    public static GuiItem navigator(ItemStack item, String guiKey) {
        return GuiProvider.getApi().navigator(item, guiKey);
    }

    public static GuiItem nextPage(ItemStack item, GuiPagination pagination) {
        return GuiProvider.getApi().nextPage(item, pagination);
    }

    public static GuiItem previousPage(ItemStack item, GuiPagination pagination) {
        return GuiProvider.getApi().previousPage(item, pagination);
    }
}
