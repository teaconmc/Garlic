package org.teacon.nocaet.data;

import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.teacon.nocaet.GarlicMod;

public class GarlicData {

    public static void register(GatherDataEvent event) {
        event.getGenerator().addProvider(new GarlicBlockModelGenerator(event.getGenerator(), GarlicMod.MODID, event.getExistingFileHelper()));
    }
}
