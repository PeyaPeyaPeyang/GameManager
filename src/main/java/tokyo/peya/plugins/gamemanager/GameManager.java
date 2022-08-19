package tokyo.peya.plugins.gamemanager;

import net.kunmc.lab.peyangpaperutils.lib.command.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class GameManager extends JavaPlugin
{
    // Effectively final.
    private static GameManagerAPI INSTANCE;

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private CommandManager mainCommand;  // We need to keep this reference to prevent the plugin from being unloaded.

    @Override
    public void onEnable()
    {
        setGameManager(new GameManagerAPI());

        this.mainCommand = new CommandManager(this, "game", "GameManager", "game");

        this.getLogger().info("GameManagerAPI is enabled.");
    }

    @Override
    public void onDisable()
    {
        getLogger().info("GameManagerAPI is disabled.");
    }

    public static @NotNull GameManagerAPI getGameManager()
    {
        return INSTANCE;
    }

    public static void setGameManager(GameManagerAPI INSTANCE)
    {
        if (GameManager.INSTANCE != null)
            throw new IllegalStateException("INSTANCE is already set.");
        else
            GameManager.INSTANCE = INSTANCE;
    }
}
