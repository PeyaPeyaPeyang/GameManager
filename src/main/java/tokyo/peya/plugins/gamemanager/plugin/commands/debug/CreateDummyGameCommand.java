package tokyo.peya.plugins.gamemanager.plugin.commands.debug;

import net.kunmc.lab.peyangpaperutils.lib.command.CommandBase;
import net.kunmc.lab.peyangpaperutils.lib.terminal.Terminal;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tokyo.peya.plugins.gamemanager.Game;
import tokyo.peya.plugins.gamemanager.plugin.GameManager;
import tokyo.peya.plugins.gamemanager.seed.GameSeed;
import tokyo.peya.plugins.gamemanager.seed.PlayerAutoGameJoinRule;
import tokyo.peya.plugins.gamemanager.seed.PlayerGameJoinRule;

import java.util.List;

public class CreateDummyGameCommand extends CommandBase
{
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull Terminal terminal, String[] args)
    {
        GameSeed seed = GameSeed.builder()
                .id("dummy_")
                .displayName("Dummy Game")
                .autoJoinRule(PlayerAutoGameJoinRule.GAME_CREATED)
                .autoJoinRule(PlayerAutoGameJoinRule.GAME_STARTED)
                .autoJoinRule(PlayerAutoGameJoinRule.GAME_ENDED)
                .joinRule(PlayerGameJoinRule.ALL)
                .build();

        Game game = GameManager.getGameManager().createGame(GameManager.getProvidingPlugin(this.getClass()), seed);

        terminal.info("New game created! id: %s", game.getGameID());

        Bukkit.dispatchCommand(sender, "/gm status " + game.getGameID());
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Terminal terminal, String[] args)
    {
        return null;
    }

    @Override
    public @Nullable String getPermission()
    {
        return "gm.debug.createDummyGame";
    }

    @Override
    public TextComponent getHelpOneLine()
    {
        return of("This command creates a dummy game for debugging.");
    }

    @Override
    public String[] getArguments()
    {
        return new String[0];
    }
}
