package info.u_team.useful_railroads.block;

import java.util.function.Supplier;

import info.u_team.u_team_core.api.ITileEntityBlock;
import info.u_team.u_team_core.api.registry.IUBlockRegistryType;
import info.u_team.useful_railroads.init.UsefulRailroadsItemGroups;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.state.*;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class CustomAdvancedTileEntityRailBlock extends AbstractRailBlock implements IUBlockRegistryType, ITileEntityBlock {
	
	public static final EnumProperty<RailShape> SHAPE = EnumProperty.create("shape", RailShape.class, RailShape.NORTH_SOUTH, RailShape.EAST_WEST);
	
	protected final String name;
	
	protected final BlockItem blockItem;
	
	protected final Supplier<TileEntityType<?>> tileEntityType;
	
	public CustomAdvancedTileEntityRailBlock(String name, Properties properties, Supplier<TileEntityType<?>> tileEntityType) {
		super(false, properties);
		this.name = name;
		blockItem = createBlockItem(new Item.Properties().group(UsefulRailroadsItemGroups.GROUP));
		this.tileEntityType = tileEntityType;
		setDefaultState(getDefaultState().with(SHAPE, RailShape.NORTH_SOUTH));
	}
	
	protected BlockItem createBlockItem(Item.Properties blockItemProperties) {
		return new BlockItem(this, blockItemProperties);
	}
	
	@Override
	public String getEntryName() {
		return name;
	}
	
	@Override
	public BlockItem getBlockItem() {
		return blockItem;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return tileEntityType.get().create();
	}
	
	@Override
	public TileEntityType<?> getTileEntityType(IBlockReader world, BlockPos pos) {
		return tileEntityType.get();
	}
	
	@Override
	public IProperty<RailShape> getShapeProperty() {
		return SHAPE;
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(SHAPE);
	}
	
}
