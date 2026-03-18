package net.developertobi.guilib.bukkit.api.gui;

import net.developertobi.guilib.api.GuiProvider;
import net.developertobi.guilib.api.gui.Gui;
import net.developertobi.guilib.api.gui.GuiController;
import net.developertobi.guilib.api.gui.GuiProperties;
import net.developertobi.guilib.api.item.GuiPos;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Consumer;

@GuiProperties(id = "confirmation_inv", rows = 3, columns = 9, closeable = true)
public class ConfirmationGui implements Gui {

    private final ItemStack displayItem;
    private final Consumer<ItemStack> onAccept;
    private final Consumer<ItemStack> onDeny;

    public ConfirmationGui(ItemStack displayItem, Consumer<ItemStack> onAccept, Consumer<ItemStack> onDeny) {
        this.displayItem = displayItem;
        this.onAccept = onAccept;
        this.onDeny = onDeny;
    }

    @Override
    public void init(Player player, GuiController controller) {
        ItemStack acceptItem = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        acceptItem.editMeta(meta -> meta.displayName(Component.text("✔", NamedTextColor.GREEN)));

        ItemStack denyItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        denyItem.editMeta(meta -> meta.displayName(Component.text("✗", NamedTextColor.RED)));

        controller.fill(
                GuiController.FillType.RECTANGLE,
                GuiProvider.getApi().of(acceptItem, (pos, guiItem, event) -> onAccept.accept(acceptItem)),
                GuiPos.of(0, 0),
                GuiPos.of(2, 2)
        );

        controller.fill(
                GuiController.FillType.RECTANGLE,
                GuiProvider.getApi().of(denyItem, (pos, guiItem, event) -> onDeny.accept(denyItem)),
                GuiPos.of(0, 6),
                GuiPos.of(2, 8)
        );

        controller.setItem(1, 4, GuiProvider.getApi().of(displayItem));
    }
}
