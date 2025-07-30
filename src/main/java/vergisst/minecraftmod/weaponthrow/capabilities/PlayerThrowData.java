package vergisst.minecraftmod.weaponthrow.capabilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import vergisst.minecraftmod.weaponthrow.handler.ConfigRegistry;
import vergisst.minecraftmod.weaponthrow.packets.State;

public class PlayerThrowData {

	//Client
	public int MAX_CHARGE = -1;
	
	//Both
	State action = State.NONE;
	
	//Server
	int chargeTime = -1;
	
	ItemStack item = ItemStack.EMPTY;
	
	PlayerEntity user;

	public PlayerThrowData(PlayerEntity player) {
		this.user = player;
	}
	
	public void setAction(State action) {
		this.action = action;
	}
	
	public State getAction() {
		return this.action;
	}
	
	public int getChargeTime() {
		return chargeTime;
	}
	
	public void startCharging(ItemStack stack) {
		this.item=stack.copy();
		chargeTime = PlayerThrowData.getMaximumCharge(user);
	}
	
	public ItemStack getChargingStack() {
		return item;
	}
	
	public void resetCharging() {
		this.action = this.action.equals(State.DURING) ? State.FINISH : this.action;
		this.item = ItemStack.EMPTY;
		chargeTime = -1;
	}

	public void setChargeTime(int ticks) {
		chargeTime = ticks;
	}
	
	public static int getMaximumCharge(PlayerEntity player) {
		return MathHelper.floor(player.getAttackCooldownProgressPerTick()* ConfigRegistry.COMMON.get().times.castTimeMuliplier);
	}
}
