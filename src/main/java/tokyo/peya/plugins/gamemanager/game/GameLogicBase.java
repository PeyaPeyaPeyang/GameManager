package tokyo.peya.plugins.gamemanager.game;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import tokyo.peya.plugins.gamemanager.Game;
import tokyo.peya.plugins.gamemanager.GameManagerAPI;
import tokyo.peya.plugins.gamemanager.seed.GameStartCause;
import tokyo.peya.plugins.gamemanager.seed.GameEndCause;
import tokyo.peya.plugins.gamemanager.seed.PlayerGameJoinCause;
import tokyo.peya.plugins.gamemanager.seed.PlayerGameLeaveCause;

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
    public void onStart(GameStartCause rule)
    {
    }

    @Override
    public void onEnd(GameEndCause rule)
    {
    }

    @Override
    public void onPlayerJoin(Player player, PlayerGameJoinCause rule)
    {
    }

    @Override
    public void onPlayerLeave(Player player, PlayerGameLeaveCause rule)
    {
    }

    @Override
    public void onStartCountdown(int remainSeconds)
    {

    }
}
