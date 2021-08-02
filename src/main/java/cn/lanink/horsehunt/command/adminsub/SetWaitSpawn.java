package cn.lanink.horsehunt.command.adminsub;

import cn.lanink.horsehunt.command.base.BaseSubCommand;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.Config;

public class SetWaitSpawn extends BaseSubCommand {

    public SetWaitSpawn(String name) {
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
        Player player = (Player) sender;
        Config config = horseHunt.getRoomConfig(player.getLevel());
        String spawn = player.getFloorX() + ":" + player.getFloorY() + ":" + player.getFloorZ();
        String world = player.getLevel().getName();
        config.set("World", world);
        config.set("waitSpawn", spawn);
        config.save();
        sender.sendMessage("§a等待出生点设置成功！");
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }

}
