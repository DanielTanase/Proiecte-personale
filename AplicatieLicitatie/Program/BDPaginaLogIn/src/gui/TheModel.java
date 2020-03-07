package gui;

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;


//Prin aceasta clasa am adaugat clasei care randeaza obiectele din tabel,
//inca un tip de obiect si anume Icon
//Altfel, nu am fi putut afisa poze in tabel
public class TheModel extends AbstractTableModel {

    private String[] columns;
    private Object[][] rows;
    
    public TheModel(){}
    
    public TheModel(Object[][] data, String[] columnName){
    
        this.rows = data;
        this.columns = columnName;
    }

    
    public Class getColumnClass(int column){
// este de retinut ca pozele pot fi puse doar pe coloana 6
        if(column == 6){
            return Icon.class;
        }
        else{
            return getValueAt(0,column).getClass();
        }
    }
    
    
    public int getRowCount() {
     return this.rows.length;
    }

    public int getColumnCount() {
     return this.columns.length;
    }

    
    public Object getValueAt(int rowIndex, int columnIndex) {
    
    return this.rows[rowIndex][columnIndex];
    }
    public String getColumnName(int col){
        return this.columns[col];
    }


}