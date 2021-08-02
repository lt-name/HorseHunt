package cn.lanink.horsehunt.command.adminsub;

import cn.lanink.horsehunt.command.base.BaseSubCommand;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

public class UnloadRoom extends BaseSubCommand {

    public UnloadRoom(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender.isOp();
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        this.horseHunt.unloadRooms();
        sender.sendMessage("§a已卸载所有房间！请在后台查看信息！");
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }

}
