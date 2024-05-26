package com.christ.erp.services.common;

import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataRow extends ArrayList<Object> {
    private static final long serialVersionUID = -7594179480212006509L;
	private final DataTable _table;
    public DataTable getTable() {
        return this._table;
    }

    public DataRow(DataTable table, List<Object> items) {
        this._table = table;
        this.addAll(items);
    }

    @Nullable
    public Object get(@NotNull String column) {
        return this.get(this.getColumnIndex(column));
    }
    @Nullable
    public String getString(int index) {
        return (String) this.get(index);
    }
    @Nullable
    public String getString(@NotNull String column) {
        return (String) this.get(column);
    }
    @Nullable
    public Integer getInt(int index) {
        return (int) this.get(index);
    }
    @Nullable
    public Integer getInt(@NotNull String column) {
        return (int) this.get(column);
    }
    @Nullable
    public Long getLong(int index) {
        return (long) this.get(index);
    }
    @Nullable
    public Long getLong(@NotNull String column) {
        return (long) this.get(column);
    }
    @Nullable
    public Boolean getBoolean(int index) {
        return this.getBooleanFromValue(this.get(index));
    }
    @Nullable
    public Boolean getBoolean(@NotNull String column) {
        return this.getBooleanFromValue(this.get(column));
    }
    @Nullable
    public Byte getByte(@NotNull String column) {
        return (Byte) this.get(column);
    }
    @Nullable
    public Byte getByte(int index) {
        return (Byte) this.get(index);
    }
    @Nullable
    public LocalDateTime getDate(int index) {
        return (LocalDateTime) this.get(index);
    }
    @Nullable
    public LocalDateTime getDate(@NotNull String column) {
        return (LocalDateTime) this.get(column);
    }
    @Nullable
    public Float getFloat(int index) {
        return (float) this.get(index);
    }
    @Nullable
    public Float getFloat(@NotNull String column) {
        return (float) this.get(column);
    }
    @Nullable
    public Double getDouble(int index) {
        return (double) this.get(index);
    }
    @Nullable
    public Double getDouble(@NotNull String column) {
        return (double) this.get(column);
    }

    private boolean getBooleanFromValue(Object value) {
        return (Boolean) value;
    }
    private int getColumnIndex(@NotNull String column) {
        if(this._table == null) {
            return -1;
        }
        else {
            return this.getTable().getColumnNames().indexOf(column);
        }
    }
}

