package tokyo.peya.plugins.gamemanager.seed;

/**
 * ゲームのサーバでの実行時の扱いについて制御します。
 */
public enum GameRunCause
{
    /**
     * サーバ上で実行できるゲームをこれ1つに限定します。
     * これが使用されているゲームが実行中に、他のゲームを実行しようとした場合、その試みは拒否されます。
     */
    ONLY_ONE_GAME
}
