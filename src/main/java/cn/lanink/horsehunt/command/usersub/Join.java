package cn.lanink.horsehunt.command.usersub;

import cn.lanink.horsehunt.command.base.BaseSubCommand;
import cn.lanink.horsehunt.room.Room;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;

public class Join extends BaseSubCommand {

    public Join(String name) {
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
        Player player = (Player) sender;
        if (horseHunt.getRooms().size() > 0) {
            if (player.riding != null) {
                sender.sendMessage("§a请勿在骑乘状态下进入房间！");
                return true;
            }
            for (Room room : horseHunt.getRooms().values()) {
                if (room.isPlaying(player)) {
                    sender.sendMessage("§c你已经在一个房间中了!");
                    return true;
                }
            }
            if (args.length < 2) {
                for (Room room : horseHunt.getRooms().values()) {
                    if ((room.getMode() == 0 || room.getMode() == 1) && room.getPlayers().size() < room.getMaxPlayers()) {
                        room.joinRoom(player);
                        sender.sendMessage("§a已为你随机分配房间！");
                        return true;
                    }
                }
            }else if (horseHunt.getRooms().containsKey(args[1])) {
                Room room = horseHunt.getRooms().get(args[1]);
                if (room.getMode() == 2 || room.getMode() == 3) {
                    sender.sendMessage("§a该房间正在游戏中，请稍后");
                }else if (room.getPlayers().values().size() >= room.getMaxPlayers()) {
                    sender.sendMessage("§a该房间已满人，请稍后");
                } else {
                    room.joinRoom(player);
                }
                return true;
            }else {
                sender.sendMessage("§a该房间不存在！");
                return true;
            }
        }
        sender.sendMessage("§a暂无房间可用！");
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] {  CommandParameter.newType("RoomName", false, CommandParamType.TEXT) };
    }

}
