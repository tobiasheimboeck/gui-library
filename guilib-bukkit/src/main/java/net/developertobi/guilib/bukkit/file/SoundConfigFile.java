package net.developertobi.guilib.bukkit.file;

import org.bukkit.Sound;

public class SoundConfigFile implements GuiFile {

    private Sound onClick;
    private Sound onOpen;
    private Sound onClose;
    private Sound onPageSwitch;

    public SoundConfigFile() {
        this(null, null, null, null);
    }

    public SoundConfigFile(Sound onClick, Sound onOpen, Sound onClose, Sound onPageSwitch) {
        this.onClick = onClick;
        this.onOpen = onOpen;
        this.onClose = onClose;
        this.onPageSwitch = onPageSwitch;
    }

    public Sound getOnClick() {
        return onClick;
    }

    public void setOnClick(Sound onClick) {
        this.onClick = onClick;
    }

    public Sound getOnOpen() {
        return onOpen;
    }

    public void setOnOpen(Sound onOpen) {
        this.onOpen = onOpen;
    }

    public Sound getOnClose() {
        return onClose;
    }

    public void setOnClose(Sound onClose) {
        this.onClose = onClose;
    }

    public Sound getOnPageSwitch() {
        return onPageSwitch;
    }

    public void setOnPageSwitch(Sound onPageSwitch) {
        this.onPageSwitch = onPageSwitch;
    }
}
