package net.developertobi.guilib.bukkit.api.gui;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.developertobi.guilib.api.gui.Gui;
import net.developertobi.guilib.api.gui.GuiController;
import net.developertobi.guilib.api.gui.GuiHandler;
import net.developertobi.guilib.api.gui.GuiProperties;
import net.developertobi.guilib.api.gui.GuiView;
import net.developertobi.guilib.api.utils.MathUtils;
import net.developertobi.guilib.bukkit.GuiLibBukkit;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class GuiHandlerImpl implements GuiHandler {

    private static NamespacedKey openGuiKey;

    private static NamespacedKey getOpenGuiKey() {
        if (openGuiKey == null) {
            openGuiKey = new NamespacedKey(GuiLibBukkit.getInstance(), "open-gui");
        }
        return openGuiKey;
    }

    private final Multimap<Player, GuiView> inventories = ArrayListMultimap.create();

    @Override
    public Multimap<Player, GuiView> getInventories() {
        return inventories;
    }

    @Override
    public void openStaticView(Player holder, Component title, Gui provider, boolean forceSyncOpening) {
        GuiView gui = cacheView(holder, title, provider, true);
        if (gui == null) return;
        gui.open(holder, forceSyncOpening);
    }

    @Override
    public GuiView cacheView(Player holder, Component title, Gui provider, boolean staticGui) {
        GuiProperties annotation = provider.getClass().getAnnotation(GuiProperties.class);
        String permission = annotation == null ? "" : annotation.permission();

        if (permission != null && !permission.isEmpty() && !holder.hasPermission(permission)) {
            return null;
        }

        GuiController controller = new GuiControllerImpl(provider);
        org.bukkit.inventory.Inventory rawInventory = Bukkit.createInventory(holder, controller.getRows() * controller.getColumns(), title);

        controller.setRawInventory(rawInventory);

        for (int i = 0; i < controller.getSlotCount(); i++) {
            controller.getContents().put(MathUtils.slotToPosition(i, controller.getColumns()), null);
        }

        provider.init(holder, controller);

        controller.updateRawInventory();

        GuiView gui = new GuiImpl(provider, title, controller, staticGui);

        inventories.put(holder, gui);
        return gui;
    }

    @Override
    public void updateCachedView(Player holder, String viewId) {
        GuiView gui = getView(holder, viewId);
        if (gui == null) return;

        Component title = gui.getTitle();
        Gui provider = gui.getController().getProvider();

        inventories.get(holder).removeIf(inv -> inv.getName().equalsIgnoreCase(viewId));
        cacheView(holder, title, provider);

        GuiView elytraView = getView(holder, viewId);
        if (elytraView == null) return;
        String key = elytraView.getName();

        String openGuiName = holder.getPersistentDataContainer().get(getOpenGuiKey(), PersistentDataType.STRING);
        if (openGuiName == null || !key.equalsIgnoreCase(openGuiName)) return;

        elytraView.open(holder, true);
    }

    @Override
    public void clearCachedViews(Player holder) {
        inventories.removeAll(holder);
        if (holder.getPersistentDataContainer().has(getOpenGuiKey(), PersistentDataType.STRING)) {
            holder.getPersistentDataContainer().remove(getOpenGuiKey());
        }
    }

    @Override
    public void removeCachedView(Player holder, GuiView view) {
        inventories.remove(holder, view);
        if (holder.getPersistentDataContainer().has(getOpenGuiKey(), PersistentDataType.STRING)) {
            holder.getPersistentDataContainer().remove(getOpenGuiKey());
        }
    }

    @Override
    public GuiView getView(Player holder, String name) {
        return inventories.get(holder).stream()
                .filter(inv -> inv.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
