package cn.lanink.horsehunt.tasks.game;

import cn.lanink.horsehunt.HorseHunt;
import cn.lanink.horsehunt.room.PlayerStatus;
import cn.lanink.horsehunt.room.Room;
import cn.lanink.horsehunt.tasks.VictoryTask;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.PluginTask;

import java.util.LinkedList;
import java.util.Map;

/**
 * 游戏时间计算
 */
public class TimeTask extends PluginTask<HorseHunt> {

    private final Room room;

    public TimeTask(HorseHunt owner, Room room) {
        super(owner);
        this.room = room;
    }

    public void onRun(int i) {
        if (this.room.getMode() != 2) {
            this.cancel();
            return;
        }

        LinkedList<Player> playerList = new LinkedList<>();
        for (Map.Entry<Player, PlayerStatus> entry : room.getPlayers().entrySet()) {
            if (entry.getValue() == PlayerStatus.SURVIVE) {
                playerList.add(entry.getKey());
            }
        }
        if (playerList.size() <= 1) {
            this.room.victoryPlayer = playerList.poll();
            this.room.setMode(3);
            Server.getInstance().getScheduler().scheduleRepeatingTask(owner,
                    new VictoryTask(owner, this.room), 20);
            return;
        }

        if (this.room.gameTime > 0) {
            this.room.gameTime--;
        }else {
            this.room.endGame();
        }
    }

}
