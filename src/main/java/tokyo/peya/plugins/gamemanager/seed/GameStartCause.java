package tokyo.peya.plugins.gamemanager.seed;


/**
 * ゲームを開始するタイミングを制御します。
 */
public enum GameStartCause
{
    /**
     * コマンドや他のプラグインによって開始されます。
     */
    MANUAL,
    /**
     * カウントダウン によって開始されます。
     */
    COUNTDOWN,
}
