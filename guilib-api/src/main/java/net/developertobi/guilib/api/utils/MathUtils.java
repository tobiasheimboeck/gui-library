package net.developertobi.guilib.api.utils;

import net.developertobi.guilib.api.item.GuiPos;

public final class MathUtils {

    public static GuiPos slotToPosition(int slot, int columns) {
        return GuiPos.of(slot / columns, slot % columns);
    }

    public static int positionToSlot(GuiPos pos, int columns) {
        return pos.row() * columns + pos.column();
    }

    public static GuiPos nextPositionFromSlot(int slot, int columns) {
        return slotToPosition(slot + 1, columns);
    }
}
