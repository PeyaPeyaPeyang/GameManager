package tokyo.peya.plugins.gamemanager;

import net.kunmc.lab.peyangpaperutils.lib.command.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import tokyo.peya.plugins.gamemanager.plugin.commands.DebugCommand;
import tokyo.peya.plugins.gamemanager.plugin.commands.StatusCommand;
import tokyo.peya.plugins.gamemanager.plugin.commands.game.JoinCommand;
import tokyo.peya.plugins.gamemanager.plugin.commands.game.LeaveCommand;

public final class GameManager extends JavaPlugin
{
    // Effectively final.
    private static GameManagerAPI API;

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private CommandManager mainCommand;  // We need to keep this reference to prevent the plugin from being unloaded.
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private CommandManager gameCommand;  // Same as above.

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

        CommandManager mainCommand = new CommandManager(this, "gm", "GameManager", "gm");
        mainCommand.registerCommand("status", new StatusCommand());
        mainCommand.registerCommand("debug", new DebugCommand());

        CommandManager gameCommand = new CommandManager(this, "game", "GameManager", "game");
        gameCommand.registerCommand("join", new JoinCommand());
        gameCommand.registerCommand("leave", new LeaveCommand());

        this.mainCommand = mainCommand;
        this.getLogger().info("GameManager is enabled.");
    }

    @Override
    public void onDisable()
    {
        getLogger().info("GameManager is disabled.");
    }
}
