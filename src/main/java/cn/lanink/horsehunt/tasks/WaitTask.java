package cn.lanink.horsehunt.tasks;

import cn.lanink.horsehunt.HorseHunt;
import cn.lanink.horsehunt.event.HorseHuntRoomStartEvent;
import cn.lanink.horsehunt.room.Room;
import cn.lanink.horsehunt.utils.Tools;
import cn.nukkit.Player;
import cn.nukkit.level.Sound;
import cn.nukkit.scheduler.PluginTask;

import java.util.LinkedList;

public class WaitTask extends PluginTask<HorseHunt> {

    private final Room room;

    public WaitTask(HorseHunt owner, Room room) {
        super(owner);
        this.room = room;
    }

    @Override
    public void onRun(int i) {
        if (this.room.getMode() != 1) {
            this.cancel();
            return;
        }
        if (this.room.getPlayers().size() >= this.room.getMinPlayers()) {
            if (this.room.waitTime > 0) {
                this.room.waitTime--;
                if (this.room.waitTime <= 5) {
                    Tools.playSound(this.room, Sound.RANDOM_CLICK);
                }
                LinkedList<String> ms = new LinkedList<>();
                ms.add("玩家: " + room.getPlayers().size() + "/" + this.room.getMaxPlayers());
                ms.add("开始倒计时: " + room.waitTime);

                for (Player player : this.room.getPlayers().keySet()) {
                    owner.getIScoreboard().showScoreboard(player, "HorseHunt", ms);
                }
            }else {
                owner.getServer().getPluginManager().callEvent(new HorseHuntRoomStartEvent(this.room));
                this.cancel();
            }
        }else if (this.room.getPlayers().size() > 0) {
            if (this.room.waitTime != this.room.getSetWaitTime()) {
                this.room.waitTime = this.room.getSetWaitTime();
            }
            LinkedList<String> ms = new LinkedList<>();
            ms.add("玩家: " + this.room.getPlayers().size() + "/" + this.room.getMaxPlayers());
            ms.add("最少需要" + this.room.getMinPlayers() + "人");
            for (Player player : this.room.getPlayers().keySet()) {
                owner.getIScoreboard().showScoreboard(player, "HorseHunt", ms);
            }
        }else {
            this.room.endGame();
            this.cancel();
        }
    }

}
