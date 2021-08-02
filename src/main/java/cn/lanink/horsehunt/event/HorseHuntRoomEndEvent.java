package cn.lanink.horsehunt.event;

import cn.lanink.horsehunt.room.Room;
import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class HorseHuntRoomEndEvent extends HorseHuntRoomPlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public HorseHuntRoomEndEvent(Room room, Player victoryPlayer) {
        this.room = room;
        this.player = victoryPlayer;
    }

}
