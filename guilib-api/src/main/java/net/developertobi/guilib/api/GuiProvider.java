package net.developertobi.guilib.api;

public class GuiProvider {

    private static GuiApi api;

    private GuiProvider() {
    }

    public static void register(GuiApi api) {
        GuiProvider.api = api;
    }

    public static GuiApi getApi() {
        return api;
    }
}
