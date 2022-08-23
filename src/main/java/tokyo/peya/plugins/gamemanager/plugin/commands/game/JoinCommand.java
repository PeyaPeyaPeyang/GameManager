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
import tokyo.peya.plugins.gamemanager.seed.PlayerGameJoinCause;
import tokyo.peya.plugins.gamemanager.seed.PlayerGameJoinRule;

import java.util.List;
import java.util.stream.Collectors;

public class JoinCommand extends CommandBase
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

        if (game.isPlayer(player))
        {
            terminal.error("すでに参加しています：%s", game.getSeed().getDisplayName() + "( " + game.getGameID() + " )");
            return;
        }

        if (game.getSeed().getJoinRule() == PlayerGameJoinRule.ONLY_ONE_GAME &&
                !GameManager.getGameManager().getPlayerGames(player).isEmpty())
        {
            Game playerGame = GameManager.getGameManager().getPlayerGames(player).get(0);

            terminal.error("すでに他のゲームに参加しているため、このゲームには参加できません：%s", playerGame.getSeed().getDisplayName() + "( " + playerGame.getGameID() + " )");
            return;
        }

        try
        {
            game.addPlayer(player, PlayerGameJoinCause.MANUAL);
        }
        catch (Exception e)
        {
            terminal.error("参加に失敗しました：%s", e.getMessage());
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
        return "game.join";
    }

    @Override
    public TextComponent getHelpOneLine()
    {
        return of("ゲームに参加します。");
    }

    @Override
    public String[] getArguments()
    {
        return new String[]{
                required("ゲームID", "gameId")
        };
    }
}
