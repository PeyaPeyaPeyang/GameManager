package tokyo.peya.plugins.gamemanager.seed;

import lombok.Builder;
import lombok.Value;

/**
 * ゲームの構成やルールを定義します。
 */
@Builder
@Value
public class GameSeed
{
    /**
     * ゲームの名前です。
     * Eg: "SkyWars"
     */
    String name;

    /**
     * ゲームを識別する一意の名前です。大小は自動で大文字に変換されます。
     * Eg: "SKYWARS"
     *
     * @see java.util.Locale#ENGLISH
     */
    String id;

    /**
     * ゲームを開始するタイミングです。
     */
    StartTiming startTiming;

    /**
     * ゲームを停止するタイミングです。
     */
    StopTiming stopTiming;
}
