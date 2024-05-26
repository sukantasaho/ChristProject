package com.christ.erp.services.common;

import java.util.ArrayList;

public class DataSet extends ArrayList<DataTable> {
    private static final long serialVersionUID = -1445193366309788651L;
	public DataSet() {

    }
    public boolean isEmpty() {
        if(this.size() > 0) {
            for(DataTable table : this) {
                if(table.isEmpty() == true) {
                    return false;
                }
            }
        }
        return true;
    }
}
