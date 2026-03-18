package net.developertobi.guilib.api.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface GuiView {

    Gui getProvider();
    GuiController getController();

    String getName();
    Component getTitle();

    int getRows();
    int getColumns();

    boolean isCloseable();
    boolean isStaticGui();

    void open(Player holder);
    void open(Player holder, int pageId);

    void open(Player holder, boolean forceSyncOpening);
    void open(Player holder, int pageId, boolean forceSyncOpening);

    void close(Player holder);
    void close(Player holder, boolean forceSyncClosing);
}
