package calemiutils.tileentity;

import calemiutils.CUConfig;
import calemiutils.gui.ScreenBank;
import calemiutils.init.InitTileEntityTypes;
import calemiutils.inventory.ContainerBank;
import calemiutils.item.ItemCurrency;
import calemiutils.security.ISecurity;
import calemiutils.security.SecurityProfile;
import calemiutils.tileentity.base.ICurrencyNetworkBank;
import calemiutils.tileentity.base.ICurrencyNetworkUnit;
import calemiutils.tileentity.base.TileEntityInventoryBase;
import calemiutils.util.Location;
import calemiutils.util.VeinScan;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class TileEntityBank extends TileEntityInventoryBase implements ICurrencyNetworkBank, ISecurity {

    private final SecurityProfile profile = new SecurityProfile();
    public List<Location> connectedUnits = new ArrayList<>();
    public int storedCurrency = 0;
    private VeinScan scan;

    public TileEntityBank () {
        super(InitTileEntityTypes.BANK.get());

        setInputSlots(0, 1);
        setSideInputSlots(0, 1);
        setExtractSlots(0, 1);
    }

    @Override
    public void tick () {
        super.tick();

        if (getLocation() != null && scan == null) {
            scan = new VeinScan(getLocation());
        }

        if (scan != null) {

            if (world.getGameTime() % 40 == 0) {

                connectedUnits.clear();

                boolean foundAnotherBank = false;

                scan.reset();
                scan.startNetworkScan(getConnectedDirections());

                for (Location location : scan.buffer) {

                    if (!location.equals(getLocation()) && location.getTileEntity() instanceof TileEntityBank) {
                        foundAnotherBank = true;
                    }

                    if (location.getTileEntity() instanceof ICurrencyNetworkUnit) {

                        ICurrencyNetworkUnit unit = (ICurrencyNetworkUnit) location.getTileEntity();

                        connectedUnits.add(location);

                        if (unit.getBankLocation() == null) {

                            unit.setBankLocation(getLocation());
                        }
                    }
                }

                enable = !foundAnotherBank;
            }
        }

        if (!world.isRemote) {

            if (getStackInSlot(0) != null && getStackInSlot(0).getItem() instanceof ItemCurrency) {

                int amountToAdd = ((ItemCurrency) getStackInSlot(0).getItem()).value;
                int stackSize = 0;

                for (int i = 0; i < getStackInSlot(0).getCount(); i++) {

                    if (canAddAmount(amountToAdd)) {
                        stackSize++;
                        amountToAdd += ((ItemCurrency) getStackInSlot(0).getItem()).value;
                    }
                }

                if (stackSize != 0) {

                    addCurrency(stackSize * ((ItemCurrency) getStackInSlot(0).getItem()).value);
                    decrStackSize(0, stackSize);
                }
            }
        }
    }

    @Override
    public Direction[] getConnectedDirections () {
        return Direction.values();
    }

    private boolean canAddAmount (int amount) {
        int storedAmount = storedCurrency;
        return storedAmount + amount <= getMaxCurrency();
    }

    public void addCurrency (int amount) {
        markForUpdate();
        setCurrency(storedCurrency + amount);
    }    @Override
    public int getSizeInventory () {
        return 2;
    }

    @Override
    public int getStoredCurrency () {
        return storedCurrency;
    }

    @Override
    public int getMaxCurrency () {
        return CUConfig.economy.bankCurrencyCapacity.get();
    }

    @Override
    public void setCurrency (int amount) {

        int setAmount = amount;

        if (amount > getMaxCurrency()) {
            setAmount = getMaxCurrency();
        }

        storedCurrency = setAmount;
    }

    @Override
    public SecurityProfile getSecurityProfile () {
        return profile;
    }

    @Override
    public ITextComponent getName () {
        return new StringTextComponent("bank");
    }

    @Override
    public Container getTileContainer (int windowId, PlayerInventory playerInv) {
        return new ContainerBank(windowId, playerInv, this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ContainerScreen getTileGuiContainer (int windowId, PlayerInventory playerInv) {
        return new ScreenBank(getTileContainer(windowId, playerInv), playerInv, getDisplayName());
    }



    @Override
    public ITextComponent getDisplayName () {
        return new StringTextComponent("Bank");
    }
}