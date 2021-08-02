package cn.lanink.horsehunt.event;

import cn.lanink.horsehunt.room.Room;
import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class HorseHuntPlayerDeathEvent extends HorseHuntRoomPlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public HorseHuntPlayerDeathEvent(Room room, Player player) {
        this.room = room;
        this.player = player;
    }

}
