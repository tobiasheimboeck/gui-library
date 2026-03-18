package net.developertobi.guilib.api.gui;

import net.developertobi.guilib.api.item.GuiItem;
import net.developertobi.guilib.api.item.GuiPos;
import net.developertobi.guilib.api.pagination.GuiPagination;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.Map;

public interface GuiController {

    Gui getProvider();
    GuiProperties getProperties();

    int getSlotCount();
    boolean isCloseable();
    void setCloseable(boolean closeable);

    Map<GuiPos, GuiItem> getContents();
    GuiPagination getPagination();
    Inventory getRawInventory();
    void setRawInventory(Inventory rawInventory);

    String getOverriddenGuiId();
    void setOverriddenGuiId(String overriddenGuiId);
    int getOverriddenRows();
    void setOverriddenRows(int overriddenRows);
    int getOverriddenColumns();
    void setOverriddenColumns(int overriddenColumns);

    String getGuiId();
    int getRows();
    int getColumns();

    void placeholder(GuiPos pos, Material type);
    void placeholder(int row, int column, Material type);

    void setItem(GuiPos pos, GuiItem item);
    void setItem(int row, int column, GuiItem item);
    void addItem(GuiItem item);
    void addItemToRandomPosition(GuiItem item);
    void removeItem(String name);
    void removeItem(Material type);

    void fill(FillType fillType, GuiItem item, GuiPos... positions);
    void clearPosition(GuiPos pos);

    boolean isPositionTaken(GuiPos pos);
    GuiPos getPositionOfItem(GuiItem item);
    GuiPos getFirstEmptyPosition();

    GuiItem getItem(GuiPos pos);
    GuiItem getItem(int row, int column);
    GuiItem findFirstItemWithType(Material type);

    GuiPagination createPagination();

    void updateRawInventory();

    enum FillType {
        ROW,
        RECTANGLE,
        LEFT_BORDER,
        RIGHT_BORDER,
        TOP_BORDER,
        BOTTOM_BORDER,
        ALL_BORDERS
    }
}
