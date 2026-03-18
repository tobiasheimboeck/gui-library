package net.developertobi.guilib.bukkit.api;

import net.developertobi.guilib.api.GuiApi;
import net.developertobi.guilib.api.GuiItemAction;
import net.developertobi.guilib.api.gui.GuiHandler;
import net.developertobi.guilib.api.item.GuiItem;
import net.developertobi.guilib.api.item.GuiPos;
import net.developertobi.guilib.api.pagination.GuiPagination;
import net.developertobi.guilib.api.GuiHelper;
import net.developertobi.guilib.bukkit.api.gui.ConfirmationGui;
import net.developertobi.guilib.bukkit.api.gui.GuiHandlerImpl;
import net.developertobi.guilib.bukkit.api.item.GuiItemImpl;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class GuiApiImpl implements GuiApi {

    private final GuiHandler guiHandler = new GuiHandlerImpl();

    @Override
    public GuiHandler getGuiHandler() {
        return guiHandler;
    }

    @Override
    public GuiItem placeholder(Material type) {
        String itemId = UUID.randomUUID().toString().split("-")[0];
        return of(makeItemStack(type, itemId));
    }

    @Override
    public GuiItem nextPage(ItemStack item, GuiPagination pagination) {
        return of(item, (pos, guiItem, event) -> pagination.toNextPage());
    }

    @Override
    public GuiItem previousPage(ItemStack item, GuiPagination pagination) {
        return of(item, (pos, guiItem, event) -> pagination.toPreviousPage());
    }

    @Override
    public GuiItem navigator(ItemStack item, String guiKey) {
        return of(item, (pos, guiItem, event) -> {
            Player holder = (Player) event.getWhoClicked();
            var gui = GuiHelper.getGui(holder, guiKey);
            if (gui == null) return;

            holder.closeInventory();
            gui.open(holder);
        });
    }

    @Override
    public GuiItem of(ItemStack item) {
        return new GuiItemImpl(item);
    }

    @Override
    public GuiItem of(ItemStack item, GuiItemAction action) {
        return new GuiItemImpl(item, action);
    }

    @Override
    public void openConfirmationGui(Player holder, Component title, ItemStack displayItem,
                                          java.util.function.Consumer<ItemStack> onAccept,
                                          java.util.function.Consumer<ItemStack> onDeny) {
        guiHandler.openStaticView(
                holder,
                title,
                new ConfirmationGui(displayItem, onAccept, onDeny),
                true
        );
    }

    private ItemStack makeItemStack(Material type, String itemId) {
        ItemStack itemStack = new ItemStack(type);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.displayName(Component.text(" "));

            NamespacedKey namespacedKey = new NamespacedKey("item", "itemid");
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            if (!dataContainer.has(namespacedKey, PersistentDataType.STRING)) {
                dataContainer.set(namespacedKey, PersistentDataType.STRING, itemId);
            }
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
}
