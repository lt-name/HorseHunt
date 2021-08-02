package cn.lanink.horsehunt.tasks.game;

import cn.lanink.horsehunt.HorseHunt;
import cn.lanink.horsehunt.room.PlayerStatus;
import cn.lanink.horsehunt.room.Room;
import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;

import java.util.LinkedList;


/**
 * 信息显示
 */
public class TipsTask extends PluginTask<HorseHunt> {

    private final Room room;

    public TipsTask(HorseHunt owner, Room room) {
        super(owner);
        this.room = room;
    }

    @Override
    public void onRun(int i) {
        if (this.room.getMode() != 2) {
            this.cancel();
            return;
        }
        if (this.room.getPlayers().size() > 0) {
            int playerNumber = 0;
            for (PlayerStatus status : this.room.getPlayers().values()) {
                if (status == PlayerStatus.SURVIVE) {
                    playerNumber++;
                }
            }
            LinkedList<String> ms = new LinkedList<>();
            ms.add("剩余时间：" + this.room.gameTime);
            ms.add("存活玩家：" + playerNumber);
            for (Player player : this.room.getPlayers().keySet()) {
                owner.getIScoreboard().showScoreboard(player, "HorseHunt", ms);
            }
        }else {
            this.room.endGame();
        }
    }

}
