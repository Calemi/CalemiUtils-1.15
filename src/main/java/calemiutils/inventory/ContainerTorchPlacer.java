package calemiutils.inventory;

import calemiutils.init.InitContainersTypes;
import calemiutils.init.InitItems;
import calemiutils.inventory.base.ContainerBase;
import calemiutils.inventory.base.SlotFilter;
import calemiutils.tileentity.TileEntityTorchPlacer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ContainerTorchPlacer extends ContainerBase {

    public ContainerTorchPlacer (final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(windowId, playerInventory, (TileEntityTorchPlacer) getTileEntity(playerInventory, data));
    }

    public ContainerTorchPlacer (final int windowId, final PlayerInventory playerInventory, final TileEntityTorchPlacer tileEntity) {
        super(InitContainersTypes.TORCH_PLACER.get(), windowId, playerInventory, tileEntity, 8, 110);

        addSlot(new SlotFilter(tileEntity, 0, 178, 7, InitItems.SPEED_UPGRADE.get()));
        addSlot(new SlotFilter(tileEntity, 1, 178, 31, InitItems.RANGE_UPGRADE.get()));
        addTileEntityStorageInv(tileEntity, 2, 8, 52, 3);
    }
}