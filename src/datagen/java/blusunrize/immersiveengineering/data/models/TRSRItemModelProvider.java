/*
 * BluSunrize
 * Copyright (c) 2023
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.data.models;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public abstract class TRSRItemModelProvider extends ModelProvider<TRSRModelBuilder>
{
	public TRSRItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper)
	{
		super(output, modid, ITEM_FOLDER, TRSRModelBuilder::new, existingFileHelper);
	}
}
