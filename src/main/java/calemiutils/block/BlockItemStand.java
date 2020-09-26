package calemiutils.block;

import calemiutils.block.base.BlockInventoryContainerBase;
import calemiutils.gui.ScreenItemStandOptions;
import calemiutils.init.InitItems;
import calemiutils.init.InitTileEntityTypes;
import calemiutils.tileentity.TileEntityItemStand;
import calemiutils.util.Location;
import calemiutils.util.helper.LoreHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class BlockItemStand extends BlockInventoryContainerBase {

    public static final IntegerProperty DISPLAY_ID = IntegerProperty.create("display", 0, 3);
    private static final VoxelShape AABB = Block.makeCuboidShape(1, 0, 1, 15, 16, 15);

    public BlockItemStand () {
        super(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(1, 1).harvestLevel(1).func_226896_b_().variableOpacity());
        setDefaultState(stateContainer.getBaseState().with(DISPLAY_ID, 0));
    }

    /**
     * This method functions the same as onBlockActivated().
     * This will handle opening the guis for options and inventory.
     */
    public ActionResultType func_225533_a_ (BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {

        Location location = new Location(world, pos);
        TileEntity tileEntity = location.getTileEntity();
        ItemStack heldStack = player.getHeldItem(hand);

        if (world.isRemote) {

            if (tileEntity instanceof TileEntityItemStand && heldStack.getItem() == InitItems.SECURITY_WRENCH.get()) {
                openGui(player, (TileEntityItemStand) tileEntity);
            }

            return ActionResultType.SUCCESS;
        }

        else if (heldStack.getItem() != InitItems.SECURITY_WRENCH.get()) {
            return super.func_225533_a_(state, world, pos, player, hand, result);
        }

        return ActionResultType.SUCCESS;
    }

    @OnlyIn(Dist.CLIENT)
    private void openGui (PlayerEntity player, TileEntityItemStand stand) {
        Minecraft.getInstance().displayGuiScreen(new ScreenItemStandOptions(player, stand));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity (IBlockReader worldIn) {
        return InitTileEntityTypes.ITEM_STAND.get().create();
    }

    @Override
    public boolean canEntitySpawn (BlockState state, IBlockReader world, BlockPos pos, EntityType<?> entityType) {
        return false;
    }

    /*
        Methods for Block properties
     */

    @Override
    public boolean isNormalCube (BlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean func_229869_c_ (BlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }

    /*
        Methods for Blocks that are not full and solid cubes.
     */

    @Override
    public VoxelShape getShape (BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return AABB;
    }

    @Override
    public VoxelShape getCollisionShape (BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return AABB;
    }

    @Override
    public boolean propagatesSkylightDown (BlockState state, IBlockReader world, BlockPos pos) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement (BlockItemUseContext context) {
        return stateContainer.getBaseState().with(DISPLAY_ID, 0);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public float func_220080_a (BlockState state, IBlockReader world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected void fillStateContainer (StateContainer.Builder<Block, BlockState> builder) {
        builder.add(DISPLAY_ID);
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        LoreHelper.addInformationLore(tooltip, "Displays Blocks & Items", true);
        LoreHelper.addInformationLore(tooltip, "Can translate, rotate, spin, scale Items & Blocks");
        LoreHelper.addControlsLore(tooltip, "Open Inventory", LoreHelper.Type.USE, true);
        LoreHelper.addControlsLore(tooltip, "Open Display Options", LoreHelper.Type.USE_WRENCH);
    }
}