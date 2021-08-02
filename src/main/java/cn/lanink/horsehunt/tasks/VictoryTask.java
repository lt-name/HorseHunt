package cn.lanink.horsehunt.tasks;

import cn.lanink.horsehunt.HorseHunt;
import cn.lanink.horsehunt.event.HorseHuntRoomEndEvent;
import cn.lanink.horsehunt.room.PlayerStatus;
import cn.lanink.horsehunt.room.Room;
import cn.lanink.horsehunt.utils.Tools;
import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;

import java.util.LinkedList;
import java.util.Map;

public class VictoryTask extends PluginTask<HorseHunt> {

    private final Room room;
    private int victoryTime;

    public VictoryTask(HorseHunt owner, Room room) {
        super(owner);
        this.room = room;
        this.victoryTime = 10;
        LinkedList<String> ms = new LinkedList<>();
        ms.add("§e恭喜 " + room.victoryPlayer.getName() + " §e获得胜利");
        for (Player player : this.room.getPlayers().keySet()) {
            owner.getIScoreboard().showScoreboard(player, "HorseHunt", ms);
        }
    }

    @Override
    public void onRun(int i) {
        if (this.room.getMode() != 3) {
            this.cancel();
            return;
        }
        if (this.victoryTime < 1) {
            owner.getServer().getPluginManager().callEvent(new HorseHuntRoomEndEvent(this.room, this.room.victoryPlayer));
            this.room.endGame();
            this.cancel();
        }else {
            this.victoryTime--;
            if (room.getPlayers().size() > 0) {
                for (Map.Entry<Player, PlayerStatus> entry : room.getPlayers().entrySet()) {
                    if (entry.getValue() == PlayerStatus.SURVIVE) {
                        Tools.spawnFirework(entry.getKey());
                    }
                    entry.getKey().sendTip("§e恭喜 " + this.room.victoryPlayer.getName() + " §e获得胜利");
                }
            }
        }
    }

}
