package net.developertobi.guilib.api.gui;

import com.google.common.collect.Multimap;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface GuiHandler {

    Multimap<Player, GuiView> getInventories();

    void openStaticView(Player holder, Component title, Gui provider, boolean forceSyncOpening);

    default void cacheView(Player holder, Component title, Gui provider) {
        cacheView(holder, title, provider, false);
    }

    GuiView cacheView(Player holder, Component title, Gui provider, boolean staticGui);

    void updateCachedView(Player holder, String viewId);

    void clearCachedViews(Player holder);

    void removeCachedView(Player holder, GuiView view);

    GuiView getView(Player holder, String name);
}
