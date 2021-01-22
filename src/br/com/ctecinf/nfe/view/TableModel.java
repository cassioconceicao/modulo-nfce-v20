/*
 * Copyright (C) 2021 ctecinf.com.br
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.ctecinf.nfe.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Cássio Conceição
 * @since 2021
 * @version 2021
 * @see http://ctecinf.com.br/
 */
public class TableModel extends AbstractTableModel {

    private final List<Object[]> data;
    private final LinkedHashMap<String, Class> columns;

    public TableModel() {
        this.data = new ArrayList();
        this.columns = new LinkedHashMap();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return (rowIndex >= 0 && rowIndex < data.size()) ? data.get(rowIndex)[columnIndex] : null;
    }

    @Override
    public String getColumnName(int column) {
        return (column >= 0 && column < columns.size()) ? columns.keySet().toArray()[column].toString() : null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return super.getColumnClass(columnIndex);
    }

    public void addColumn(String label, Class<?> type) {
        columns.put(label, type);
    }

    public void addRow(Object... data) {
        this.data.add(data);
        fireTableRowsInserted(this.data.size() - 1, this.data.size() - 1);
    }

    public void removeRow(int rowIndex) {
        if (data.size() > 0 && rowIndex >= 0 && rowIndex < data.size() && data.remove(data.remove(rowIndex))) {
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }

    public void removeAllRows() {
        if (data.size() > 0) {
            int size = data.size() - 1;
            data.clear();
            fireTableRowsDeleted(0, size);
        }
    }
}
