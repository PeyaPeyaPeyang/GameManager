package tokyo.peya.plugins.gamemanager.seed;

/**
 * プレイヤのゲームの参加のタイミングを制御します。
 */
public enum PlayerGameJoinCause
{
    /**
     * コマンドや他のプラグインによって参加されます。
     */
    MANUAL,
    /**
     * ゲーム作成時に自動的に参加します。
     */
    GAME_CREATED,
    /**
     * ゲーム開始時に自動的に参加します。
     */
    GAME_STARTED,
    /**
     * ゲーム終了時に自動的に参加します。
     */
    GAME_ENDED,
    /**
     * サーバへ参加したときに自動的に参加します。
     */
    SERVER_JOINED,
}
