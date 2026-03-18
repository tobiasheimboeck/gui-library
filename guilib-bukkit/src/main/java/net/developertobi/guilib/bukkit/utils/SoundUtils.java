package net.developertobi.guilib.bukkit.utils;

import net.developertobi.guilib.bukkit.GuiLibBukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class SoundUtils {

    private static Sound getClickSound() {
        Sound s = GuiLibBukkit.getInstance().getSoundConfigFile().getOnClick();
        return s != null ? s : Sound.BLOCK_NOTE_BLOCK_HAT;
    }

    private static Sound getOpenSound() {
        Sound s = GuiLibBukkit.getInstance().getSoundConfigFile().getOnOpen();
        return s != null ? s : Sound.BLOCK_COPPER_DOOR_OPEN;
    }

    private static Sound getCloseSound() {
        Sound s = GuiLibBukkit.getInstance().getSoundConfigFile().getOnClose();
        return s != null ? s : Sound.BLOCK_COPPER_DOOR_CLOSE;
    }

    private static Sound getPageSwitchSound() {
        Sound s = GuiLibBukkit.getInstance().getSoundConfigFile().getOnPageSwitch();
        return s != null ? s : Sound.BLOCK_COPPER_BULB_TURN_ON;
    }

    public static void playClickSound(Player player) {
        playSound(player, getClickSound());
    }

    public static void playOpenSound(Player player) {
        playSound(player, getOpenSound());
    }

    public static void playCloseSound(Player player) {
        playSound(player, getCloseSound());
    }

    public static void playSwitchPageSound(Player player) {
        playSound(player, getPageSwitchSound());
    }

    private static void playSound(Player player, Sound sound) {
        if (sound == null) return;
        player.playSound(player.getLocation(), sound, 1f, 1f);
    }
}
