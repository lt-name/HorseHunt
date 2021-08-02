package cn.lanink.horsehunt.command;

import cn.lanink.horsehunt.command.base.BaseCommand;
import cn.lanink.horsehunt.command.usersub.Join;
import cn.lanink.horsehunt.command.usersub.List;
import cn.lanink.horsehunt.command.usersub.Quit;
import cn.lanink.horsehunt.ui.GuiCreate;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

public class UserCommand extends BaseCommand {

    public UserCommand(String name) {
        super(name, "HorseHunt 游戏命令");
        this.setPermission("HotPotato.command.user");

        this.addSubCommand(new Join("join"));
        this.addSubCommand(new Quit("quit"));
        this.addSubCommand(new List("list"));

        this.loadCommandBase();
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage("§a/%cmdName% §e打开ui\n" +
                "§a/%cmdName% join 房间名称 §e加入游戏\n" +
                "§a/%cmdName% quit §e退出游戏\n" +
                "§a/%cmdName% list §e查看房间列表"
                        .replace("%cmdName%", this.getName()));
    }

    @Override
    public void sendUI(CommandSender sender) {
        GuiCreate.sendUserMenu((Player) sender);
    }

}
