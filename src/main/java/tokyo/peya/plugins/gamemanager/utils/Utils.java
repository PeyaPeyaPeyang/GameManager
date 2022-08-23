package tokyo.peya.plugins.gamemanager.utils;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * ユーティリティをまとめたクラスです。
 */
public class Utils
{
    /**
     * パーセンテージによって色を返します。
     * 0.3以下：赤
     * 0.5以下：黄
     * それ以外：緑
     *
     * @param percentage パーセンテージ
     * @return 色
     */
    public static ChatColor getPercentageColor(double percentage)
    {
        if (percentage < 0.4)
            return ChatColor.RED;
        else if (percentage < 0.6)
            return ChatColor.YELLOW;
        else
            return ChatColor.GREEN;
    }

    /**
     * パーセンテージによって色を返します。
     * @param value 値
     * @param max 最大値
     * @return 色
     * @see #getPercentageColor(double)
     */
    public static ChatColor getPercentageColor(int value, int max)
    {
        return getPercentageColor(value / (double) max);
    }

    private static final int MAX_CHAT_TEXT = 80;

    /**
     * メッセージを中央に表示します。
     * @param player プレイヤー
     * @param message メッセージ
     */
    public static void sendMessageCenter(Player player, String message)
    {
        player.sendMessage(StringUtils.repeat(" ", calcCentralizeSpaces(message)) + message);
    }

    private static int calcCentralizeSpaces(String text)
    {
        return (int) Math.round((MAX_CHAT_TEXT - 1.4 * ChatColor.stripColor(text).length()) / 2.0);
    }

}
