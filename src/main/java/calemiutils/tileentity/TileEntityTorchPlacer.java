package calemiutils.tileentity;

import calemiutils.CUConfig;
import calemiutils.gui.ScreenTorchPlacer;
import calemiutils.init.InitTileEntityTypes;
import calemiutils.inventory.ContainerTorchPlacer;
import calemiutils.tileentity.base.TileEntityUpgradable;
import calemiutils.util.Location;
import calemiutils.util.helper.InventoryHelper;
import calemiutils.util.helper.MathHelper;
import calemiutils.util.helper.TorchHelper;
import calemiutils.util.helper.WorldEditHelper;
import net.minecraft.block.Blocks;
import net.minecraft.block.TorchBlock;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

public class TileEntityTorchPlacer extends TileEntityUpgradable {

    private static final TorchBlock TORCH_BLOCK = (TorchBlock) Blocks.TORCH;
    private static final ItemStack TORCH_STACK = new ItemStack(TORCH_BLOCK);

    public TileEntityTorchPlacer () {

        super(InitTileEntityTypes.TORCH_PLACER.get());

        setInputSlots(MathHelper.getCountingArray(0, 28));
        setSideInputSlots(MathHelper.getCountingArray(0, 28));
        setExtractSlots(MathHelper.getCountingArray(2, 28));
        enable = false;
    }

    @Override
    public void tick () {

        boolean dirty = false;

        if (!world.isRemote && enable && hasTorches() && currentRange < getScaledRange()) {

            Location darkSpot = findDarkSpot();

            if (darkSpot == null) {
                currentRange++;
            }

            else if (darkSpot != null) {

                tickProgress();

                dirty = true;

                if (isDoneAndReset()) {

                    darkSpot.setBlock(TORCH_BLOCK);
                    InventoryHelper.consumeItem(this, 1, true, TORCH_STACK);
                }
            }
        }

        else {
            currentRange = 0;
            currentProgress = 0;
        }

        if (dirty) {
            markForUpdate();
        }
    }

    private boolean hasTorches () {

        for (int i = 0; i < getSizeInventory(); i++) {

            if (getStackInSlot(i) != null && getStackInSlot(i).getItem() == Item.getItemFromBlock(TORCH_BLOCK)) {
                return true;
            }
        }

        return false;
    }

    private Location findDarkSpot () {

        ArrayList<Location> locations = WorldEditHelper.selectWallsFromRadius(getLocation(), currentRange, getLocation().y - 5, getLocation().y + 5);

        for (Location nextLocation : locations) {

            if (TorchHelper.canPlaceTorchAt(nextLocation)) {
                return nextLocation;
            }
        }

        return null;
    }

    @Override
    public int getRangeSlot () {
        return 1;
    }

    @Override
    public int getSizeInventory () {
        return 29;
    }

    @Override
    public int getScaledRangeMin () {
        return 10;
    }

    @Override
    public int getScaledRangeMax () {
        return CUConfig.misc.torchPlacerMaxRange.get();
    }

    @Override
    public int getSpeedSlot () {
        return 0;
    }

    @Override
    public int getScaledSpeedMin () {
        return 3;
    }

    @Override
    public int getScaledSpeedMax () {
        return 15;
    }

    @Override
    public int getMaxProgress () {
        return 100;
    }

    @Override
    public ITextComponent getName () {
        return new StringTextComponent("torch placer");
    }

    @Override
    public Container getTileContainer (int windowId, PlayerInventory playerInv) {
        return new ContainerTorchPlacer(windowId, playerInv, this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ContainerScreen getTileGuiContainer (int windowId, PlayerInventory playerInv) {
        return new ScreenTorchPlacer(getTileContainer(windowId, playerInv), playerInv, getDisplayName());
    }

    @Override
    public ITextComponent getDisplayName () {
        return new StringTextComponent("Torch Placer");
    }
}