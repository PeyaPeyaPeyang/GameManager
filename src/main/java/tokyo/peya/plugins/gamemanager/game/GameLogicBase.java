package tokyo.peya.plugins.gamemanager.game;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import tokyo.peya.plugins.gamemanager.Game;
import tokyo.peya.plugins.gamemanager.GameManagerAPI;
import tokyo.peya.plugins.gamemanager.seed.GameStartRule;
import tokyo.peya.plugins.gamemanager.seed.GameEndRule;

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
    public void onStart(GameStartRule timing)
    {
    }

    @Override
    public void onEnd(GameEndRule timing)
    {
    }

    @Override
    public void onPlayerJoin(Player player)
    {
    }

    @Override
    public void onPlayerLeave(Player player)
    {
    }
}
