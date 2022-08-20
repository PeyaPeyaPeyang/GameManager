package tokyo.peya.plugins.gamemanager.plugin.commands;

import net.kunmc.lab.peyangpaperutils.lib.command.CommandBase;
import net.kunmc.lab.peyangpaperutils.lib.command.SubCommandWith;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tokyo.peya.plugins.gamemanager.plugin.commands.game.JoinCommand;
import tokyo.peya.plugins.gamemanager.plugin.commands.game.LeaveCommand;

import java.util.HashMap;
import java.util.Map;

public class GameCommand extends SubCommandWith
{
    private final Map<String, CommandBase> commands;

    public GameCommand()
    {
        this.commands = new HashMap<String, CommandBase>(){{
            put("join", new JoinCommand());
            put("leave", new LeaveCommand());

        }};
    }

    @Override
    protected String getName()
    {
        return "game";
    }

    @Override
    protected Map<String, CommandBase> getSubCommands(@NotNull CommandSender sender)
    {
        return this.commands;
    }

    @Override
    public @Nullable String getPermission()
    {
        return "gm.game";
    }

    @Override
    public TextComponent getHelpOneLine()
    {
        return of("ゲームへの参加などをするコマンドです。");
    }
}
