package sample.objects;

public class FieldDB {
    private String table;
    private String field;
    private String title;

    public FieldDB() {
    }

    public FieldDB(String table, String field) {
        this.table = table;
        this.field = field;
        this.title = table+"."+field;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
