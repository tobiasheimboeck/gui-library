package net.developertobi.guilib.bukkit.file;

public class MessageFile implements GuiFile {

    private String noPermissionMessage;

    public MessageFile() {
        this("<red>You don't have the permission to open this GUI!");
    }

    public MessageFile(String noPermissionMessage) {
        this.noPermissionMessage = noPermissionMessage;
    }

    public String getNoPermissionMessage() {
        return noPermissionMessage;
    }

    public void setNoPermissionMessage(String noPermissionMessage) {
        this.noPermissionMessage = noPermissionMessage;
    }
}
