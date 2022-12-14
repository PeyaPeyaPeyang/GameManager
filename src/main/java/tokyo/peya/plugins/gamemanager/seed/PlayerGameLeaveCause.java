package tokyo.peya.plugins.gamemanager.seed;

public enum PlayerGameLeaveCause
{
    /**
     * コマンドや他のプラグインによって退出されます。
     */
    MANUAL,
    /**
     * サーバから退出されたときに自動的に退出します。
     */
    SERVER_LEFT,
}
