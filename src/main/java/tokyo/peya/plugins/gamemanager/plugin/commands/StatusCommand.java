package tokyo.peya.plugins.gamemanager.plugin.commands;

import net.kunmc.lab.peyangpaperutils.lib.command.CommandBase;
import net.kunmc.lab.peyangpaperutils.lib.terminal.Terminal;
import net.kunmc.lab.peyangpaperutils.lib.utils.Pair;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tokyo.peya.plugins.gamemanager.Game;
import tokyo.peya.plugins.gamemanager.game.GamePlayer;
import tokyo.peya.plugins.gamemanager.plugin.GameManager;
import tokyo.peya.plugins.gamemanager.seed.GameSeed;
import tokyo.peya.plugins.gamemanager.seed.PlayerAutoGameJoinRule;

import java.util.List;
import java.util.stream.Collectors;

public class StatusCommand extends CommandBase
{

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull Terminal terminal, String[] args)
    {
        if (args.length == 0)
        {
            GameManager.getGameManager().getGames().stream()
                    .map(game -> new Pair<>(game.getGameID(), game.getSeed().getDisplayName()))
                    .map(pair -> of(ChatColor.YELLOW + "* " + ChatColor.AQUA + pair.getLeft() + " - " + pair.getRight())
                            .clickEvent(ClickEvent.runCommand("/gm status " + pair.getLeft()))
                            .hoverEvent(HoverEvent.showText(of(ChatColor.YELLOW + "クリックして詳細を表示！"))))
                    .forEach(terminal::write);
            return;
        }

        printGameInfo(terminal, args[0]);
    }

    private void printGameInfo(Terminal terminal, String gameId)
    {
        Game game;
        if ((game = GameManager.getGameManager().getGame(gameId)) == null)
        {
            terminal.error("無効なゲームIDです：%s",gameId);
            return;
        }

        terminal.writeLine(ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + "===================================");

        GameSeed seed = game.getSeed();

        printValue(terminal, 0, "ゲームID", game.getGameID());
        printValue(terminal, 0,  "ゲーム名", seed.getDisplayName());
        printValue(terminal, 0, "プレイヤー", "(" + game.getPlayers().size() + ") \n"
                + game.getPlayers().stream()
                .map(GamePlayer::getPlayer)
                .map(Player::getName)
                .sorted()
                .collect(Collectors.joining(", ")));

        // TODO: printValue(terminal, 0, "ゲーム開始時間", game.getStartTime());

        printValue(terminal, 0, "ゲーム設定", "");
        printValue(terminal, 1, "プレイヤ参加ルール", seed.getJoinRule());
        printValue(terminal, 1, "プレイヤ自動参加ルール", seed.getAutoJoinRules().stream()
                .map(PlayerAutoGameJoinRule::toString)
                .collect(Collectors.joining(", ")));
        printValue(terminal, 1, "ゲーム自動開始ルール", seed.getAutoStartRule());
        printValue(terminal, 1, "ゲーム自動終了ルール", seed.getAutoEndRule());
    }

    private static void printValue(Terminal terminal, int indent, String key, Object value)
    {
        terminal.info(ChatColor.AQUA + "%s%s：" + ChatColor.YELLOW + "%s",
                StringUtils.repeat(" ", indent * 2), key, value);
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
        return "gm.status";
    }

    @Override
    public TextComponent getHelpOneLine()
    {
        return of("ゲームの状態を表示します。");
    }

    @Override
    public String[] getArguments()
    {
        return new String[] {
            optional("ゲームID", "gameId")
        };
    }
}
