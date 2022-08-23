package tokyo.peya.plugins.gamemanager;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tokyo.peya.plugins.gamemanager.game.GameLogic;
import tokyo.peya.plugins.gamemanager.game.GamePlayer;
import tokyo.peya.plugins.gamemanager.game.logics.CoreGameLogic;
import tokyo.peya.plugins.gamemanager.game.logics.CountdownDisplayLogic;
import tokyo.peya.plugins.gamemanager.seed.GameEndRule;
import tokyo.peya.plugins.gamemanager.seed.GameRunRule;
import tokyo.peya.plugins.gamemanager.seed.GameSeed;
import tokyo.peya.plugins.gamemanager.seed.GameStartRule;
import tokyo.peya.plugins.gamemanager.seed.PlayerAutoGameJoinRule;
import tokyo.peya.plugins.gamemanager.seed.PlayerGameJoinRule;
import tokyo.peya.plugins.gamemanager.seed.PlayerGameLeaveRule;

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

    @Getter(AccessLevel.NONE)
    @Nullable
    private final CountdownProvider countdownProvider;

    @NotNull
    @Getter(AccessLevel.NONE)
    private final List<GameLogic> gameLogics;
    @NotNull
    @Getter(AccessLevel.NONE)
    private final List<GamePlayer> players;

    private boolean running;

    public Game(@NotNull Plugin plugin, @NotNull GameManager gameManager, @NotNull GameManagerAPI gameManagerAPI, @NotNull GameSeed seed)
    {
        this.plugin = plugin;

        this.gameManager = gameManager;
        this.gameManagerAPI = gameManagerAPI;
        this.seed = seed;
        this.gameID = UUID.randomUUID().toString().substring(0, 8);

        this.gameLogics = new ArrayList<>(seed.getLogics());
        this.gameLogics.add(0, new CoreGameLogic(this, gameManagerAPI, seed));

        if (seed.getCountdownSeconds() > 0)
        {
            this.countdownProvider = new CountdownProvider(this, seed.getCountdownSeconds());
            this.gameLogics.add(new CountdownDisplayLogic(this, gameManagerAPI, seed.getCountdownSeconds()));
        }
        else
            this.countdownProvider = null;


        this.players = new LinkedList<>();

        this.running = false;

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
        if (this.running)
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

    private boolean checkGameStartable(boolean isAuto)
    {
        if (this.running)
            if (isAuto)
                return false;
            else
                throw new IllegalStateException("Game is already running.");

        if (this.seed.getRunRule() == GameRunRule.ONLY_ONE_GAME)
        {
            Game anotherOnlyOne = this.gameManagerAPI.getRunningOnlyOneGame();
            if (anotherOnlyOne != null)
                if (isAuto)
                    return false;
                else
                    throw new IllegalStateException("Another game is already running: " + anotherOnlyOne.getGameID());
        }

        if (this.countdownProvider != null && this.countdownProvider.isCountdownRunning())
            if (isAuto)
                return false;
            else
                throw new IllegalStateException("Countdown is already running. Retry after canceling the countdown.");

        return true;
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
        this.checkGameStartable(rule == GameStartRule.MANUAL);

        if (!(this.countdownProvider == null || rule == GameStartRule.COUNTDOWN))
        {
            this.countdownProvider.scheduleGameStart();
            return;
        }

        this.running = true;

        this.dispatchOnStarted(rule);
    }

    /**
     * ゲームの開始がスケジュールされている場合に, そのスケジュールをキャンセルします。
     *
     * @throws IllegalStateException ゲームの開始がスケジュールされていない場合
     */
    public void cancelGameStart()
    {
        if (this.countdownProvider == null) // Other check is in CountdownProvider#cancelGameStart()
            throw new IllegalStateException("Game start is not scheduled.");

        this.countdownProvider.cancelGameStart();
    }

    /**
     * ゲームの開始がスケジュールされている場合に, そのスケジュールをキャンセルし, 即座にゲームを開始します。
     *
     * @throws IllegalStateException ゲームの開始がスケジュールされていない場合
     */
    public void skipScheduleAndStartGame()
    {
        if (this.countdownProvider == null) // Other check is in CountdownProvider#skipCountdown()
            throw new IllegalStateException("Game start is not scheduled.");

        this.countdownProvider.skipCountdown();
    }

    /**
     * ゲームの開始がスケジュールされているかどうかを返します。
     * @return ゲームの開始がスケジュールされている場合は true, そうでない場合は false
     */
    public boolean isStartScheduled()
    {
        return this.countdownProvider != null && this.countdownProvider.isCountdownRunning();
    }

    /**
     * ゲームの開始がスケジュールされている場合に, ゲームが開始するまでの時間を返します。
     *
     * @return ゲームが開始するまでの時間
     */
    public int getRemainingSeconds()
    {
        return this.countdownProvider != null ? this.countdownProvider.getCountdownTimeRemaining() : 0;
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
        if (!this.running)
            throw new IllegalStateException("The game aren't started: " + this.gameID);

        this.dispatchOnStopped(rule);
        this.running = false;
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
        if (!this.isPlayerCanJoin(player, joinRule != PlayerAutoGameJoinRule.MANUAL))
            return;

        this.players.add(new GamePlayer(this, player));

        this.dispatchOnPlayerJoined(player, joinRule);
    }

    /**
     * ゲームからプレイヤを削除します。
     * @param player プレイヤ
     * @param leaveRule 一致したルール
     * @throws IllegalArgumentException プレイヤがゲームに存在しない場合
     */
    public void removePlayer(@NotNull Player player, @NotNull PlayerGameLeaveRule leaveRule)
    {
        if (!this.isPlayer(player) && leaveRule == PlayerGameLeaveRule.MANUAL)
            throw new IllegalArgumentException("The player isn't in the game: " + player.getName());

        this.players.removeIf(gamePlayer -> gamePlayer.getPlayer().equals(player));

        this.dispatchOnPlayerLeft(player, leaveRule);
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

    private void dispatchOnPlayerJoined(@NotNull Player player, @NotNull PlayerAutoGameJoinRule joinRule)
    {
        try
        {
            this.gameLogics.forEach(gameLogic -> gameLogic.onPlayerJoin(player, joinRule));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            this.gameManager.getLogger().severe("Failed to pass player join game handlers: " + this.gameID);
        }
    }

    private void dispatchOnPlayerLeft(@NotNull Player player, @NotNull PlayerGameLeaveRule leaveRule)
    {
        try
        {
            this.gameLogics.forEach(gameLogic -> gameLogic.onPlayerLeave(player, leaveRule));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            this.gameManager.getLogger().severe("Failed to pass player quit game handlers: " + this.gameID);
        }
    }

    void dispatchOnStartCountdown(int time)
    {
        try
        {
            this.gameLogics.forEach(gameLogic -> gameLogic.onStartCountdown(time));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            this.gameManager.getLogger().severe("Failed to pass start countdown game handlers: " + this.gameID);
        }
    }

    /**
     * ロジックを削除します。
     * @param gameLogic ロジック
     */
    public void removeLogic(@NotNull GameLogic gameLogic)
    {
        this.gameLogics.remove(gameLogic);
    }
}
