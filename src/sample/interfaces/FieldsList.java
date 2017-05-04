package sample.interfaces;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.objects.FieldDB;

public class FieldsList implements MetadataList {

    private ObservableList<FieldDB> list = FXCollections.observableArrayList();

    public ObservableList<FieldDB> getList() {
        return list;
    }

    @Override
    public void add(FieldDB fieldDB) {
        list.add(fieldDB);
    }

    @Override
    public void remove(FieldDB fieldDB) {
        list.remove(fieldDB);
    }
}
