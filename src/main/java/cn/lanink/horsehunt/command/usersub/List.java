package cn.lanink.horsehunt.command.usersub;

import cn.lanink.horsehunt.command.base.BaseSubCommand;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

public class List extends BaseSubCommand {

    public List(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender.isPlayer();
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        StringBuilder list = new StringBuilder();
        for (String string : this.horseHunt.getRooms().keySet()) {
            list.append(string).append(" ");
        }
        sender.sendMessage("§e房间列表： §a " + list);
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }

}
