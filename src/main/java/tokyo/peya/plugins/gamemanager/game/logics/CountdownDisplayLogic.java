package tokyo.peya.plugins.gamemanager.game.logics;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import tokyo.peya.plugins.gamemanager.Game;
import tokyo.peya.plugins.gamemanager.GameManagerAPI;
import tokyo.peya.plugins.gamemanager.game.GameLogicBase;
import tokyo.peya.plugins.gamemanager.game.GamePlayer;
import tokyo.peya.plugins.gamemanager.utils.Utils;

public class CountdownDisplayLogic extends GameLogicBase
{
    private final int countdownTime;

    public CountdownDisplayLogic(Game game, GameManagerAPI gameManager, int countdownTime)
    {
        super(game, gameManager);

        this.countdownTime = countdownTime;
    }

    @Override
    public void onStartCountdown(int remainSeconds)
    {
        getGame().getPlayers().stream().parallel()
                .map(GamePlayer::getPlayer)
                .forEach(player -> this.notifyCountdown(player, remainSeconds));

        if (remainSeconds == 1)
            this.destructSelf();
    }

    private void notifyCountdown(Player player, int remainSeconds)
    {
        boolean notifyChat = remainSeconds % 10 == 0 || remainSeconds <= 5;
        boolean notifyTitle = remainSeconds <= 5;
        boolean notifySound = remainSeconds <= 5;

        if (notifyChat)
            player.sendMessage(ChatColor.YELLOW + "ゲーム開始まであと " +
                            Utils.getPercentageColor(remainSeconds, this.countdownTime) +
                    ChatColor.YELLOW + "秒");

        if (notifyTitle)
            player.sendTitle(
                    Utils.getPercentageColor(remainSeconds, this.countdownTime).toString() + remainSeconds,
                    ChatColor.YELLOW + "ゲームの準備を！",
                    0, 20, 0);

        if (notifySound)
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
    }

}
