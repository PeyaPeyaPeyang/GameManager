package tokyo.peya.plugins.gamemanager;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tokyo.peya.plugins.gamemanager.seed.GameRunRule;
import tokyo.peya.plugins.gamemanager.seed.GameSeed;
import tokyo.peya.plugins.gamemanager.seed.GameEndRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ゲームマネージャのAPIです。
 */
public class GameManagerAPI
{
    @NotNull
    private final GameManager gameManager;
    @NotNull
    private final Map<String, Game> games;

    public GameManagerAPI(@NotNull GameManager gameManager)
    {
        this.gameManager = gameManager;

        this.games = new HashMap<>();
    }

    /**
     * ゲームを新規作成します。
     *
     * @param seed ゲームのシード
     * @return 新規作成したゲーム
     */
    public Game createGame(@NotNull Plugin plugin, @NotNull GameSeed seed)
    {
        Game game = new Game(plugin, this.gameManager, this, seed);

        this.games.put(game.getGameID(), game);

        return game;
    }

    /**
     * ゲームを取得します。
     *
     * @param gameId ゲームID
     * @return ゲーム
     */
    public @Nullable Game getGame(@NotNull String gameId)
    {
        return this.games.get(gameId);
    }

    /**
     * ゲームを取得します。存在しない場合は {@link IllegalArgumentException} をスローします。
     *
     * @param gameId ゲームID
     * @throws IllegalArgumentException ゲームが存在しない場合
     */
    public @NotNull Game getGameStrict(@NotNull String gameId)
    {
        Game game = this.games.get(gameId);
        if (game == null)
            throw new IllegalArgumentException("Game not found: " + gameId);
        return game;
    }

    /**
     * ゲームを開始します。内部で {@link Game#start()} を呼び出します。
     *
     * @param game ゲーム
     */
    public void startGame(@NotNull Game game)
    {
        game.start();
    }

    /**
     * 動作中のゲームをすべて取得します。
     *
     * @return 動作中のゲーム
     */
    public List<Game> getRunningGames()
    {
        return this.games.values().stream().parallel()
                .filter(Game::isStarted)
                .collect(Collectors.toList());
    }

    /**
     * {@link GameRunRule} が {@link GameRunRule#ONLY_ONE_GAME} に指定されているゲームが作動中かどうかを判定します。
     *
     * @return 動作中のゲームが存在する場合は true、そうでない場合は false
     * @see GameRunRule#ONLY_ONE_GAME
     */
    public @Nullable Game getRunningOnlyOneGame()
    {
        return this.games.values().stream().parallel()
                .filter(Game::isStarted)
                .filter(game -> game.getSeed().getRunRule() == GameRunRule.ONLY_ONE_GAME)
                .findFirst().orElse(null);
    }

    /**
     * ゲームを削除します。動作中の場合は {@link GameEndRule#MANUAL} で停止されます。
     * このメソッドは内部で {@link Game#dispose()} を呼び出します。
     *
     * @param game ゲーム
     * @see Game#dispose()
     */
    public void dispose(@NotNull Game game)
    {
        game.dispose();
    }

    /**
     * ゲームを削除します。動作中の場合は {@link GameEndRule#MANUAL} で停止されす。
     * このメソッドは内部で {@link Game#dispose()} を呼び出します。
     *
     * @param gameId ゲームID
     * @throws IllegalArgumentException ゲームが存在しない場合
     * @see Game#dispose()
     */
    public void dispose(@NotNull String gameId)
    {
        this.getGameStrict(gameId).dispose();
    }

    void removeFromList(@NotNull Game game)
    {
        this.games.remove(game.getGameID());
    }

    /**
     * プレイヤが参加しているゲームをすべて取得します。
     * @param player プレイヤ
     * @return 参加しているゲーム
     */
    public List<Game> getPlayerGames(@NotNull Player player)
    {
        return this.games.values().stream().parallel()
                .filter(game -> game.isPlayer(player))
                .collect(Collectors.toList());
    }

    /**
     * ゲーム一覧を取得します。
     * このメソッドから提供されるリスト化rあアイテムを削除しても、実際のゲームリストは変更されません。
     * @return ゲーム一覧
     */
    public List<Game> getGames()
    {
        return new ArrayList<>(this.games.values());
    }
}
