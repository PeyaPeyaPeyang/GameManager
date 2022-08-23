package tokyo.peya.plugins.gamemanager.plugin.commands.manager;

import net.kunmc.lab.peyangpaperutils.lib.command.CommandBase;
import net.kunmc.lab.peyangpaperutils.lib.terminal.Terminal;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tokyo.peya.plugins.gamemanager.Game;
import tokyo.peya.plugins.gamemanager.GameManager;

import java.util.List;
import java.util.stream.Collectors;

public class StartCommand extends CommandBase
{
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull Terminal terminal, String[] args)
    {
        if (indicateArgsLengthInvalid(terminal, args, 1))
            return;

        Game game;
        if ((game = GameManager.getGameManager().getGame(args[0])) == null)
        {
            terminal.error("無効なゲームIDです：%s",args[0]);
            return;
        }

        if (game.isRunning())
        {
            terminal.error("ゲームは既に開始されています。");
            return;
        }

        if (game.isStartScheduled())
        {
            terminal.error("ゲームは開始予定としてマークされています。");
            return;
        }

        try
        {
            game.start();
        }
        catch (Exception e)
        {
            terminal.error("ゲームの開始に失敗しました：%s",e.getMessage());
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Terminal terminal, String[] args)
    {
        return GameManager.getGameManager().getGames().stream()
                .map(Game::getGameID)
                .collect(Collectors.toList());
    }

    @Override
    public @Nullable String getPermission()
    {
        return "gm.start";
    }

    @Override
    public TextComponent getHelpOneLine()
    {
        return of("ゲームを開始します。");
    }

    @Override
    public String[] getArguments()
    {
        return new String[] {
                required("gameId", "ゲームID")
        };
    }
}
