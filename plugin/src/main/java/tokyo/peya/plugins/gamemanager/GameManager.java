package tokyo.peya.plugins.gamemanager;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import tokyo.peya.plugins.gamemanager.impls.GameManagerAPIImpl;

public final class GameManager extends JavaPlugin
{
    // Effectively final.
    private static GameManagerAPI INSTANCE;

    @Override
    public void onEnable()
    {
        setGameManager(new GameManagerAPIImpl());

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
