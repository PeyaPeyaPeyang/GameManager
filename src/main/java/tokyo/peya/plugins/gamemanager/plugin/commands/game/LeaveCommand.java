package tokyo.peya.plugins.gamemanager.plugin.commands.game;

import net.kunmc.lab.peyangpaperutils.lib.command.CommandBase;
import net.kunmc.lab.peyangpaperutils.lib.terminal.Terminal;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tokyo.peya.plugins.gamemanager.Game;
import tokyo.peya.plugins.gamemanager.GameManager;
import tokyo.peya.plugins.gamemanager.seed.PlayerGameLeaveRule;

import java.util.List;
import java.util.stream.Collectors;

public class LeaveCommand extends CommandBase
{
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull Terminal terminal, String[] args)
    {
        if (indicateArgsLengthInvalid(terminal, args, 1) || indicatePlayer(terminal))
            return;

        String gameId = args[0];
        gameId = gameId.substring(0, !gameId.contains("(") ? gameId.length() : gameId.indexOf("("));

        Player player = (Player) sender;

        Game game = GameManager.getGameManager().getGame(gameId);

        if (game == null)
        {
            terminal.error("ゲームが見つかりませんでした：%s", gameId);
            return;
        }

        if (!game.isPlayer(player))
        {
            terminal.error("参加していません：%s", game.getSeed().getDisplayName() + "( " + game.getGameID() + " )");
            return;
        }
        try
        {
            game.removePlayer(player, PlayerGameLeaveRule.MANUAL);
        }
        catch (Exception e)
        {
            terminal.error("退出に失敗しました：%s", e.getMessage());
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Terminal terminal, String[] args)
    {
        return GameManager.getGameManager().getGames().stream()
                .map(game -> game.getGameID() + "( + " + game.getSeed().getId() + ")")
                .collect(Collectors.toList());
    }

    @Override
    public @Nullable String getPermission()
    {
        return "game.leave";
    }

    @Override
    public TextComponent getHelpOneLine()
    {
        return of("ゲームから退出します。");
    }

    @Override
    public String[] getArguments()
    {
        return new String[]{
                required("ゲームID", "gameId")
        };
    }
}
