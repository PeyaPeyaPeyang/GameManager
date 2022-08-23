package tokyo.peya.plugins.gamemanager.utils;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

/**
 * ストレージの値です。
 */
@Data
public class StorageValue
{
    private @Nullable Object value;
    private boolean readonly;

    public StorageValue(@Nullable Object value)
    {
        this.value = value;
        this.readonly = false;
    }

    /**
     * 値を読み取り専用に設定します。
     */
    public void readonly()
    {
        this.readonly = true;
    }

    /**
     * 値が null かどうかを返します。
     * @return null かどうか
     */
    public boolean isNull()
    {
        return this.value == null;
    }

    /**
     * 値を設定します。
     * @param value 値
     * @throws IllegalStateException このストレージが読み取り専用の場合
     */
    public void setValue(@Nullable Object value)
    {
        if (this.readonly)
            throw new IllegalStateException("This value is readonly.");
        this.value = value;
    }

    /**
     * {@link String} として取得します。
     * @return {@link String}
     */
    public String getAsString()
    {
        return this.value == null ? "null": this.value.toString();
    }

    /**
     * {@link Integer} として取得します。
     * @return {@link Integer}
     *
     * @throws ClassCastException 型が不正な場合
     */
    public Integer getAsInteger()
    {
        return this.value == null ? null: (Integer) this.value;
    }

    /**
     * {@link Double} として取得します。
     * @return {@link Double}
     *
     * @throws ClassCastException 型が不正な場合
     */
    public Double getAsDouble()
    {
        return this.value == null ? null: (Double) this.value;
    }

    /**
     * {@link Boolean} として取得します。
     * @return {@link Boolean}
     *
     * @throws ClassCastException 型が不正な場合
     */
    public Boolean getAsBoolean()
    {
        return this.value == null ? null: (Boolean) this.value;
    }

    /**
     * {@link Long} として取得します。
     * @return {@link Long}
     *
     * @throws ClassCastException 型が不正な場合
     */
    public Long getAsLong()
    {
        return this.value == null ? null: (Long) this.value;
    }

    /**
     * {@link Short} として取得します。
     * @return {@link Short}
     *
     * @throws ClassCastException 型が不正な場合
     */
    public Short getAsShort()
    {
        return this.value == null ? null: (Short) this.value;
    }

    /**
     * {@link Byte} として取得します。
     * @return {@link Byte}
     *
     * @throws ClassCastException 型が不正な場合
     */
    public Byte getAsByte()
    {
        return this.value == null ? null: (Byte) this.value;
    }

    /**
     * {@link Float} として取得します。
     * @return {@link Float}
     *
     * @throws ClassCastException 型が不正な場合
     */
    public Float getAsFloat()
    {
        return this.value == null ? null: (Float) this.value;
    }

    /**
     * {@link Character} として取得します。
     * @return {@link Character}
     *
     * @throws ClassCastException 型が不正な場合
     */
    public Character getAsCharacter()
    {
        return this.value == null ? null: (Character) this.value;
    }

    /**
     * 空の StorageValue を返します。
     * @return 空の Storage Value
     */
    public static StorageValue empty()
    {
        return new StorageValue(null);
    }
}
