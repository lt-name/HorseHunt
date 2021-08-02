package cn.lanink.horsehunt.command.adminsub;

import cn.lanink.horsehunt.command.base.BaseSubCommand;
import cn.lanink.horsehunt.event.HorseHuntRoomStartEvent;
import cn.lanink.horsehunt.room.Room;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

/**
 * @author lt_name
 */
public class StartRoom extends BaseSubCommand {

    public StartRoom(String name) {
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
            if (room.getPlayers().size() >= 2) {
                if (room.getMode() == 1) {
                    Server.getInstance().getPluginManager().callEvent(new HorseHuntRoomStartEvent(room));
                    sender.sendMessage("§a已强制开启游戏！");
                }else {
                    sender.sendMessage("§c房间已经开始了！");
                }
            }else {
                sender.sendMessage("§a房间人数不足2人,无法开始游戏！");
            }
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
