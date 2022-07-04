package org.teacon.nocaet.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.data.LanguageProvider;
import org.teacon.nocaet.GarlicRegistry;

import java.util.Map;

public class GarlicLangGenerator extends LanguageProvider {

    private final Map<String, String> prefix = Map.ofEntries();
    private final Map<String, String> suffix = Map.ofEntries(
        Map.entry("_light", "Lighted"),
        Map.entry("_shadow", "Shadowed")
    );

    public GarlicLangGenerator(DataGenerator gen, String modid, String locale) {
        super(gen, modid, locale);
    }

    @Override
    protected void addTranslations() {
        add(((TranslatableComponent) GarlicRegistry.TAB.getDisplayName()).getKey(), "noCaeT");
        for (var entry : GarlicRegistry.BLOCKS.getEntries()) {
            var content = new StringBuilder();
            var path = entry.getId().getPath();
            for (var ent : prefix.entrySet()) {
                if (path.startsWith(ent.getKey())) {
                    path = path.substring(ent.getKey().length());
                    content.append(ent.getValue()).append(" ");
                }
            }
            for (var ent : suffix.entrySet()) {
                if (path.endsWith(ent.getKey())) {
                    path = path.substring(0, path.length() - ent.getKey().length());
                    content.append(ent.getValue()).append(" ");
                }
            }
            for (var s : path.split("_")) {
                content.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
            }
            add(entry.get(), content.toString().trim());
        }
        addMisc();
    }

    private void addMisc() {
        add("nocaet.flame.tooltip", "TeaCon 2022 Collections");
        add("nocaet.command.progress", "Set progress to %s");
    }
}
