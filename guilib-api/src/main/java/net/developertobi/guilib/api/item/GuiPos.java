package net.developertobi.guilib.api.item;

public record GuiPos(int row, int column) {

    public static GuiPos of(int row, int column) {
        return new GuiPos(row, column);
    }
}
