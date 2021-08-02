package cn.lanink.horsehunt.event;

import cn.lanink.horsehunt.room.Room;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class HorseHuntRoomStartEvent extends HorseHuntRoomEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public HorseHuntRoomStartEvent(Room room) {
        this.room = room;
    }

}
