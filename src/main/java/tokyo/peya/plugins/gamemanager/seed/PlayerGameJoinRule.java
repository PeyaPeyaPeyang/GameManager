package tokyo.peya.plugins.gamemanager.seed;

/**
 * プレイヤの参加状態に関するルールを定義します。
 */
public enum PlayerGameJoinRule
{
    /**
     * プレイヤは、一番最初に参加したゲームにのみ参加できます。
     * 自動的な参加は無視されます。
     */
    ONLY_ONE_GAME,
    /**
     * プレイヤは、他のゲームへの参加の有無に関わらず、すべてのゲームに参加できます。
     */
    ALL
}
