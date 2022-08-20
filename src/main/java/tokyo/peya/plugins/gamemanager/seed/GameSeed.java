package tokyo.peya.plugins.gamemanager.seed;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import tokyo.peya.plugins.gamemanager.game.GameLogic;

import java.util.List;
import java.util.Locale;

/**
 * ゲームの構成やルールを定義します。
 */
@Builder
@Value
public class GameSeed
{
    /**
     * ゲームの表示名です。
     * Eg: "SkyWars"
     */
    String displayName;

    /**
     * ゲームを識別する一意の名前。大小は自動で大文字に変換されます。
     * Eg: "SKYWARS"
     *
     * @see java.util.Locale#ENGLISH
     */
    String id;

    /**
     * ゲームを自動開始する条件です。
     */
    @Builder.Default
    GameStartRule autoStartRule = GameStartRule.MANUAL;

    /**
     * ゲームを自動停止する条件です。
     */
    @Builder.Default
    GameEndRule autoEndRule = GameEndRule.MANUAL;

    /**
     * ゲームを実行するルールです。
     */
    @Builder.Default
    GameRunRule runRule = GameRunRule.ONLY_ONE_GAME;

    /**
     * プレイヤの複数ゲームへの参加を許可するかどうかを制御します。
     */
    @Builder.Default
    PlayerGameJoinRule joinRule = PlayerGameJoinRule.ONLY_ONE_GAME;

    /**
     * プレイヤのゲームの自動参加の条件です。
     */
    @Singular("joinTiming")
    List<PlayerAutoGameJoinRule> autoJoinRules;


    /**
     * ゲームを停止するときにサーバのプレイヤーをゲームから削除するかどうかです。
     */
    boolean removeServerPlayerOnStop;


    /**
     * ゲームのロジックを定義です。
     */
    @Singular
    List<GameLogic> logics;

    /**
     * プレイヤの参加タイミングとして設定されているかどうかを判定します。
     * @param timing 判定するタイミング
     * @return 設定されている場合はtrue
     */
    public boolean isJoinTiming(@NotNull PlayerAutoGameJoinRule timing)
    {
        return this.autoJoinRules.contains(timing);
    }

    public String getId()
    {
        return this.id.toUpperCase(Locale.ENGLISH);
    }
}
