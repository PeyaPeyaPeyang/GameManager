package tokyo.peya.plugins.gamemanager.plugin.commands.manager;

import net.kunmc.lab.peyangpaperutils.lib.command.CommandBase;
import net.kunmc.lab.peyangpaperutils.lib.command.SubCommandWith;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tokyo.peya.plugins.gamemanager.plugin.commands.manager.debug.CreateDummyGameCommand;

import java.util.HashMap;
import java.util.Map;

public class DebugCommand extends SubCommandWith
{
    private final Map<String, CommandBase> commands;

    public DebugCommand()
    {
        this.commands = new HashMap<String, CommandBase>(){{
            this.put("createDummyGame", new CreateDummyGameCommand());
        }};
    }

    @Override
    protected String getName()
    {
        return "debug";
    }

    @Override
    protected Map<String, CommandBase> getSubCommands(@NotNull CommandSender sender)
    {
        return this.commands;
    }

    @Override
    public @Nullable String getPermission()
    {
        return "gm.debug";
    }

    @Override
    public TextComponent getHelpOneLine()
    {
        return of("ゲームマネージャのデバッグ用コマンドです。");
    }
}
