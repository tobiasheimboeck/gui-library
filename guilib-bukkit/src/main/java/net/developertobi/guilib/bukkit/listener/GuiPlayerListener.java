package net.developertobi.guilib.bukkit.listener;

import net.developertobi.guilib.api.GuiProvider;
import net.developertobi.guilib.api.gui.GuiHandler;
import net.developertobi.guilib.api.gui.GuiView;
import net.developertobi.guilib.api.item.GuiItem;
import net.developertobi.guilib.api.utils.MathUtils;
import net.developertobi.guilib.bukkit.GuiLibBukkit;
import net.developertobi.guilib.bukkit.utils.SoundUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;

public class GuiPlayerListener implements Listener {

    private static NamespacedKey openGuiKey;

    private static NamespacedKey getOpenGuiKey() {
        if (openGuiKey == null) {
            openGuiKey = new NamespacedKey(GuiLibBukkit.getInstance(), "open-gui");
        }
        return openGuiKey;
    }

    private final GuiLibBukkit plugin;
    private final GuiHandler guiHandler;

    public GuiPlayerListener(GuiLibBukkit plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.guiHandler = GuiProvider.getApi().getGuiHandler();
    }

    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!player.getPersistentDataContainer().has(getOpenGuiKey(), PersistentDataType.STRING)) return;
        if (event.getClickedInventory() != player.getOpenInventory().getTopInventory()) return;
        if (!validateGui(player, event.getView().title())) return;

        GuiView gui = guiHandler.getView(player, getOpenGuiName(player));
        if (gui == null) return;
        var position = MathUtils.slotToPosition(event.getSlot(), gui.getColumns());

        if (gui.getController().isInInputArea(position)) {
            return;
        }

        event.setCancelled(true);

        GuiItem currentItem = gui.getController().getItem(position);
        if (currentItem == null) return;
        currentItem.runAction(position, currentItem, event);

        if (GuiLibBukkit.getInstance().getSoundConfigFile().getOnClick() != null
                && gui.getController().getProperties().playSoundOnClick()) {
            SoundUtils.playClickSound(player);
        }
    }

    @EventHandler
    public void onPlayerInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof Player)) return;
        Player player = (Player) event.getPlayer();

        if (!player.getPersistentDataContainer().has(getOpenGuiKey(), PersistentDataType.STRING)) return;
        if (!validateGui(player, event.getView().title())) return;

        GuiView gui = guiHandler.getView(player, getOpenGuiName(player));
        if (gui == null) return;

        if (!gui.isCloseable()) {
            Bukkit.getScheduler().runTask(plugin, () -> gui.open(player));
        } else {
            player.getPersistentDataContainer().remove(getOpenGuiKey());
            if (gui.getController().getProperties().playSoundOnClose()) {
                SoundUtils.playCloseSound(player);
            }
            if (gui.isStaticGui()) {
                guiHandler.removeCachedView(player, gui);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        guiHandler.clearCachedViews(event.getPlayer());
    }

    private boolean validateGui(Player player, Component title) {
        String openGuiTitle = PlainTextComponentSerializer.plainText().serialize(title);

        String guiName = getOpenGuiName(player);
        GuiView gui = guiHandler.getView(player, guiName);
        if (gui == null) return false;
        String possibleGuiTitle = PlainTextComponentSerializer.plainText().serialize(gui.getTitle());

        return possibleGuiTitle.equalsIgnoreCase(openGuiTitle);
    }

    private String getOpenGuiName(Player player) {
        String name = player.getPersistentDataContainer().get(getOpenGuiKey(), PersistentDataType.STRING);
        return name != null ? name : "";
    }
}
