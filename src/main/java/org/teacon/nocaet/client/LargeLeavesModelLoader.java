package org.teacon.nocaet.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LargeLeavesModelLoader implements IModelLoader<ModelLoaderRegistry.VanillaProxy> {

    @Override
    public ModelLoaderRegistry.VanillaProxy read(JsonDeserializationContext context, JsonObject object) {
        var scale = GsonHelper.getAsFloat(object, "scale", 1.0F);
        var list = this.getModelElements(context, object);
        for (var element : list) {
            element.from.sub(element.rotation.origin);
            element.from.mul(scale);
            element.from.add(element.rotation.origin);
            element.to.sub(element.rotation.origin);
            element.to.mul(scale);
            element.to.add(element.rotation.origin);
        }
        return new ModelLoaderRegistry.VanillaProxy(list);
    }

    private List<BlockElement> getModelElements(JsonDeserializationContext context, JsonObject object) {
        var list = new ArrayList<BlockElement>();
        if (object.has("elements")) {
            for (var element : GsonHelper.getAsJsonArray(object, "elements")) {
                list.add(context.deserialize(element, BlockElement.class));
            }
        }
        return list;
    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {
    }
}
