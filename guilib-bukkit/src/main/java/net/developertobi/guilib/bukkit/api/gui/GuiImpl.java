package net.developertobi.guilib.bukkit.api.gui;

import net.developertobi.guilib.api.gui.Gui;
import net.developertobi.guilib.api.gui.GuiController;
import net.developertobi.guilib.api.gui.GuiView;
import net.developertobi.guilib.api.pagination.GuiPagination;
import net.developertobi.guilib.bukkit.GuiLibBukkit;
import net.developertobi.guilib.bukkit.utils.SoundUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataType;

public class GuiImpl implements GuiView {

    private static NamespacedKey openGuiKey;

    private static NamespacedKey getOpenGuiKey() {
        if (openGuiKey == null) {
            openGuiKey = new NamespacedKey(GuiLibBukkit.getInstance(), "open-gui");
        }
        return openGuiKey;
    }

    private final Gui provider;
    private final Component title;
    private final GuiController controller;
    private final boolean isStaticGui;
    private final GuiPagination pagination;
    private final String name;
    private final int rows;
    private final int columns;
    private final boolean isCloseable;

    public GuiImpl(Gui provider, Component title, GuiController controller, boolean isStaticGui) {
        this.provider = provider;
        this.title = title;
        this.controller = controller;
        this.isStaticGui = isStaticGui;
        this.pagination = controller.getPagination();
        this.name = controller.getGuiId();
        this.rows = controller.getRows();
        this.columns = controller.getColumns();
        this.isCloseable = controller.isCloseable();
    }

    @Override
    public Gui getProvider() {
        return provider;
    }

    @Override
    public GuiController getController() {
        return controller;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public int getColumns() {
        return columns;
    }

    @Override
    public boolean isCloseable() {
        return isCloseable;
    }

    @Override
    public boolean isStaticGui() {
        return isStaticGui;
    }

    @Override
    public void open(Player holder) {
        validateOpening(holder);
    }

    @Override
    public void open(Player holder, int pageId) {
        if (validateOpening(holder)) return;
        if (pagination != null) {
            pagination.page(pageId);
        }
    }

    @Override
    public void open(Player holder, boolean forceSyncOpening) {
        Bukkit.getScheduler().runTask(GuiLibBukkit.getInstance(), () -> open(holder));
    }

    @Override
    public void open(Player holder, int pageId, boolean forceSyncOpening) {
        Bukkit.getScheduler().runTask(GuiLibBukkit.getInstance(), () -> open(holder, pageId));
    }

    @Override
    public void close(Player holder) {
        holder.closeInventory();
    }

    @Override
    public void close(Player holder, boolean forceSyncClosing) {
        Bukkit.getScheduler().runTask(GuiLibBukkit.getInstance(), () -> close(holder));
    }

    private boolean validateOpening(Player holder) {
        String permission = controller.getProperties().permission();

        if (permission != null && !permission.isEmpty() && !holder.hasPermission(permission)) {
            holder.sendMessage(MiniMessage.miniMessage().deserialize(
                    GuiLibBukkit.getInstance().getMessageFile().getNoPermissionMessage()));
            return true;
        }

        Inventory rawInventory = controller.getRawInventory();
        if (rawInventory == null) {
            return true;
        }
        holder.openInventory(rawInventory);
        holder.getPersistentDataContainer().set(getOpenGuiKey(), PersistentDataType.STRING, name);

        if (controller.getProperties().playSoundOnOpen()) {
            SoundUtils.playOpenSound(holder);
        }

        return false;
    }
}
