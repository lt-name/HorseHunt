package cn.lanink.horsehunt.command.adminsub;

import cn.lanink.horsehunt.command.base.BaseSubCommand;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.Config;

public class SetGameTime extends BaseSubCommand {

    public SetGameTime(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender.isPlayer() && sender.isOp();
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 2) {
            if (args[1].matches("[0-9]*")) {
                if (Integer.parseInt(args[1]) > 5) {
                    Player player = (Player) sender;
                    Config config = horseHunt.getRoomConfig(player.getLevel());
                    config.set("gameTime", Integer.valueOf(args[1]));
                    config.save();
                    sender.sendMessage("§a游戏时间已设置为：" + args[1]);
                } else {
                    sender.sendMessage("§a爆炸等待时间最小不能低于5秒！");
                }
            }else {
                sender.sendMessage("§a时间只能设置为正整数！");
            }
        }else {
            sender.sendMessage("§c请输入要设置的时间！");
        }
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("time", true, CommandParamType.INT) };
    }

}
