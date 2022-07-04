package org.teacon.nocaet.data;

import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.teacon.nocaet.GarlicRegistry;

import java.util.List;
import java.util.stream.IntStream;

public class GarlicBlockModelGenerator extends BlockStateProvider {

    private final ExistingFileHelper helper;

    public GarlicBlockModelGenerator(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
        helper = exFileHelper;
    }

    private List<ModelFile.ExistingModelFile> models(String name, int i) {
        return IntStream.rangeClosed(1, i).mapToObj(it -> new ModelFile.ExistingModelFile(modLoc("block/" + name + it), helper)).toList();
    }

    @Override
    protected void registerStatesAndModels() {
        var leaves = models("leaves", 2);
        var strip = models("strip", 2);
        var wood = models("wood", 4);
        for (var entry : GarlicRegistry.BLOCKS.getEntries()) {
            var block = entry.get();
            if (block.getRegistryName().getPath().contains("leaves")) {
                this.getVariantBuilder(block)
                    .partialState()
                    .addModels(leaves.stream()
                        .flatMap(model -> IntStream.of(90, 180, 270)
                            .mapToObj(i -> ConfiguredModel.builder().modelFile(model).rotationY(i).buildLast())
                        ).toArray(ConfiguredModel[]::new));
                this.itemModels().singleTexture(block.getRegistryName().getPath(), mcLoc("item/generated"),
                    "layer0", modLoc("block/leaves01"));
            } else if (block.getRegistryName().getPath().contains("log_strip")) {
                this.axisBlock(block, strip);
            } else if (block.getRegistryName().getPath().contains("log")) {
                this.axisBlock(block, wood);
            }
        }
    }

    private void axisBlock(Block block, List<? extends ModelFile> models) {
        this.getVariantBuilder(block)
            .partialState()
            .with(RotatedPillarBlock.AXIS, Direction.Axis.X)
            .addModels(models.stream()
                .map(model -> ConfiguredModel.builder().modelFile(model).rotationX(90).rotationY(90).buildLast())
                .toArray(ConfiguredModel[]::new))
            .partialState()
            .with(RotatedPillarBlock.AXIS, Direction.Axis.Y)
            .addModels(models.stream()
                .map(model -> ConfiguredModel.builder().modelFile(model).buildLast())
                .toArray(ConfiguredModel[]::new))
            .partialState()
            .with(RotatedPillarBlock.AXIS, Direction.Axis.Z)
            .addModels(models.stream()
                .map(model -> ConfiguredModel.builder().modelFile(model).rotationX(90).buildLast())
                .toArray(ConfiguredModel[]::new));
        this.simpleBlockItem(block, models.get(0));
    }
}
