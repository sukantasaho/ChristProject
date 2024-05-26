package com.christ.erp.services.common;

import java.util.ArrayList;
import java.util.List;

public class DataTable extends ArrayList<DataRow> {
    private static final long serialVersionUID = 4051849493394455709L;
	private final List<String> _columnNames;
    public List<String> getColumnNames() {
        return this._columnNames;
    }

    private final long _rowsAffected;
    public long getRowsAffected() {
        return this._rowsAffected;
    }

    private final String _statusMessage;
    public String getStatusMessage() {
        return this._statusMessage;
    }

    private final List<DataRow> _rows;
    public List<DataRow> getRows() {
        return this._rows;
    }

    public DataTable(List<DataRow> rows, List<String> columnNames, long rowsAffected, String statusMessage) {
        this._columnNames = (columnNames == null ? new ArrayList<>() : columnNames);
        this._rows = (rows == null ? new ArrayList<>() : rows);
        this._rowsAffected = rowsAffected;
        this._statusMessage = statusMessage;
    }

    public Object getValue(int row, int column) {
        Object value = null;
        try { value = this.getRows().get(row).get(column); }
        catch(Exception ignored) { }
        if(value == null) { value = ""; }
        return value;
    }
    public Object getValue(int row, String column) {
        Object value = null;
        try { value = this.getRows().get(row).get(column); }
        catch(Exception ignored) { }
        if(value == null) { value = ""; }
        return value;
    }
    public boolean isEmpty() {
        return this.getRows() == null ||
            this.getColumnNames() == null ||
            this.getRows().size() == 0 ||
            this.getColumnNames().size() == 0;
    }
}
