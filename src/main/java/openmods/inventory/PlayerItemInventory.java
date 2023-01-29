package openmods.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import openmods.Log;
import openmods.utils.ItemUtils;

public class PlayerItemInventory extends ItemInventory {

    private final EntityPlayer player;
    private final int inventorySlot;
    private final int selfId = System.identityHashCode(this);

    private boolean isValid = true;

    private static final String TAG_INV = "openedInGui";

    private static void setGuiId(ItemStack stack, int id) {
        ItemUtils.getItemTag(stack).setInteger(TAG_INV, id);
    }

    private static int getGuiId(ItemStack stack) {
        return ItemUtils.getItemTag(stack).getInteger(TAG_INV);
    }

    // Potentially unsecure: player can switch item before inventory opens
    public PlayerItemInventory(EntityPlayer player, int size) {
        this(player, size, player.inventory.currentItem);
    }

    public PlayerItemInventory(EntityPlayer player, int size, int inventorySlot) {
        super(player.inventory.getStackInSlot(inventorySlot), size);
        this.player = player;
        this.inventorySlot = inventorySlot;
        setGuiId(containerStack, selfId);
    }

    private void invalidate(EntityPlayer player) {
        Log.info("Player %s tried to trigger item duplication bug", player);
        this.isValid = false;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        if (!(player == this.player && player.inventory.currentItem == inventorySlot)) {
            invalidate(player);
            return false;
        }

        if (!player.worldObj.isRemote) {
            final ItemStack currentItem = player.inventory.getCurrentItem();
            if (currentItem == null || !currentItem.isItemEqual(containerStack) || getGuiId(currentItem) != selfId) {
                invalidate(player);
                return false;
            }
        }

        return true;
    }

    @Override
    public void onInventoryChanged(int slotNumber) {
        super.onInventoryChanged(slotNumber);
        if (isValid) player.inventory.setInventorySlotContents(inventorySlot, containerStack);
    }
}
