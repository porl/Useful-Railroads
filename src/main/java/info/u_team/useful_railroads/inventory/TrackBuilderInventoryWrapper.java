package info.u_team.useful_railroads.inventory;

import java.util.function.Supplier;
import java.util.stream.IntStream;

import info.u_team.useful_railroads.init.UsefulRailroadsTags;
import info.u_team.useful_railroads.util.TrackBuilderMode;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.items.*;

public class TrackBuilderInventoryWrapper {
	
	protected final BlockTagItemStackHandler railInventory = new BlockTagItemStackHandler(UsefulRailroadsTags.Items.TRACK_BUILDER_RAILS, 9);
	protected final BlockTagItemStackHandler groundBlockInventory = new BlockTagItemStackHandler(UsefulRailroadsTags.Items.TRACK_BUILDER_GROUND_BLOCKS, 27);
	protected final BlockTagItemStackHandler redstoneTorchInventory = new BlockTagItemStackHandler(UsefulRailroadsTags.Items.TRACK_BUILDER_REDSTONE_TORCHES, 2);
	protected final IItemHandler fuelInventory;
	
	protected int fuel = 0;
	
	protected TrackBuilderMode mode = TrackBuilderMode.MODE_NOAIR;
	
	private TrackBuilderInventoryWrapper(Supplier<World> worldSupplier) {
		fuelInventory = new TrackBuilderFuelItemHandler(worldSupplier, fuelAdder -> fuel += fuelAdder);
	}
	
	public BlockTagItemStackHandler getRailInventory() {
		return railInventory;
	}
	
	public BlockTagItemStackHandler getGroundBlockInventory() {
		return groundBlockInventory;
	}
	
	public BlockTagItemStackHandler getRedstoneTorchInventory() {
		return redstoneTorchInventory;
	}
	
	public IItemHandler getFuelInventory() {
		return fuelInventory;
	}
	
	public void readItemStack() {
	}
	
	public void writeItemStack() {
	}
	
	public int getFuel() {
		return fuel;
	}
	
	public void setFuel(int fuel) {
		this.fuel = fuel;
	}
	
	public TrackBuilderMode getMode() {
		return mode;
	}
	
	public void setMode(TrackBuilderMode mode) {
		this.mode = mode;
	}
	
	public static class Client extends TrackBuilderInventoryWrapper {
		
		public Client(int fuel, TrackBuilderMode mode, Supplier<World> worldSupplier) {
			super(worldSupplier);
			this.fuel = fuel;
			this.mode = mode;
		}
		
	}
	
	public static class Server extends TrackBuilderInventoryWrapper {
		
		private final ItemStack stack;
		
		public Server(ItemStack stack, Supplier<World> worldSupplier) {
			super(worldSupplier);
			this.stack = stack;
			readItemStack();
		}
		
		@Override
		public void readItemStack() {
			readItemHandler(railInventory, "rail");
			readItemHandler(groundBlockInventory, "ground_block");
			readItemHandler(redstoneTorchInventory, "redstone_torch");
			
			final CompoundNBT compound = stack.getTag();
			if (compound != null) {
				fuel = compound.getInt("fuel");
				mode = TrackBuilderMode.byName(compound.getString("mode"));
			}
		}
		
		@Override
		public void writeItemStack() {
			writeItemHandler(railInventory, "rail");
			writeItemHandler(groundBlockInventory, "ground_block");
			writeItemHandler(redstoneTorchInventory, "redstone_torch");
			
			if (fuel > 0) {
				final CompoundNBT compound = stack.getOrCreateTag();
				compound.putInt("fuel", fuel);
			} else {
				final CompoundNBT compound = stack.getTag();
				if (compound != null) {
					compound.remove("fuel");
				}
			}
			
			if (mode != TrackBuilderMode.MODE_NOAIR) {
				final CompoundNBT compound = stack.getOrCreateTag();
				compound.putString("mode", mode.getName());
			} else {
				final CompoundNBT compound = stack.getTag();
				if (compound != null) {
					compound.remove("mode");
				}
			}
		}
		
		private void readItemHandler(ItemStackHandler handler, String childCompoundName) {
			final CompoundNBT childCompound = stack.getChildTag(childCompoundName);
			if (childCompound != null) {
				handler.deserializeNBT(childCompound);
			}
		}
		
		private void writeItemHandler(ItemStackHandler handler, String childCompoundName) {
			if (IntStream.range(0, handler.getSlots()).mapToObj(handler::getStackInSlot).allMatch(ItemStack::isEmpty)) {
				stack.removeChildTag(childCompoundName);
			} else {
				stack.getOrCreateTag().put(childCompoundName, handler.serializeNBT());
			}
		}
	}
}