package calemiutils.block;

import calemiutils.block.base.BlockInventoryContainerBase;
import calemiutils.init.InitTileEntityTypes;
import calemiutils.item.ItemLinkBookLocation;
import calemiutils.tileentity.TileEntityBookStand;
import calemiutils.util.Location;
import calemiutils.util.helper.LoreHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
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
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class BlockBookStand extends BlockInventoryContainerBase {

    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    private static final VoxelShape AABB = Block.makeCuboidShape(3, 0, 3, 13, 12, 13);

    public BlockBookStand () {
        super(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(1, 1).harvestLevel(0).func_226896_b_().variableOpacity());
        setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH));
    }

    /**
     * This method functions the same as onBlockActivated().
     * This will handle opening the gui or opening the Link Book's gui.
     */
    @Override
    public ActionResultType func_225533_a_ (BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {

        ItemLinkBookLocation book = getBook(world, pos);
        Location location = new Location(world, pos);
        TileEntity tileEntity = location.getTileEntity();

        TileEntityBookStand tileEntityBookStand = (TileEntityBookStand) tileEntity;

        if (player.getHeldItem(hand).getItem() instanceof ItemLinkBookLocation) {
            return ActionResultType.FAIL;
        }

        if (player.isCrouching()) {

            if (player instanceof ServerPlayerEntity) {

                if (!world.isRemote) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, pos);
                }

                return ActionResultType.SUCCESS;
            }
        }

        else if (book != null) {

            if (world.isRemote) {
                book.openGui(player, hand, ((TileEntityBookStand) tileEntity).getStackInSlot(0), false);
            }

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.FAIL;
    }

    /**
     * Gets the Link Book Item that is inside the Container.
     */
    private ItemLinkBookLocation getBook (World world, BlockPos pos) {

        if (getBookStack(world, pos) != null) {

            ItemStack stack = getBookStack(world, pos);

            if (stack != null && !stack.isEmpty()) {

                if (stack.getItem() instanceof ItemLinkBookLocation) {
                    return (ItemLinkBookLocation) stack.getItem();
                }
            }
        }

        return null;
    }

    /**
     * Gets the Link Book ItemStack that is inside the Container.
     */
    private ItemStack getBookStack (World world, BlockPos pos) {

        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof TileEntityBookStand) {
            TileEntityBookStand bookStand = (TileEntityBookStand) tileEntity;
            return bookStand.getStackInSlot(0);
        }

        return null;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity (IBlockReader worldIn) {
        return InitTileEntityTypes.BOOK_STAND.get().create();
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
        return stateContainer.getBaseState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public float func_220080_a (BlockState state, IBlockReader world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected void fillStateContainer (StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        LoreHelper.addInformationLore(tooltip, "Holds Link Books.");
        LoreHelper.addControlsLore(tooltip, "Open Gui", LoreHelper.Type.USE, true);
        LoreHelper.addControlsLore(tooltip, "Open Inventory", LoreHelper.Type.SNEAK_USE);
        LoreHelper.addControlsLore(tooltip, "Place Book (Copies data from stored book if it exists)", LoreHelper.Type.USE_BOOK);
    }
}