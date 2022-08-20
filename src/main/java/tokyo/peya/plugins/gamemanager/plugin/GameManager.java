package tokyo.peya.plugins.gamemanager.plugin;

import net.kunmc.lab.peyangpaperutils.lib.command.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import tokyo.peya.plugins.gamemanager.GameManagerAPI;

public final class GameManager extends JavaPlugin
{
    // Effectively final.
    private static GameManagerAPI API;

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private CommandManager mainCommand;  // We need to keep this reference to prevent the plugin from being unloaded.

    public static @NotNull GameManagerAPI getGameManager()
    {
        return API;
    }

    public static void setGameManager(GameManagerAPI INSTANCE)
    {
        if (GameManager.API != null)
            throw new IllegalStateException("INSTANCE is already set.");
        else
            GameManager.API = INSTANCE;
    }

    @Override
    public void onEnable()
    {
        setGameManager(new GameManagerAPI(this));

        this.mainCommand = new CommandManager(this, "gamemanager", "GameManager", "gm");

        this.getLogger().info("GameManager is enabled.");
    }

    @Override
    public void onDisable()
    {
        getLogger().info("GameManager is disabled.");
    }
}
