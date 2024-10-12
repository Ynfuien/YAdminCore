package pl.ynfuien.yessentials.colors;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.util.Index;
import org.bukkit.entity.Player;
import pl.ynfuien.yessentials.utils.Messenger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class ColorFormatter {
    public static final MiniMessage SERIALIZER = MiniMessage.builder()
            .postProcessor(new LegacyPostProcessor())
            .build();

    public static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexCharacter('#')
            .hexColors()
            .build();

    // Tag resolvers by permission
    private final HashMap<String, TagResolver> tagResolvers;


    private final String permissionBase;
    public ColorFormatter(String permissionBase, Set<YTagResolver> excluded) {
        this.permissionBase = permissionBase;

        tagResolvers = createTagResolverMapByPermission(permissionBase, excluded);
    }
    public ColorFormatter(String permissionBase) {
        this.permissionBase = permissionBase;

        tagResolvers = createTagResolverMapByPermission(permissionBase,
                Set.of(YTagResolver.CLICK,
                YTagResolver.HOVER,
                YTagResolver.INSERTION,
                YTagResolver.NEWLINE,
                YTagResolver.SCORE)
        );
    }


    // Gets tag resolvers with provided permission base
    public static HashMap<String, TagResolver> createTagResolverMapByPermission(String permissionBase, Set<YTagResolver> excludedResolvers) {
        HashMap<String, TagResolver> tagResolvers = new HashMap<>();

        for (YTagResolver yResolver : YTagResolver.values()) {
            if (excludedResolvers.contains(yResolver)) continue;
            tagResolvers.put(yResolver.getPermission(permissionBase), yResolver.getResolver());
        }


        Index<String, NamedTextColor> colorsIndex = NamedTextColor.NAMES;
        for (String colorName : colorsIndex.keys()) {
            tagResolvers.put(String.format("%s.color.%s", permissionBase, colorName), SingleColorTagResolver.of(colorsIndex.value(colorName)));
        }

        for (TextDecoration decoration : TextDecoration.values()) {
            tagResolvers.put(String.format("%s.decoration.%s", permissionBase, decoration.name()), StandardTags.decorations(decoration));
        }

        return tagResolvers;
    }


    // Parses player's text with legacy, MiniMessage and PAPI formats
    public Component format(Player p, String text) {
        if (p.hasPermission(permissionBase + ".papi")) text = Messenger.parsePAPI(p, text);
        Component formatted = parseFormats(p, text);
//        if (p.hasPermission(permissionBase + ".papi")) formatted = parsePAPI(p, formatted, text);

        return formatted;
    }

//    // Parses PAPI placeholders in provided component
//    public static Component parsePAPI(Player p, Component messageComponent, String message) {
//        if (!Hooks.isPapiEnabled()) return messageComponent;
//        if (!PlaceholderAPI.containsPlaceholders(message)) return messageComponent;
//
//        Matcher matcher = PlaceholderAPI.getPlaceholderPattern().matcher(message);
//        while (matcher.find()) {
//            String placeholder = matcher.group(0);
//
//            String parsedPlaceholder = PlaceholderAPI.setPlaceholders(p, placeholder);
//            if (parsedPlaceholder.equals(placeholder)) continue;
//
//            Component formattedPlaceholder = SERIALIZER.deserialize(parsedPlaceholder.replace('§', '&'), TagResolver.standard());
//
//            TextReplacementConfig replacementConfig = TextReplacementConfig
//                    .builder()
//                    .matchLiteral(placeholder)
//                    .replacement(formattedPlaceholder)
//                    .build();
//            messageComponent = messageComponent.replaceText(replacementConfig);
//        }
//
//        return messageComponent;
//    }

    // Checks player's permissions for colors/styles and parses message using those
    private static final Pattern MM_TAG_PATTERN = Pattern.compile("<.+>");
    private  Component parseFormats(Player p, String message) {
        MiniMessage serializer = MiniMessage.builder()
                .postProcessor(new LegacyPostProcessor(p, permissionBase))
                .tags(TagResolver.empty())
                .build();

        if (!MM_TAG_PATTERN.matcher(message).find()) return serializer.deserialize(message);

        List<TagResolver> permittedResolvers = new ArrayList<>();
        for (String perm : tagResolvers.keySet()) {
            if (p.hasPermission(perm)) permittedResolvers.add(tagResolvers.get(perm));
        }

        return serializer.deserialize(message, TagResolver.resolver(permittedResolvers));
    }

    public enum YTagResolver {
        COLOR_HEX(HexColorTagResolver.get()),
        NBT(StandardTags.nbt()),
        CLICK(StandardTags.clickEvent()),
        FONT(StandardTags.font()),
        GRADIENT(StandardTags.gradient()),
        HOVER(StandardTags.hoverEvent()),
        INSERTION(StandardTags.insertion()),
        KEYBIND(StandardTags.keybind()),
        NEWLINE(StandardTags.newline()),
        RAINBOW(StandardTags.rainbow()),
        RESET(StandardTags.reset()),
        SCORE(StandardTags.score()),
        SELECTOR(StandardTags.selector()),
        TRANSITION(StandardTags.transition()),
        TRANSLATABLE(StandardTags.translatable());

        private final TagResolver resolver;
        YTagResolver(TagResolver resolver) {
            this.resolver = resolver;
        }

        public TagResolver getResolver() {
            return resolver;
        }

        public String getName() {
            return name().toLowerCase().replace('_', '.');
        }

        public String getPermission(String base) {
            return base + "." + getName();
        }
    }
}
