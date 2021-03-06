package com.jesus_crie.modularbot.core.utils;

import net.dv8tion.jda.core.JDA;

import javax.annotation.Nonnull;
import java.util.List;

public class Utils {

    public static String f(String format, Object... args) {
        return String.format(format, args);
    }

    public static String f(JDA.ShardInfo shardInfo) {
        return f("[" + (shardInfo.getShardId() + 1) + " / " + shardInfo.getShardTotal() + "]");
    }

    public static String fullClassToSimpleClassName(@Nonnull String fullClassName) {
        if (!fullClassName.contains(".")) {
            return fullClassName;
        } else {
            String[] parts = fullClassName.split("\\.");
            return parts[parts.length - 1];
        }
    }

    @SafeVarargs
    public static <T> void addAll(@Nonnull List<? super T> list, int index, T... elements) {
        for (int i = 0; i < elements.length; i++)
            list.add(index + i, elements[i]);
    }
}
