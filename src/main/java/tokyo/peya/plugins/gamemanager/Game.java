package tokyo.peya.plugins.gamemanager;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tokyo.peya.plugins.gamemanager.game.GameLogic;
import tokyo.peya.plugins.gamemanager.game.GamePlayer;
import tokyo.peya.plugins.gamemanager.game.logics.CoreGameLogic;
import tokyo.peya.plugins.gamemanager.plugin.GameManager;
import tokyo.peya.plugins.gamemanager.seed.GameRunRule;
import tokyo.peya.plugins.gamemanager.seed.GameSeed;
import tokyo.peya.plugins.gamemanager.seed.GameStartRule;
import tokyo.peya.plugins.gamemanager.seed.GameEndRule;
import tokyo.peya.plugins.gamemanager.seed.PlayerGameJoinRule;
import tokyo.peya.plugins.gamemanager.seed.PlayerAutoGameJoinRule;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Game
{
    @NotNull
    private final Plugin plugin;

    @NotNull
    private final GameManager gameManager;
    @NotNull
    private final GameManagerAPI gameManagerAPI;
    @NotNull
    private final GameSeed seed;
    @NotNull
    private final String gameID;

    @NotNull
    @Getter(AccessLevel.NONE)
    private final List<GameLogic> gameLogics;
    @NotNull
    @Getter(AccessLevel.NONE)
    private final List<GamePlayer> players;

    private boolean started;

    public Game(@NotNull Plugin plugin, @NotNull GameManager gameManager, @NotNull GameManagerAPI gameManagerAPI, @NotNull GameSeed seed)
    {
        this.plugin = plugin;

        this.gameManager = gameManager;
        this.gameManagerAPI = gameManagerAPI;
        this.seed = seed;
        this.gameID = UUID.randomUUID().toString().substring(0, 8);

        this.gameLogics = seed.getLogics();
        this.gameLogics.add(0, new CoreGameLogic(this, gameManagerAPI, seed));

        this.players = new LinkedList<>();

        this.started = false;

        this.initialize();
    }

    private void initialize()
    {
        this.gameLogics.forEach(this::onLogicAdded);

        this.dispatchOnCreated();
    }

    private void onLogicAdded(@NotNull GameLogic gameLogic)
    {
        Bukkit.getPluginManager().registerEvents(gameLogic, Game.this.plugin);
    }

    /**
     * ゲームを削除します。動作中の場合は {@link GameEndRule#MANUAL} で停止されます。
     */
    public void dispose()
    {
        if (this.started)
            this.stop();

        this.gameLogics.forEach(HandlerList::unregisterAll);  // no need to create onLogicRemoved method

        this.gameManagerAPI.removeFromList(this);
    }

    /**
     * ロジックを追加します。
     *
     * @param gameLogic ロジック
     */
    public void addLogic(@NotNull GameLogic gameLogic)
    {
        this.gameLogics.add(gameLogic);
        this.onLogicAdded(gameLogic);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Game)) return false;
        Game game = (Game) o;
        return this.seed.getId().equals(game.seed.getId()) && this.gameID.equals(game.gameID);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.seed.getId(), this.gameID);
    }

    private void checkGameStartable()
    {
        if (this.started)
            throw new IllegalStateException("Game has already started: " + this.gameID);

        if (this.seed.getRunRule() == GameRunRule.ONLY_ONE_GAME)
        {
            Game anotherOnlyOne = this.gameManagerAPI.getRunningOnlyOneGame();
            if (anotherOnlyOne != null)
                throw new IllegalStateException("Another game is running: " + anotherOnlyOne.getGameID());
        }
    }

    /**
     * ゲームを開始します。
     *
     * @param rule 一致したルール
     * @throws IllegalStateException ゲームがすでに開始されている場合
     * @throws IllegalStateException {@link GameRunRule#ONLY_ONE_GAME} が指定されていて、かつ他のゲームがすでに開始されている場合
     */
    public void start(GameStartRule rule)
    {
        this.checkGameStartable();

        this.started = true;

        this.dispatchOnStarted(rule);
    }

    /**
     * ゲームを開始します。
     * 内部で {@link #start(GameStartRule)} を呼び出します。タイミングには {@link GameStartRule#MANUAL} が指定されます。
     *
     * @throws IllegalStateException ゲームがすでに開始されている場合
     * @throws IllegalStateException {@link GameRunRule#ONLY_ONE_GAME} が指定されていて、かつ他のゲームがすでに開始されている場合
     * @see #start(GameStartRule)
     */
    public void start()
    {
        this.start(GameStartRule.MANUAL);
    }

    /**
     * ゲームを終了します。
     *
     * @param rule 合致した終了ルール
     */
    public void stop(GameEndRule rule)
    {
        if (!this.started)
            throw new IllegalStateException("The game aren't started: " + this.gameID);

        this.dispatchOnStopped(rule);
        this.started = false;
    }

    /**
     * ゲームを終了します。
     * 内部で {@link #stop(GameEndRule)} を呼び出します。タイミングには {@link GameEndRule#MANUAL} が指定されます。
     *
     * @see #stop(GameEndRule)
     */
    public void stop()
    {
        this.stop(GameEndRule.MANUAL);
    }

    private boolean isPlayerCanJoin(@NotNull Player player, boolean auto)
    {
        if (this.isPlayer(player))
            if (auto)
                return false;
            else
                throw new IllegalStateException("The player is already in the game: " + player.getName());

        if (this.seed.getJoinRule() == PlayerGameJoinRule.ONLY_ONE_GAME)
            if (!this.gameManagerAPI.getPlayerGames(player).isEmpty())
                if (auto)
                    return false;
                else
                    throw new IllegalStateException("The player is already in another game: " + player.getName());

        return true;
    }

    /**
     * ゲームにプレイヤがいるかどうかを返します。
     * @param player プレイヤ
     * @return ゲームにプレイヤがいるかどうか
     */
    public boolean isPlayer(@NotNull Player player)
    {
        return this.players.stream().parallel()
                .anyMatch(gamePlayer -> gamePlayer.getPlayer().equals(player));
    }

    /**
     * ゲームに参加しているプレイヤを返します。
     * このメソッドから提供されるリストからアイテムを削除しても、実際のプレイヤリストは変更されません。
     * @return ゲームに参加しているプレイヤ
     */
    public List<GamePlayer> getPlayers()
    {
        return new ArrayList<>(this.players);
    }

    /**
     * ゲームにプレイヤを追加します。
     *
     * @param player プレイヤ
     * @param joinRule 一致したルール
     */
    public void addPlayer(@NotNull Player player, @NotNull PlayerAutoGameJoinRule joinRule)
    {
        if (!this.isPlayerCanJoin(player, joinRule == PlayerAutoGameJoinRule.MANUAL))
            return;

        this.players.add(new GamePlayer(this, player));
    }



    // =============================== Game logic event dispatch ===============================

    private void dispatchOnStarted(GameStartRule rule)
    {
        try
        {
            this.gameLogics.forEach(gameLogic -> gameLogic.onStart(rule));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            this.gameManager.getLogger().severe("Failed to pass start game handlers: " + this.gameID);

            this.stop();
        }
    }

    private void dispatchOnStopped(GameEndRule rule)
    {
        try
        {
            this.gameLogics.forEach(gameLogic -> gameLogic.onEnd(rule));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            this.gameManager.getLogger().severe("Failed to pass stop game handlers: " + this.gameID);
        }
    }

    private void dispatchOnCreated()
    {
        try
        {
            this.gameLogics.forEach(GameLogic::onCreate);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            this.gameManager.getLogger().severe("Failed to pass create game handlers: " + this.gameID);
        }
    }
}
