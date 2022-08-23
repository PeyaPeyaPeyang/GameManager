package tokyo.peya.plugins.gamemanager.utils;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Storage
{
    private static final char KEY_SEPARATOR = '.';

    private final HashMap<String, Object> storage;

    public Storage()
    {
        this.storage = new HashMap<>();
    }

    /**
     * 値を設定します。
     * @param key キー
     * @param value 値
     *
     * @throws IllegalStateException 値がすでに設定されており, {@link HashMap} 以外の型の場合
     */
    @SuppressWarnings("unchecked")
    public void put(String key, Object value)
    {
        HashMap<String, Object> current = this.storage;

        String[] keys = StringUtils.split(key, KEY_SEPARATOR);

        for (int i = 0; i < keys.length - 1; i++)
        {
            String currentKey = keys[i];
            if (!current.containsKey(currentKey))
                current.put(currentKey, new HashMap<String, Object>());

            Object currentValue = current.get(currentKey);

            if (currentValue instanceof HashMap)
                current = (HashMap<String, Object>) currentValue;
            else
                throw new IllegalArgumentException("Invalid key: " + key);
        }

        current.put(keys[keys.length - 1], new StorageValue(value));
    }

    /**
     * 値を取得します。
     * @param key キー
     * @return 値
     *
     * @throws IllegalStateException 無効な値が設定されている場合
     */
    @SuppressWarnings("unchecked")
    public @NotNull StorageValue get(String key)
    {
        HashMap<String, Object> current = this.storage;

        String[] keys = StringUtils.split(key, KEY_SEPARATOR);

        for (String currentKey : keys)
        {
            if (!current.containsKey(currentKey))
                return StorageValue.empty();

            Object currentValue = current.get(currentKey);

            if (currentValue instanceof HashMap)
                current = (HashMap<String, Object>) currentValue;
            else if (currentValue instanceof StorageValue)
                return (StorageValue) currentValue;
            else
                throw new IllegalArgumentException("Invalid value: " + key);
        }

        return StorageValue.empty();
    }

}
