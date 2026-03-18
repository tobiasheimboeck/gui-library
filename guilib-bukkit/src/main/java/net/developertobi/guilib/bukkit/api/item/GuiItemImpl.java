package net.developertobi.guilib.bukkit.api.item;

import net.developertobi.guilib.api.GuiItemAction;
import net.developertobi.guilib.api.gui.GuiController;
import net.developertobi.guilib.api.item.GuiItem;
import net.developertobi.guilib.api.item.GuiPos;
import net.developertobi.guilib.api.item.ItemEnchantment;
import net.developertobi.guilib.api.utils.MathUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class GuiItemImpl implements GuiItem {

    private ItemStack item;
    private final GuiItemAction action;
    private final String itemId = UUID.randomUUID().toString().split("-")[0];
    private Instant lastClickTime = Instant.EPOCH;
    private final long cooldownMillis = 250;

    public GuiItemImpl(ItemStack item) {
        this(item, null);
    }

    public GuiItemImpl(ItemStack item, GuiItemAction action) {
        this.item = item;
        this.action = action;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public void setItem(ItemStack item) {
        this.item = item;
    }

    String getItemId() {
        return itemId;
    }

    @Override
    public void runAction(GuiPos position, GuiItem guiItem, InventoryClickEvent event) {
        Instant now = Instant.now();

        if (!lastClickTime.equals(Instant.EPOCH) && (now.toEpochMilli() - lastClickTime.toEpochMilli()) < cooldownMillis) {
            event.setCancelled(true);
            return;
        }

        lastClickTime = now;
        if (action != null) {
            action.run(position, guiItem, event);
        }
    }

    @Override
    public void update(GuiController controller, GuiItem.Modification modification, Object... values) {
        if (values.length > 1) {
            throw new UnsupportedOperationException("There are no more than one value allowed! Current size: " + values.length);
        }

        GuiPos guiPosition = controller.getPositionOfItem(this);
        ItemStack modifiableItem;
        ItemStack extraItem = null;

        if (guiPosition != null) {
            int slot = MathUtils.positionToSlot(guiPosition, controller.getColumns());
            Inventory rawInventory = controller.getRawInventory();
            if (rawInventory == null) {
                throw new IllegalStateException("Raw inventory is null");
            }
            modifiableItem = rawInventory.getItem(slot);
            if (modifiableItem == null) {
                modifiableItem = this.item;
            }
        } else {
            modifiableItem = this.item;
        }

        if (controller.getPagination() != null) {
            for (GuiItem paginationItem : controller.getPagination().getItems().values()) {
                if (paginationItem instanceof GuiItemImpl impl && impl.getItemId().equals(this.itemId)) {
                    extraItem = impl.getItem();
                    break;
                }
            }
        }

        Object newValue = values[0];

        switch (modification) {
            case TYPE -> {
                if (!(newValue instanceof Material material)) {
                    throw new UnsupportedOperationException("'newValue' is not a Material!");
                }

                modifiableItem = modifiableItem.withType(material);
                this.item = modifiableItem;

                if (guiPosition != null) {
                    int slot = MathUtils.positionToSlot(guiPosition, controller.getColumns());
                    Inventory rawInventory = controller.getRawInventory();
                    if (rawInventory != null) {
                        rawInventory.setItem(slot, modifiableItem);
                    }
                }

                if (extraItem != null) {
                    extraItem = extraItem.withType(material);
                }
                if (extraItem != null && controller.getPagination() != null) {
                    for (GuiItem paginationItem : controller.getPagination().getItems().values()) {
                        if (paginationItem instanceof GuiItemImpl impl && impl.getItemId().equals(this.itemId)) {
                            impl.setItem(extraItem);
                            break;
                        }
                    }
                }
            }

            case DISPLAY_NAME -> {
                if (!(newValue instanceof Component component)) {
                    throw new UnsupportedOperationException("'newValue' is not a Component!");
                }

                modifiableItem.editMeta(meta -> meta.displayName(component));
                if (extraItem != null) {
                    extraItem.editMeta(meta -> meta.displayName(component));
                }
            }

            case LORE -> {
                if (!(newValue instanceof List<?> list)) {
                    throw new UnsupportedOperationException("'newValue' is not a List!");
                }

                @SuppressWarnings("unchecked")
                List<Component> lore = (List<Component>) list;
                modifiableItem.editMeta(meta -> meta.lore(lore));
                if (extraItem != null) {
                    extraItem.editMeta(meta -> meta.lore(lore));
                }
            }

            case AMOUNT -> {
                if (!(newValue instanceof Integer amount)) {
                    throw new UnsupportedOperationException("'newValue' is not an Integer!");
                }

                modifiableItem.setAmount(amount);
                if (extraItem != null) {
                    extraItem.setAmount(amount);
                }
            }

            case INCREMENT -> {
                if (!(newValue instanceof Integer increment)) {
                    throw new UnsupportedOperationException("'newValue' is not an Integer!");
                }

                modifiableItem.setAmount(modifiableItem.getAmount() + increment);
                if (extraItem != null) {
                    extraItem.setAmount(extraItem.getAmount() + increment);
                }
            }

            case ENCHANTMENTS -> {
                if (!(newValue instanceof ItemEnchantment enchantment)) {
                    throw new UnsupportedOperationException("'newValue' is not an ItemEnchantment!");
                }

                enchantment.performAction(modifiableItem);
                if (extraItem != null) {
                    enchantment.performAction(extraItem);
                }
            }

            case GLOWING -> {
                if (!(newValue instanceof Boolean glowing)) {
                    throw new UnsupportedOperationException("'newValue' is not a Boolean!");
                }

                modifiableItem.editMeta(meta -> meta.setEnchantmentGlintOverride(glowing));
                if (extraItem != null) {
                    extraItem.editMeta(meta -> meta.setEnchantmentGlintOverride(glowing));
                }
            }
        }
    }
}
