package calemiutils.util.helper;

import calemiutils.util.Location;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.Direction;

import java.util.EnumSet;
import java.util.Set;

public class EntityHelper {

    public static void teleportPlayer (ServerPlayerEntity entity, Location location) {
        teleport(entity, location);
    }

    private static void teleport (ServerPlayerEntity entity, Location location) {

        Set<SPlayerPositionLookPacket.Flags> set = EnumSet.noneOf(SPlayerPositionLookPacket.Flags.class);

        entity.stopRiding();
        entity.connection.setPlayerLocation(location.x + 0.5F, location.y, location.z + 0.5F, 0, 0, set);

        if (!entity.isElytraFlying()) {
            entity.setMotion(0, 0, 0);
            entity.onGround = true;
        }
    }

    public static boolean canTeleportAt (ServerPlayerEntity entity, Location legLocation) {

        Location groundLocation = new Location(legLocation, Direction.DOWN);
        Location headLocation = new Location(legLocation, Direction.UP);

        System.out.println(groundLocation.toString());

        return groundLocation.doesBlockHaveCollision() && !legLocation.doesBlockHaveCollision() && !headLocation.doesBlockHaveCollision();
    }
}