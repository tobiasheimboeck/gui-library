package net.developertobi.guilib.bukkit.api.gui;

import net.developertobi.guilib.api.GuiProvider;
import net.developertobi.guilib.api.gui.Gui;
import net.developertobi.guilib.api.gui.GuiController;
import net.developertobi.guilib.api.gui.GuiProperties;
import net.developertobi.guilib.api.item.GuiItem;
import net.developertobi.guilib.api.item.GuiPos;
import net.developertobi.guilib.api.pagination.GuiPagination;
import net.developertobi.guilib.api.utils.MathUtils;
import net.developertobi.guilib.bukkit.api.pagination.GuiPaginationImpl;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class GuiControllerImpl implements GuiController {

    private final Gui provider;
    private final GuiProperties properties;
    private final int slotCount;
    private boolean closeable;
    private final Map<GuiPos, GuiItem> contents = new HashMap<>();
    private GuiPagination pagination;
    private Inventory rawInventory;
    private String overriddenGuiId;
    private int overriddenRows;
    private int overriddenColumns;
    private GuiPos inputAreaStart;
    private GuiPos inputAreaEnd;

    public GuiControllerImpl(Gui provider) {
        this.provider = provider;
        GuiProperties ann = provider.getClass().getAnnotation(GuiProperties.class);
        if (ann == null) {
            throw new IllegalArgumentException("Gui provider must have @GuiProperties annotation: " + provider.getClass().getName());
        }
        this.properties = ann;
        this.slotCount = properties.rows() * properties.columns();
        this.closeable = properties.closeable();
        this.overriddenGuiId = null;
        this.overriddenRows = 0;
        this.overriddenColumns = 0;
    }

    @Override
    public Gui getProvider() {
        return provider;
    }

    @Override
    public GuiProperties getProperties() {
        return properties;
    }

    @Override
    public int getSlotCount() {
        return slotCount;
    }

    @Override
    public boolean isCloseable() {
        return closeable;
    }

    @Override
    public void setCloseable(boolean closeable) {
        this.closeable = closeable;
    }

    @Override
    public Map<GuiPos, GuiItem> getContents() {
        return contents;
    }

    @Override
    public GuiPagination getPagination() {
        return pagination;
    }

    public void setPagination(GuiPagination pagination) {
        this.pagination = pagination;
    }

    @Override
    public Inventory getRawInventory() {
        return rawInventory;
    }

    @Override
    public void setRawInventory(Inventory rawInventory) {
        this.rawInventory = rawInventory;
    }

    @Override
    public String getOverriddenGuiId() {
        return overriddenGuiId;
    }

    @Override
    public void setOverriddenGuiId(String overriddenGuiId) {
        this.overriddenGuiId = overriddenGuiId;
    }

    @Override
    public int getOverriddenRows() {
        return overriddenRows;
    }

    @Override
    public void setOverriddenRows(int overriddenRows) {
        this.overriddenRows = overriddenRows;
    }

    @Override
    public int getOverriddenColumns() {
        return overriddenColumns;
    }

    @Override
    public void setOverriddenColumns(int overriddenColumns) {
        this.overriddenColumns = overriddenColumns;
    }

    @Override
    public String getGuiId() {
        return overriddenGuiId != null ? overriddenGuiId : properties.id();
    }

    @Override
    public int getRows() {
        return overriddenRows != 0 ? overriddenRows : properties.rows();
    }

    @Override
    public int getColumns() {
        return overriddenColumns != 0 ? overriddenColumns : properties.columns();
    }

    @Override
    public void placeholder(GuiPos pos, Material type) {
        setItem(pos, GuiProvider.getApi().placeholder(type));
    }

    @Override
    public void placeholder(int row, int column, Material type) {
        setItem(row, column, GuiProvider.getApi().placeholder(type));
    }

    @Override
    public void setInputArea(GuiPos start, GuiPos end) {
        this.inputAreaStart = start;
        this.inputAreaEnd = end;
    }

    @Override
    public boolean isInInputArea(GuiPos pos) {
        if (inputAreaStart == null || inputAreaEnd == null) return false;
        int minRow = Math.min(inputAreaStart.row(), inputAreaEnd.row());
        int maxRow = Math.max(inputAreaStart.row(), inputAreaEnd.row());
        int minCol = Math.min(inputAreaStart.column(), inputAreaEnd.column());
        int maxCol = Math.max(inputAreaStart.column(), inputAreaEnd.column());
        return pos.row() >= minRow && pos.row() <= maxRow
                && pos.column() >= minCol && pos.column() <= maxCol;
    }

    @Override
    public void setItem(GuiPos pos, GuiItem item) {
        contents.put(pos, item);
    }

    @Override
    public void setItem(int row, int column, GuiItem item) {
        contents.put(GuiPos.of(row, column), item);
    }

    @Override
    public void addItem(GuiItem item) {
        GuiPos emptyPosition = getFirstEmptyPosition();
        if (emptyPosition == null) return;
        setItem(emptyPosition, item);
    }

    @Override
    public void addItemToRandomPosition(GuiItem item) {
        int randomSlotIndex = ThreadLocalRandom.current().nextInt(slotCount);
        GuiPos randomPosition = MathUtils.slotToPosition(randomSlotIndex, getColumns());
        if (isPositionTaken(randomPosition)) return;
        setItem(randomPosition, item);
    }

    @Override
    public void removeItem(String name) {
        PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
        for (Map.Entry<GuiPos, GuiItem> entry : contents.entrySet()) {
            GuiItem guiItem = entry.getValue();
            if (guiItem == null) continue;

            if (!serializer.serialize(guiItem.getItem().displayName()).equalsIgnoreCase(name)) continue;

            contents.put(entry.getKey(), null);
            if (rawInventory != null) {
                rawInventory.remove(guiItem.getItem());
            }
        }
    }

    @Override
    public void removeItem(Material type) {
        for (Map.Entry<GuiPos, GuiItem> entry : contents.entrySet()) {
            GuiItem guiItem = entry.getValue();
            if (guiItem == null) continue;
            if (guiItem.getItem().getType() != type) continue;

            contents.put(entry.getKey(), null);
        }
    }

    @Override
    public void fill(FillType fillType, GuiItem item, GuiPos... positions) {
        int rows = getRows();
        int columns = getColumns();

        switch (fillType) {
            case ROW -> {
                if (positions.length > 1) {
                    throw new IllegalArgumentException("To fill a row only 1 position is allowed. Used positions: " + positions.length);
                }
                int startSlot = MathUtils.positionToSlot(positions[0], getColumns());
                for (int currentSlot = startSlot; currentSlot < startSlot + columns; currentSlot++) {
                    setItem(MathUtils.slotToPosition(currentSlot, columns), item);
                }
            }

            case RECTANGLE -> {
                if (positions.length != 2) {
                    throw new IllegalArgumentException("Only two positions are allowed to create a rectangle!");
                }
                GuiPos fromPos = positions[0];
                GuiPos toPos = positions[1];
                int fromRow = fromPos.row();
                int fromColumn = fromPos.column();
                int toRow = toPos.row();
                int toColumn = toPos.column();

                for (int row = fromRow; row <= toRow; row++) {
                    for (int col = fromColumn; col <= toColumn; col++) {
                        setItem(GuiPos.of(row, col), item);
                    }
                }
            }

            case LEFT_BORDER -> {
                int currentSlot = 0;
                while (currentSlot < slotCount) {
                    GuiPos currentPosition = MathUtils.slotToPosition(currentSlot, columns);
                    setItem(currentPosition, item);
                    currentSlot += rows;
                }
            }

            case RIGHT_BORDER -> {
                int lastColumnStart = rows - 1;
                int lastColumnEnd = slotCount - 1;
                int currentSlot = lastColumnStart;
                while (currentSlot <= lastColumnEnd) {
                    GuiPos currentPos = MathUtils.slotToPosition(currentSlot, columns);
                    GuiPos nextPos = MathUtils.nextPositionFromSlot(currentSlot, columns);

                    if (currentPos.row() == nextPos.row()) {
                        currentSlot += rows;
                        continue;
                    }

                    GuiPos currentPosition = MathUtils.slotToPosition(currentSlot, columns);
                    setItem(currentPosition, item);
                    currentSlot += rows;
                }
            }

            case TOP_BORDER -> {
                for (int currentSlot = 0; currentSlot < columns; currentSlot++) {
                    GuiPos currentPosition = MathUtils.slotToPosition(currentSlot, columns);
                    setItem(currentPosition, item);
                }
            }

            case BOTTOM_BORDER -> {
                int size = slotCount;
                int firstColumnInLastRow = size - columns;
                for (int currentSlot = firstColumnInLastRow; currentSlot < size; currentSlot++) {
                    GuiPos currentPosition = MathUtils.slotToPosition(currentSlot, columns);
                    setItem(currentPosition, item);
                }
            }

            case ALL_BORDERS -> {
                fill(FillType.TOP_BORDER, item);
                fill(FillType.RIGHT_BORDER, item);
                fill(FillType.BOTTOM_BORDER, item);
                fill(FillType.LEFT_BORDER, item);
            }
        }
    }

    @Override
    public void clearPosition(GuiPos pos) {
        contents.put(pos, null);
        if (rawInventory != null) {
            rawInventory.clear(MathUtils.positionToSlot(pos, getColumns()));
        }
    }

    @Override
    public boolean isPositionTaken(GuiPos pos) {
        return contents.get(pos) != null;
    }

    @Override
    public GuiPos getPositionOfItem(GuiItem item) {
        for (Map.Entry<GuiPos, GuiItem> entry : contents.entrySet()) {
            if (entry.getValue() == item) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public GuiPos getFirstEmptyPosition() {
        int columns = getColumns();
        for (int i = 0; i < slotCount; i++) {
            GuiPos position = MathUtils.slotToPosition(i, columns);
            if (contents.get(position) == null) {
                return position;
            }
        }
        return null;
    }

    @Override
    public GuiItem getItem(GuiPos pos) {
        return contents.get(pos);
    }

    @Override
    public GuiItem getItem(int row, int column) {
        return contents.get(GuiPos.of(row, column));
    }

    @Override
    public GuiItem findFirstItemWithType(Material type) {
        for (int slot = 0; slot < slotCount; slot++) {
            GuiPos currentPosition = MathUtils.slotToPosition(slot, getColumns());
            GuiItem guiItem = contents.get(currentPosition);
            if (guiItem == null) continue;
            if (guiItem.getItem() == null) continue;
            if (guiItem.getItem().getType() != type) continue;
            return guiItem;
        }
        return null;
    }

    @Override
    public GuiPagination createPagination() {
        if (pagination == null) {
            pagination = new GuiPaginationImpl(this);
        }
        return pagination;
    }

    @Override
    public void updateRawInventory() {
        for (Map.Entry<GuiPos, GuiItem> entry : contents.entrySet()) {
            GuiItem guiItem = entry.getValue();
            if (guiItem == null) continue;
            if (rawInventory != null) {
                rawInventory.setItem(MathUtils.positionToSlot(entry.getKey(), getColumns()), guiItem.getItem());
            }
        }
    }
}
