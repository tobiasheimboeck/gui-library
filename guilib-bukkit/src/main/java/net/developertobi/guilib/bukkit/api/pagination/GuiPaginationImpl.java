package net.developertobi.guilib.bukkit.api.pagination;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.developertobi.guilib.api.gui.GuiController;
import net.developertobi.guilib.api.item.GuiItem;
import net.developertobi.guilib.api.item.GuiPos;
import net.developertobi.guilib.api.pagination.GuiPagination;
import net.developertobi.guilib.bukkit.utils.SoundUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GuiPaginationImpl implements GuiPagination {

    private final GuiController controller;
    private final List<GuiPos> positions = new ArrayList<>();
    private final Multimap<Integer, GuiItem> items = ArrayListMultimap.create();

    private int currentPageId = 0;
    private int itemsPerPage = 9;

    public GuiPaginationImpl(GuiController controller) {
        this.controller = controller;
    }

    @Override
    public List<GuiPos> getPositions() {
        return positions;
    }

    @Override
    public Multimap<Integer, GuiItem> getItems() {
        return items;
    }

    @Override
    public int getLastPageId() {
        List<Integer> pageIds = new ArrayList<>(items.keySet());
        pageIds.sort(Integer::compareTo);
        return pageIds.isEmpty() ? 0 : pageIds.get(pageIds.size() - 1);
    }

    @Override
    public int getPageAmount() {
        return items.keySet().size();
    }

    @Override
    public int getCurrentPageId() {
        return currentPageId;
    }

    @Override
    public boolean isFirstPage() {
        return currentPageId == 0;
    }

    @Override
    public boolean isLastPage() {
        List<Integer> pageIds = new ArrayList<>(items.keySet());
        pageIds.sort(Integer::compareTo);
        return pageIds.isEmpty() || currentPageId == pageIds.get(pageIds.size() - 1);
    }

    @Override
    public void page(int pageId) {
        List<Integer> pageIds = new ArrayList<>(items.keySet());
        pageIds.sort(Integer::compareTo);
        this.currentPageId = (pageIds.size() <= pageId || pageId < 0) ? 0 : pageId;
        refreshPage();
    }

    @Override
    public void toFirstPage() {
        this.currentPageId = 0;
        refreshPage();
    }

    @Override
    public void toLastPage() {
        if (items.isEmpty()) {
            this.currentPageId = 0;
        } else {
            List<Integer> pageIds = new ArrayList<>(items.keySet());
            pageIds.sort(Integer::compareTo);
            this.currentPageId = pageIds.get(pageIds.size() - 1);
        }
        refreshPage();
    }

    @Override
    public void toNextPage() {
        if (items.isEmpty()) return;
        if (isLastPage()) return;
        this.currentPageId += 1;
        refreshPage();
    }

    @Override
    public void toPreviousPage() {
        if (items.isEmpty()) return;
        if (isFirstPage()) return;
        this.currentPageId -= 1;
        refreshPage();
    }

    @Override
    public void setItemField(int startRow, int startColumn, int endRow, int endColumn) {
        int numRows = controller.getRows();
        int numColumns = controller.getColumns();

        for (int row = startRow; row < Math.min(numRows, endRow + 1); row++) {
            for (int column = startColumn; column < Math.min(numColumns, endColumn + 1); column++) {
                positions.add(GuiPos.of(row, column));
            }
        }
    }

    @Override
    public void distributeItems(List<GuiItem> itemsToDistribute) {
        items.clear();
        int pageIndex = 0;

        for (int i = 0; i < itemsToDistribute.size(); i++) {
            if (i % itemsPerPage == 0) {
                pageIndex = i / itemsPerPage;
            }
            items.put(pageIndex, itemsToDistribute.get(i));
        }

        refreshPage();
    }

    @Override
    public void limitItemsPerPage(int amount) {
        this.itemsPerPage = amount;
    }

    @Override
    public void refreshPage() {
        for (GuiPos currentPosition : positions) {
            if (!controller.isPositionTaken(currentPosition)) continue;
            controller.clearPosition(currentPosition);
        }

        List<GuiItem> itemsForNextPage = new ArrayList<>(items.get(currentPageId));
        int itemIndex = 0;

        for (GuiPos currentPosition : positions) {
            if (itemIndex >= itemsForNextPage.size()) break;
            if (controller.isPositionTaken(currentPosition)) continue;
            controller.setItem(currentPosition, itemsForNextPage.get(itemIndex));
            itemIndex++;
        }

        controller.updateRawInventory();

        if (controller.getProperties().playSoundOnPageSwitch()) {
            Object holder = controller.getRawInventory() != null ? controller.getRawInventory().getHolder() : null;
            if (holder instanceof Player player) {
                SoundUtils.playSwitchPageSound(player);
            }
        }
    }
}
