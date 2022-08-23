package tokyo.peya.plugins.gamemanager.game.logics;

import net.kunmc.lab.peyangpaperutils.lib.terminal.Terminals;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tokyo.peya.plugins.gamemanager.Game;
import tokyo.peya.plugins.gamemanager.GameManagerAPI;
import tokyo.peya.plugins.gamemanager.game.GameLogicBase;
import tokyo.peya.plugins.gamemanager.game.GamePlayer;
import tokyo.peya.plugins.gamemanager.seed.GameSeed;
import tokyo.peya.plugins.gamemanager.seed.GameStartRule;
import tokyo.peya.plugins.gamemanager.seed.GameEndRule;
import tokyo.peya.plugins.gamemanager.seed.PlayerAutoGameJoinRule;
import tokyo.peya.plugins.gamemanager.seed.PlayerGameLeaveRule;
import tokyo.peya.plugins.gamemanager.utils.Utils;

/**
 * ゲームの根幹にかかわるビルト・インのロジックです。
 */
public class CoreGameLogic extends GameLogicBase
{
    private final GameSeed seed;

    public CoreGameLogic(Game game, GameManagerAPI gameManager, GameSeed seed)
    {
        super(game, gameManager);

        this.seed = seed;
    }

    private void addAllPlayers(PlayerAutoGameJoinRule timing)
    {
        Bukkit.getOnlinePlayers().forEach(player ->
                CoreGameLogic.this.getGame().addPlayer(player, timing));
    }

    private void addAllPlayersAsTiming(PlayerAutoGameJoinRule timing)
    {
        if (this.seed.isJoinTiming(timing))
            this.addAllPlayers(timing);
    }

    @Override
    public void onCreate()
    {
        this.addAllPlayersAsTiming(PlayerAutoGameJoinRule.GAME_CREATED);
    }

    @Override
    public void onStart(GameStartRule rule)
    {
        this.addAllPlayersAsTiming(PlayerAutoGameJoinRule.GAME_STARTED);

        this.getGame().getPlayers().stream().parallel()
                .map(GamePlayer::getPlayer)
                .forEach(this::notifyGameStart);
    }

    private void notifyGameStart(Player player)
    {
        player.sendMessage(ChatColor.GREEN + "ゲームが開始されました。");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1.0F, 1.0F);

        player.sendTitle(
                ChatColor.YELLOW + ChatColor.BOLD.toString() + this.getGame().getSeed().getDisplayName(),
                ChatColor.GREEN + "ゲームが開始されました。",
                20, 40, 20);

        // Game description

        String header = ChatColor.GREEN + StringUtils.repeat("-", 53);

        player.sendMessage(header);

        Utils.sendMessageCenter(player, ChatColor.BOLD + this.getGame().getSeed().getDisplayName());
        player.sendMessage("");

        String description = this.getGame().getSeed().getDescription();
        String[] lines = description.split("\n");

        for (String line : lines)
            Utils.sendMessageCenter(player, ChatColor.YELLOW + ChatColor.BOLD.toString() + line);

        player.sendMessage("");
        player.sendMessage(header);
    }

    @Override
    public void onEnd(GameEndRule rule)
    {
        this.addAllPlayersAsTiming(PlayerAutoGameJoinRule.GAME_ENDED);

        this.getGame().getPlayers().stream().parallel()
                .map(GamePlayer::getPlayer)
                .forEach(this::notifyGameEnd);
    }

    private void notifyGameEnd(Player player)
    {
        player.sendMessage(ChatColor.GREEN + "ゲームが終了しました。");
        player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 1.0F, 0.1F);
    }

    @Override
    public void onPlayerJoin(Player player, PlayerAutoGameJoinRule rule)
    {
        Terminals.of(player).info(ChatColor.GREEN + "ゲーム「" + this.seed.getDisplayName() + "」に参加しました。");

        String playerJoinMessage = ChatColor.GREEN + "+ " + ChatColor.YELLOW + player.getName() + " がゲームに参加しました。";

        if (this.seed.isNotificationOnPlayerJoinLeave())
            this.getGame().getPlayers().stream().parallel()
                    .filter(gamePlayer -> !gamePlayer.getPlayer().getUniqueId().equals(player.getUniqueId()))
                    .forEach(p -> p.getPlayer().sendMessage(playerJoinMessage));
    }

    @Override
    public void onPlayerLeave(Player player, PlayerGameLeaveRule rule)
    {
        Terminals.of(player).info(ChatColor.RED +  "ゲーム「" + this.seed.getDisplayName() + "」から退出しました。");

        String playerLeaveMessage = ChatColor.RED + "- " + ChatColor.YELLOW + player.getName() + " がゲームから退出しました。";

        if (this.seed.isNotificationOnPlayerJoinLeave())
            this.getGame().getPlayers().stream().parallel()
                    .filter(gamePlayer -> !gamePlayer.getPlayer().getUniqueId().equals(player.getUniqueId()))
                    .forEach(p -> p.getPlayer().sendMessage(playerLeaveMessage));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        if (this.getGame().getSeed().getAutoJoinRules().contains(PlayerAutoGameJoinRule.SERVER_JOINED))
            this.getGame().addPlayer(event.getPlayer(), PlayerAutoGameJoinRule.SERVER_JOINED);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        this.getGame().removePlayer(event.getPlayer(), PlayerGameLeaveRule.SERVER_LEFT);
    }
}
