package calemiutils.gui;

import calemiutils.CalemiUtils;
import calemiutils.config.CUConfig;
import calemiutils.gui.base.ButtonRect;
import calemiutils.gui.base.ContainerScreenBase;
import calemiutils.inventory.ContainerBank;
import calemiutils.item.ItemWallet;
import calemiutils.packet.PacketBank;
import calemiutils.tileentity.TileEntityBank;
import calemiutils.util.helper.CurrencyHelper;
import calemiutils.util.helper.MathHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenBank extends ContainerScreenBase<ContainerBank> {

    public ScreenBank (Container container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }

    @Override
    protected void init () {
        super.init();

        TileEntityBank bank = (TileEntityBank) getTileEntity();

        addButton(new ButtonRect(getScreenX() + (getGuiSizeX() / 2 - 25) + 30, getScreenY() + 40, 50, "Withdraw", (btn) -> withdraw(bank)));
        addButton(new ButtonRect(getScreenX() + (getGuiSizeX() / 2 - 25) - 30, getScreenY() + 40, 50, "Deposit", (btn) -> deposit(bank)));
    }

    /**
     * Called when the withdraw button is pressed.
     * Handles withdrawals from the Bank.
     */
    private void withdraw (TileEntityBank bank) {

        //Checks if there is a Wallet in the Wallet slot.
        if (bank.getInventory().getStackInSlot(1).getItem() instanceof ItemWallet) {

            ItemStack walletStack = bank.getInventory().getStackInSlot(1);
            int walletBalance = ItemWallet.getBalance(walletStack);

            int amountToAdd = MathHelper.getAmountToAdd(walletBalance, bank.storedCurrency, CUConfig.wallet.walletCurrencyCapacity.get());

            //If the Wallet can fit the currency, add it and subtract it from the Bank.
            if (amountToAdd > 0) {
                CurrencyHelper.withdrawFromBank(bank, amountToAdd);
                CurrencyHelper.depositToWallet(walletStack, amountToAdd);
            }

            //If the Wallet can't fit all the money, get how much is needed to fill it, then only used that much.
            else {

                int amountToFill = MathHelper.getAmountToFill(walletBalance, bank.storedCurrency, CUConfig.wallet.walletCurrencyCapacity.get());

                if (amountToFill > 0) {
                    CurrencyHelper.withdrawFromBank(bank, amountToFill);
                    CurrencyHelper.depositToWallet(walletStack, amountToFill);
                }
            }

            //Syncs the Bank's currency to the server.
            CalemiUtils.network.sendToServer(new PacketBank(bank.storedCurrency, ItemWallet.getBalance(bank.getInventory().getStackInSlot(1)), bank.getPos()));
        }
    }

    /**
     * Called when the deposit button is pressed.
     * Handles deposits from the Bank.
     */
    private void deposit (TileEntityBank bank) {

        //Checks if there is a Wallet in the Wallet slot.
        if (bank.getInventory().getStackInSlot(1).getItem() instanceof ItemWallet) {

            ItemStack walletStack = bank.getInventory().getStackInSlot(1);
            int walletBalance = ItemWallet.getBalance(walletStack);

            int amountToAdd = MathHelper.getAmountToAdd(bank.storedCurrency, walletBalance, bank.getMaxCurrency());

            //If the Bank can fit the currency, add it and subtract it from the Wallet.
            if (amountToAdd > 0) {
                CurrencyHelper.depositToBank(bank, amountToAdd);
                CurrencyHelper.withdrawFromWallet(walletStack, amountToAdd);
            }

            //If the Bank can't fit all the money, get how much is needed to fill it, then only used that much.
            else {

                int remainder = MathHelper.getAmountToFill(bank.storedCurrency, walletBalance, bank.getMaxCurrency());

                if (remainder > 0) {

                    CurrencyHelper.depositToBank(bank, remainder);
                    CurrencyHelper.withdrawFromWallet(walletStack, remainder);
                }
            }

            CalemiUtils.network.sendToServer(new PacketBank(bank.storedCurrency, ItemWallet.getBalance(bank.getInventory().getStackInSlot(1)), bank.getPos()));
        }
    }

    @Override
    public void drawGuiBackground (int mouseX, int mouseY) {}

    /**
     * Handles rendering a tab that shows if the Bank is inactive.
     */
    @Override
    public void drawGuiForeground (int mouseX, int mouseY) {

        if (!getTileEntity().enable) {
            addInfoIcon(1);
            addInfoHoveringText(mouseX, mouseY, "Inactive!", "Another Bank is connected in the network!");
        }
    }

    @Override
    public int getGuiSizeY () {
        return 144;
    }

    @Override
    public String getGuiTextureName () {
        return "bank";
    }
}
