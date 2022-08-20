package tokyo.peya.plugins.gamemanager.game.logics;

import org.bukkit.Bukkit;
import tokyo.peya.plugins.gamemanager.Game;
import tokyo.peya.plugins.gamemanager.GameManagerAPI;
import tokyo.peya.plugins.gamemanager.game.GameLogicBase;
import tokyo.peya.plugins.gamemanager.seed.GameSeed;
import tokyo.peya.plugins.gamemanager.seed.GameStartRule;
import tokyo.peya.plugins.gamemanager.seed.GameEndRule;
import tokyo.peya.plugins.gamemanager.seed.PlayerAutoGameJoinRule;

/**
 * ゲームの根幹にかかわるビルト・インのロジックです。
 */
public class CoreGameLogic extends GameLogicBase
{
    private final GameSeed seed;

    public CoreGameLogic(Game game, GameManagerAPI gameManager, GameSeed seed)
    {
        super(game, gameManager);

        this.seed = seed;
    }

    private void addAllPlayers(PlayerAutoGameJoinRule timing)
    {
        Bukkit.getOnlinePlayers().forEach(player ->
                CoreGameLogic.this.getGame().addPlayer(player, timing));
    }

    private void addAllPlayersAsTiming(PlayerAutoGameJoinRule timing)
    {
        if (this.seed.isJoinTiming(timing))
            this.addAllPlayers(timing);
    }

    @Override
    public void onCreate()
    {
        this.addAllPlayersAsTiming(PlayerAutoGameJoinRule.GAME_CREATED);
    }

    @Override
    public void onStart(GameStartRule timing)
    {
        this.addAllPlayersAsTiming(PlayerAutoGameJoinRule.GAME_STARTED);
    }

    @Override
    public void onEnd(GameEndRule timing)
    {
        this.addAllPlayersAsTiming(PlayerAutoGameJoinRule.GAME_ENDED);
    }
}
