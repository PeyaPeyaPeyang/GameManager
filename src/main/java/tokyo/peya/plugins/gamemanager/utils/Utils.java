package tokyo.peya.plugins.gamemanager.utils;

import org.bukkit.ChatColor;

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
}
