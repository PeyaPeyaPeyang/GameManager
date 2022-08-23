package tokyo.peya.plugins.gamemanager.game;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import tokyo.peya.plugins.gamemanager.seed.GameStartCause;
import tokyo.peya.plugins.gamemanager.seed.GameEndCause;
import tokyo.peya.plugins.gamemanager.seed.PlayerGameJoinCause;
import tokyo.peya.plugins.gamemanager.seed.PlayerGameLeaveCause;

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
     * @param rule 一致した開始条件
     */
    void onStart(GameStartCause rule);

    /**
     * ストップ時に実行する処理です。
     *
     * @param rule 一致した停止条件
     */
    void onEnd(GameEndCause rule);

    /**
     * プレイヤがゲーム参加したときに実行する処理です。
     * サーバに参加したときに呼び出されるものではありません。
     *
     * @param player プレイヤ
     * @param rule 一致した参加条件
     */
    void onPlayerJoin(Player player, PlayerGameJoinCause rule);

    /**
     * プレイヤが離脱したときに実行する処理です。
     * サーバから離脱したときに呼び出されるものではありません。
     *
     * @param player プレイヤ
     */
    void onPlayerLeave(Player player, PlayerGameLeaveCause rule);

    /**
     * ゲームの開始のカウントダウンが進んでいるときに実行する処理です。
     *
     * @param remainSeconds 残り秒数
     */
    void onStartCountdown(int remainSeconds);
}
