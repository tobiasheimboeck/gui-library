package net.developertobi.guilib.bukkit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.developertobi.guilib.api.GuiApi;
import net.developertobi.guilib.api.GuiProvider;
import net.developertobi.guilib.bukkit.api.GuiApiImpl;
import net.developertobi.guilib.bukkit.file.MessageFile;
import net.developertobi.guilib.bukkit.file.SoundConfigFile;
import net.developertobi.guilib.bukkit.listener.GuiPlayerListener;
import net.developertobi.guilib.bukkit.utils.FileUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class GuiLibBukkit extends JavaPlugin {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private static GuiLibBukkit instance;

    private SoundConfigFile soundConfigFile;
    private MessageFile messageFile;

    @Override
    public void onEnable() {
        instance = this;

        this.soundConfigFile = createOrLoadSoundConfigFile();
        this.messageFile = createOrLoadMessageFile();

        GuiApi guiApi = new GuiApiImpl();
        GuiProvider.register(guiApi);

        new GuiPlayerListener(this);
    }

    private SoundConfigFile createOrLoadSoundConfigFile() {
        return FileUtils.createOrLoadFile(getDataFolder().toPath(), "global", "sounds", SoundConfigFile.class, new SoundConfigFile());
    }

    private MessageFile createOrLoadMessageFile() {
        return FileUtils.createOrLoadFile(getDataFolder().toPath(), "global", "messages", MessageFile.class, new MessageFile());
    }

    public static Gson getGson() {
        return GSON;
    }

    public static GuiLibBukkit getInstance() {
        return instance;
    }

    public SoundConfigFile getSoundConfigFile() {
        return soundConfigFile;
    }

    public MessageFile getMessageFile() {
        return messageFile;
    }
}
