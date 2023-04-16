package blusunrize.immersiveengineering.data.models;

import net.minecraft.data.DataGenerator;

import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import com.tom.morewires.MoreImmersiveWires;

public abstract class TRSRItemModelProvider extends ModelProvider<TRSRModelBuilder>
{
	public TRSRItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper)
	{
		super(generator, MoreImmersiveWires.modid, ITEM_FOLDER, TRSRModelBuilder::new, existingFileHelper);
	}
}
