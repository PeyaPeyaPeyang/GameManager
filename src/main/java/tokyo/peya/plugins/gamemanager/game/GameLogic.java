package tokyo.peya.plugins.gamemanager.game;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import tokyo.peya.plugins.gamemanager.seed.GameStartRule;
import tokyo.peya.plugins.gamemanager.seed.GameEndRule;

/**
 * ゲームのロジック(動作)を定義します。
 */
public interface GameLogic extends Listener
{
    /**
     * ゲームが作成されたときに実行する処理です。
     */
    void onCreate();

    /**
     * スタート時に実行する処理です。
     *
     * @param timing タイミング
     */
    void onStart(GameStartRule timing);

    /**
     * ストップ時に実行する処理です。
     *
     * @param timing タイミング
     */
    void onEnd(GameEndRule timing);

    /**
     * プレイヤがゲーム参加したときに実行する処理です。
     * サーバに参加したときに呼び出されるものではありません。
     *
     * @param player プレイヤ
     */
    void onPlayerJoin(Player player);

    /**
     * プレイヤが離脱したときに実行する処理です。
     * サーバから離脱したときに呼び出されるものではありません。
     *
     * @param player プレイヤ
     */
    void onPlayerLeave(Player player);
}
