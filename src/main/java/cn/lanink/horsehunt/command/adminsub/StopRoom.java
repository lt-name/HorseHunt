package cn.lanink.horsehunt.command.adminsub;

import cn.lanink.horsehunt.command.base.BaseSubCommand;
import cn.lanink.horsehunt.room.Room;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

/**
 * @author lt_name
 */
public class StopRoom extends BaseSubCommand {

    public StopRoom(String name) {
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
        Room room = this.horseHunt.getRooms().get(player.getLevel().getName());
        if (room != null) {
            room.endGame();
            sender.sendMessage("§a已强制结束房间！");
        }else {
            sender.sendMessage("§a当前地图不是游戏房间！");
        }
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
