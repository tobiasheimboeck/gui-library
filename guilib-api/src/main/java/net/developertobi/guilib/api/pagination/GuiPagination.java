package net.developertobi.guilib.api.pagination;

import com.google.common.collect.Multimap;
import net.developertobi.guilib.api.item.GuiItem;
import net.developertobi.guilib.api.item.GuiPos;

import java.util.ArrayList;
import java.util.List;

public interface GuiPagination {

    List<GuiPos> getPositions();
    Multimap<Integer, GuiItem> getItems();

    default List<GuiItem> getPaginationItems() {
        return new ArrayList<>(getItems().values());
    }

    int getLastPageId();
    int getPageAmount();
    int getCurrentPageId();

    boolean isFirstPage();
    boolean isLastPage();

    void page(int pageId);

    void toFirstPage();
    void toLastPage();

    void toNextPage();
    void toPreviousPage();

    void setItemField(int startRow, int startColumn, int endRow, int endColumn);
    void distributeItems(List<GuiItem> items);
    void limitItemsPerPage(int amount);
    void refreshPage();
}
