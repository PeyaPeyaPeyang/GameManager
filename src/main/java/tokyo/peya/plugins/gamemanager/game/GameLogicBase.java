package tokyo.peya.plugins.gamemanager.game;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import tokyo.peya.plugins.gamemanager.Game;
import tokyo.peya.plugins.gamemanager.GameManagerAPI;
import tokyo.peya.plugins.gamemanager.seed.GameStartRule;
import tokyo.peya.plugins.gamemanager.seed.GameEndRule;
import tokyo.peya.plugins.gamemanager.seed.PlayerAutoGameJoinRule;
import tokyo.peya.plugins.gamemanager.seed.PlayerGameLeaveRule;

import java.util.Objects;
import java.util.UUID;

/**
 * ゲームのロジックの定義の基底クラスです。
 */
@Getter(AccessLevel.PROTECTED)
public abstract class GameLogicBase implements GameLogic, Listener
{
    private final Game game;
    private final GameManagerAPI gameManager;

    public GameLogicBase(Game game, GameManagerAPI gameManager)
    {
        this.game = game;
        this.gameManager = gameManager;
    }

    @Override
    public void onCreate()
    {

    }

    @Override
    public void onStart(GameStartRule rule)
    {
    }

    @Override
    public void onEnd(GameEndRule rule)
    {
    }

    @Override
    public void onPlayerJoin(Player player, PlayerAutoGameJoinRule rule)
    {
    }

    @Override
    public void onPlayerLeave(Player player, PlayerGameLeaveRule rule)
    {
    }

    @Override
    public void onStartCountdown(int remainSeconds)
    {

    }
}
