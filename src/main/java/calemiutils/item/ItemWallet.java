package calemiutils.item;

import calemiutils.CUConfig;
import calemiutils.CalemiUtils;
import calemiutils.inventory.ContainerWallet;
import calemiutils.inventory.base.ItemStackInventory;
import calemiutils.item.base.ItemBase;
import calemiutils.util.UnitChatMessage;
import calemiutils.util.helper.ItemHelper;
import calemiutils.util.helper.LoreHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class ItemWallet extends ItemBase {

    public ItemWallet () {
        super(new Item.Properties().group(CalemiUtils.TAB).maxStackSize(1));
    }

    public static UnitChatMessage getMessage (PlayerEntity player) {
        return new UnitChatMessage("Wallet", player);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick (World world, PlayerEntity player, Hand hand) {

        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote && player instanceof ServerPlayerEntity) {

            if (CUConfig.wallet.walletCurrencyCapacity.get() > 0) {
                openGui((ServerPlayerEntity) player, stack, player.inventory.currentItem);
                return new ActionResult<>(ActionResultType.SUCCESS, stack);
            }
        }

        return new ActionResult<>(ActionResultType.FAIL, stack);
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable World world, List<ITextComponent> tooltipList, ITooltipFlag advanced) {
        LoreHelper.addInformationLore(tooltipList, "Used to store currency in one place.");
        LoreHelper.addControlsLore(tooltipList, "Open Inventory", LoreHelper.Type.USE, true);
        LoreHelper.addBlankLine(tooltipList);
        LoreHelper.addCurrencyLore(tooltipList, getBalance(stack), CUConfig.wallet.walletCurrencyCapacity.get());
    }

    public static int getBalance (ItemStack stack) {
        return ItemHelper.getNBT(stack).getInt("balance");
    }

    private void openGui (ServerPlayerEntity player, ItemStack stack, int selectedSlot) {

        NetworkHooks.openGui(player, new SimpleNamedContainerProvider(
            (id, playerInventory, openPlayer) -> new ContainerWallet(id, playerInventory, new ItemStackInventory(stack, 1), player.inventory.currentItem), stack.getDisplayName()),
            (buffer) -> buffer.writeVarInt(selectedSlot));
    }
}