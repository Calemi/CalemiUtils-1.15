package calemiutils.inventory;

import calemiutils.init.InitContainersTypes;
import calemiutils.init.InitItems;
import calemiutils.inventory.base.ContainerBase;
import calemiutils.inventory.base.SlotFilter;
import calemiutils.tileentity.TileEntityBank;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ContainerBank extends ContainerBase {

    public ContainerBank (final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(windowId, playerInventory, (TileEntityBank) getTileEntity(playerInventory, data));
    }

    public ContainerBank (final int windowId, final PlayerInventory playerInventory, final TileEntityBank tileEntity) {
        super(InitContainersTypes.BANK.get(), windowId, playerInventory, tileEntity, 8, 62);
        addSlot(new SlotFilter(tileEntity, 0, 62, 18, InitItems.COIN_PENNY.get(), InitItems.COIN_NICKEL.get(), InitItems.COIN_QUARTER.get(), InitItems.COIN_DOLLAR.get()));
        addSlot(new SlotFilter(tileEntity, 1, 98, 18, InitItems.WALLET.get()));
    }
}