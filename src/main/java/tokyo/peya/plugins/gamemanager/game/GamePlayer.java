package tokyo.peya.plugins.gamemanager.game;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tokyo.peya.plugins.gamemanager.Game;

import java.util.Objects;

public class GamePlayer
{
    @Getter
    @NotNull
    private final Game game;

    @Getter
    @NotNull
    private final Player player;

    public GamePlayer(@NotNull Game game, @NotNull Player player)
    {
        this.game = game;
        this.player = player;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof GamePlayer)) return false;
        GamePlayer that = (GamePlayer) o;
        return this.player.getUniqueId().equals(that.player.getUniqueId());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.player.getUniqueId());
    }
}
