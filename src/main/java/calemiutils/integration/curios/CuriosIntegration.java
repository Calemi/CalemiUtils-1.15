package calemiutils.integration.curios;

import calemiutils.init.InitItems;
import calemiutils.item.ItemTorchBelt;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.capability.CuriosCapability;
import top.theillusivec4.curios.api.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CuriosIntegration {

    private static final ItemStack modelStack = new ItemStack(InitItems.WALLET.get());

    /**
     * Adds the Curios capability to the Wallet.
     */
    public static ICapabilityProvider walletCapability () {

        ICurio curio = new ICurio() {

            @Override
            public void render (String identifier, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

                ICurio.RenderHelper.translateIfSneaking(matrixStack, livingEntity);
                ICurio.RenderHelper.rotateIfSneaking(matrixStack, livingEntity);

                matrixStack.push();
                matrixStack.translate(0.26D, 0.85D, 0.0D);
                matrixStack.rotate(Vector3f.YP.rotationDegrees(90));
                matrixStack.rotate(Vector3f.XP.rotationDegrees(180));
                matrixStack.scale(0.5F, 0.5F, 0.9F);

                Minecraft.getInstance().getItemRenderer().renderItem(modelStack, ItemCameraTransforms.TransformType.GROUND, combinedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer);

                matrixStack.pop();
            }

            @Override
            public void playEquipSound (LivingEntity entityLivingBase) {
                entityLivingBase.world.playSound(null, entityLivingBase.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            }

            @Override
            public boolean canEquip (String identifier, LivingEntity entityLivingBase) {
                return !CuriosAPI.getCurioEquipped(InitItems.WALLET.get(), entityLivingBase).isPresent();
            }

            @Override
            public boolean hasRender (String identifier, LivingEntity livingEntity) {
                return true;
            }

            @Override
            public boolean canRightClickEquip () {
                return false;
            }
        };

        return new ICapabilityProvider() {

            private final LazyOptional<ICurio> curioOpt = LazyOptional.of(() -> curio);

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability (@Nonnull Capability<T> cap, @Nullable Direction side) {
                return CuriosCapability.ITEM.orEmpty(cap, curioOpt);
            }
        };
    }

    /**
     * Adds the Curios capability to the Torch Belt.
     */
    public static ICapabilityProvider torchBeltCapability () {

        ICurio curio = new ICurio() {

            @Override
            public void onCurioTick (String identifier, int index, LivingEntity livingEntity) {

                if (livingEntity instanceof PlayerEntity) {

                    PlayerEntity player = ((PlayerEntity) livingEntity);

                    if (CuriosAPI.getCurioEquipped(InitItems.TORCH_BELT.get(), player).isPresent()) {
                        ItemTorchBelt.tick(CuriosAPI.getCurioEquipped(InitItems.TORCH_BELT.get(), player).get().right, player.world, player);
                    }
                }
            }

            @Override
            public void playEquipSound (LivingEntity entityLivingBase) {
                entityLivingBase.world.playSound(null, entityLivingBase.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            }

            @Override
            public boolean canEquip (String identifier, LivingEntity entityLivingBase) {
                return !CuriosAPI.getCurioEquipped(InitItems.TORCH_BELT.get(), entityLivingBase).isPresent();
            }

            @Override
            public boolean canRightClickEquip () {
                return false;
            }
        };

        return new ICapabilityProvider() {

            private final LazyOptional<ICurio> curioOpt = LazyOptional.of(() -> curio);

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability (@Nonnull Capability<T> cap, @Nullable Direction side) {
                return CuriosCapability.ITEM.orEmpty(cap, curioOpt);
            }
        };
    }
}
