package cn.lanink.horsehunt.command;

import cn.lanink.horsehunt.command.adminsub.*;
import cn.lanink.horsehunt.command.base.BaseCommand;
import cn.lanink.horsehunt.ui.GuiCreate;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

public class AdminCommand extends BaseCommand {

    public AdminCommand(String name) {
        super(name, "HorseHunt 管理命令");
        this.setPermission("HorseHunt.command.admin");

        this.addSubCommand(new SetWaitSpawn("setwaitspawn"));
        this.addSubCommand(new AddRandomSpawn("addrandomspawn"));
        this.addSubCommand(new SetWaitTime("setwaittime"));
        this.addSubCommand(new SetGameTime("setgametime"));

        this.addSubCommand(new StartRoom("startroom"));
        this.addSubCommand(new StopRoom("stoproom"));

        this.addSubCommand(new ReloadRoom("reloadroom"));
        this.addSubCommand(new UnloadRoom("unloadroom"));

        this.loadCommandBase();
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage("§a/%cmdName% §e打开ui\n" +
                "§a/%cmdName% setwaitspawn §e设置当前位置为等待出生点\n" +
                "§a/%cmdName% addrandomspawn §e添加当前位置为随机出生点\n" +
                "§a/%cmdName% setwaittime 数字 §e设置游戏人数达到最少人数后的等待时间\n" +
                "§a/%cmdName% setgametime 数字 §e设置游戏时间\n" +
                "§a/%cmdName% startroom §e强制开启所在地图的房间\n" +
                "§a/%cmdName% stoproom §e强制关闭所在地图的房间\n" +
                "§a/%cmdName% reloadroom §e重载所有房间\n" +
                "§a/%cmdName% unloadroom §e关闭所有房间,并卸载配置"
                        .replace("%cmdName%", this.getName()));
    }

    @Override
    public void sendUI(CommandSender sender) {
        GuiCreate.sendAdminMenu((Player) sender);
    }

}
