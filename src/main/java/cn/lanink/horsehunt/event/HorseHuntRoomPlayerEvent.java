package cn.lanink.horsehunt.event;

import cn.lanink.horsehunt.room.Room;
import cn.nukkit.event.player.PlayerEvent;


public abstract class HorseHuntRoomPlayerEvent extends PlayerEvent {

    protected Room room;

    public HorseHuntRoomPlayerEvent() {

    }

    public Room getRoom() {
        return this.room;
    }

}
