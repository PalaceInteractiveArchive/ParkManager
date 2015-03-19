package us.mcmagic.magicassistant.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.server.v1_8_R2.*;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.mcmagiccore.uuidconverter.UUIDConverter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class AutographUtil {

    private static final LoadingCache<FieldKey, Field> fieldCache = CacheBuilder.newBuilder().build(new CacheLoader<FieldKey, Field>() {
        @Override
        public Field load(FieldKey key) throws Exception {
            Field out = key.clazz.getDeclaredField(key.name);
            out.setAccessible(true);
            return out;
        }
    });

    private static Object getField(Object object, String key) {
        try {
            return fieldCache.getUnchecked(new FieldKey(object.getClass(), key)).get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setField(Object object, String key, Object value) {
        try {
            fieldCache.getUnchecked(new FieldKey(object.getClass(), key)).set(object, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setStaticField(Class<?> clazz, String key, Object value) {
        try {
            fieldCache.getUnchecked(new FieldKey(clazz, key)).set(null, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object getStaticField(Class<?> clazz, String key) {
        try {
            return fieldCache.getUnchecked(new FieldKey(clazz, key)).get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public AutographUtil() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(MagicAssistant.getInstance(),
                PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS, PacketType.Play.Client.BLOCK_PLACE,
                PacketType.Play.Client.WINDOW_CLICK, PacketType.Play.Client.SET_CREATIVE_SLOT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacketType().equals(PacketType.Play.Server.SET_SLOT)) {
                    ItemStack current = (ItemStack) getField(event.getPacket().getHandle(), "c");// Get the current ItemStack
                    if (current != null && current.getItem() != null && (current.getItem() == Items.WRITTEN_BOOK ||
                            current.getItem() == Items.WRITABLE_BOOK) && current.getTag() != null) {
                        rewriteBookOut(current.getTag());
                    }
                } else if (event.getPacketType().equals(PacketType.Play.Server.WINDOW_ITEMS)) {
                    ItemStack[] stacks = (ItemStack[]) getField(event.getPacket().getHandle(), "b");
                    for (ItemStack stack : stacks) {
                        if (stack != null && stack.getItem() != null && (stack.getItem() == Items.WRITTEN_BOOK ||
                                stack.getItem() == Items.WRITABLE_BOOK) && stack.getTag() != null) {
                            rewriteBookOut(stack.getTag());
                        }
                    }
                } else {
                    MagicAssistant.getInstance().getLogger().log(Level.WARNING, "Tried to handle unknown packet type: "
                            + event.getPacketType());
                }
            }

            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType().equals(PacketType.Play.Client.BLOCK_PLACE)) {
                    ItemStack current = (ItemStack) getField(event.getPacket().getHandle(), "e");
                    if (current != null && current.getItem() != null && (current.getItem() == Items.WRITTEN_BOOK || current.getItem() == Items.WRITABLE_BOOK) && current.getTag() != null) {
                        rewriteBookIn(current.getTag());
                    }
                } else if (event.getPacketType().equals(PacketType.Play.Client.WINDOW_CLICK)) {
                    ItemStack current = (ItemStack) getField(event.getPacket().getHandle(), "item");
                    if (current != null && current.getItem() != null && (current.getItem() == Items.WRITTEN_BOOK || current.getItem() == Items.WRITABLE_BOOK) && current.getTag() != null) {
                        rewriteBookIn(current.getTag());
                    }
                } else if (event.getPacketType().equals(PacketType.Play.Client.SET_CREATIVE_SLOT)) {
                    ItemStack current = (ItemStack) getField(event.getPacket().getHandle(), "b");
                    if (current != null && current.getItem() != null && (current.getItem() == Items.WRITTEN_BOOK || current.getItem() == Items.WRITABLE_BOOK) && current.getTag() != null) {
                        rewriteBookIn(current.getTag());
                    }
                } else {
                    MagicAssistant.getInstance().getLogger().log(Level.WARNING, "Tried to handle unknown packet type: "
                            + event.getPacketType());
                }
            }
        });
    }

    private void rewriteBookIn(NBTTagCompound tag) {
        if (tag.hasKeyOfType("tagMcMagicOriginal", 10)) {
            tag.set("tag", tag.getCompound("tagMcMagicOriginal"));
        }
    }

    /**
     * Rewrites the book NBT going from the server to the client
     *
     * @param tag The book NBT
     */
    private void rewriteBookOut(NBTTagCompound tag) {
        if (tag.hasKeyOfType("tag", 10)) {// Check if it has the usual book tag
            NBTTagCompound bookData = tag.getCompound("tag");
            MagicAssistant.getInstance().getLogger().log(Level.INFO, "Found book sending with bookData: " +
                    bookData.toString());
            if (bookData.hasKeyOfType("pages", 9)) {
                NBTTagCompound bookDataOriginal = (NBTTagCompound) bookData.clone();
                NBTTagList pages = bookData.getList("pages", 8);
                MagicAssistant.getInstance().getLogger().log(Level.INFO, "Found pages: " + pages.toString());
                for (int i = 0; i < pages.size(); i++) {
                    String s = pages.getString(i);
                    s = stripUuids(s);
                    pages.a(i, new NBTTagString(s));
                }
                tag.set("tagMcMagicOriginal", bookDataOriginal);
            }
        }
    }

    /**
     * Convert UUIDs to player names in all of the provided strings. Modifies them in-place
     *
     * @param input The list to iterate through.
     */
    public static void stripUuid(List<String> input) {
        for (int i = 0; i < input.size(); i++) {
            input.add(i, stripUuids(input.get(i)));
        }
    }

    /**
     * Parses the string for UUIDs and replaces them with player names.
     *
     * @param input The input to parse. Should have tags similar to <uuid></uuid>
     * @return The parsed data
     */
    public static String stripUuids(String input) {
        StringBuilder out = new StringBuilder();
        String[] beginnings = input.split("<uuid>");
        out.append(beginnings[0]);
        for (int i = 1; i < beginnings.length; i++) {
            String[] endings = beginnings[i].split("</uuid>");
            if (endings.length != 2) {
                throw new RuntimeException("Malformed UUID tag in '" + input + "'");
            } else {
                UUID uuid = UUID.fromString(endings[0]);
                Map<UUID, String> userCache = new HashMap<>(MagicAssistant.userCache);
                if (!userCache.containsKey(uuid)) {
                    String name = UUIDConverter.convert(uuid.toString());
                    MagicAssistant.userCache.put(uuid, name);
                }
                out.append(MagicAssistant.userCache.get(uuid)).append(endings[1]);
            }
        }
        return out.toString();
    }

    static class FieldKey {
        private final Class<?> clazz;
        private final String name;

        public FieldKey(Class<?> clazz, String name) {
            this.clazz = clazz;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FieldKey fieldKey = (FieldKey) o;
            return clazz.equals(fieldKey.clazz) && name.equals(fieldKey.name);
        }

        @Override
        public int hashCode() {
            int result = clazz.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }
    }
}
