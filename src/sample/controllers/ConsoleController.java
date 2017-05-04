package sample.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sample.DBHelper;
import sample.interfaces.FieldsList;
import sample.objects.FieldDB;
import sample.objects.TableDB;

import java.net.URL;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class ConsoleController implements Initializable {

    @FXML
    private TreeTableView<TableDB> tt1_1;
    @FXML
    private TreeTableView<TableDB> tt1_2;
    @FXML
    private TableView<FieldDB> tv1;

    TreeItem<TableDB> root1;
    TreeItem<TableDB> root2;
    private Stage ConsoleStage;

    private FieldsList fieldsList = new FieldsList();

    public void setMainStage(Stage ConsoleStage) {
        this.ConsoleStage = ConsoleStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();
    }



    public void init() {
        try {
            DatabaseMetaData metaData = DBHelper.getConnection().getMetaData();


            ResultSet result = null;


            root1 = new TreeItem<>(new TableDB(DBHelper.getConnection().getCatalog(), "ROOT"));
            root1.setExpanded(false);
            root2 = new TreeItem<>(new TableDB(DBHelper.getConnection().getCatalog(), "ROOT"));
            root2.setExpanded(false);

            ArrayList<String> tables = new ArrayList<>();
            String table[] = {"TABLE"};
            result = metaData.getTables(
                    null, null, null, table);
            while (result.next()) {
//                String tableName = result.getString("TABLE_NAME");
//                root1.getChildren().add(new TreeItem<TableDB>(new TableDB(tableName, "table")));
                tables.add(result.getString("TABLE_NAME"));

            }
            for (String actualTable : tables) {
                result = metaData.getColumns(null, null, actualTable, null);
                TreeItem<TableDB> tableItem = new TreeItem<>(new TableDB(actualTable, "TABLE"));
                while (result.next()) {
                    tableItem.getChildren().add(new TreeItem<>(new TableDB(result.getString("COLUMN_NAME"), result.getString("TYPE_NAME"))));
                }
                root1.getChildren().add(tableItem);
            }


            TreeTableColumn<TableDB, String> col1_1 = new TreeTableColumn<>("Table");
            col1_1.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));

            TreeTableColumn<TableDB, String> col1_2 = new TreeTableColumn<>("Type");
            col1_2.setCellValueFactory(new TreeItemPropertyValueFactory<>("type"));

            TreeTableColumn<TableDB, String> col2_1 = new TreeTableColumn<>("Table");
            col2_1.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));

            TreeTableColumn<TableDB, String> col2_2 = new TreeTableColumn<>("Type");
            col2_2.setCellValueFactory(new TreeItemPropertyValueFactory<>("type"));

            TableColumn<FieldDB, String> col3 = new TableColumn<>("title");
            col3.setCellValueFactory(new PropertyValueFactory<>("title"));

            tt1_1.setRoot(root1);
            tt1_2.setRoot(root2);


            tt1_1.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                    addInTables();
                }

            });

            tt1_2.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                        addInFields();
                    }
                }
            });

            tv1.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                   if(event.getCode().getName().equals("Delete")){
                      fieldsList.remove(tv1.getSelectionModel().getSelectedItem());
                   }
                }
            });

            tt1_1.setShowRoot(false);
            tt1_1.getColumns().add(col1_1);
            tt1_1.getColumns().add(col1_2);

            tt1_2.setShowRoot(false);
            tt1_2.getColumns().add(col2_1);
            tt1_2.getColumns().add(col2_2);

            tv1.getColumns().add(col3);
            tv1.setItems(fieldsList.getList());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addInFields() {
        if (tt1_2.getSelectionModel().getSelectedItem() == null) return;
        TreeItem<TableDB> selectedItem = tt1_2.getSelectionModel().getSelectedItem();
        if (selectedItem.getValue().getType() == "TABLE") {
            String nameTable = selectedItem.getValue().getName();
            for (TreeItem<TableDB> item : selectedItem.getChildren()) {
                fieldsList.add(new FieldDB(nameTable, item.getValue().getName()));
            }
        } else {
            fieldsList.add(new FieldDB(selectedItem.getParent().getValue().getName(), selectedItem.getValue().getName()));
        }
    }

    private void addInTables() {
        if (tt1_1.getSelectionModel().getSelectedItem() == null) return;
        if (tt1_1.getSelectionModel().getSelectedItem().getValue().getType() == "TABLE") {
            ObservableList<TreeItem<TableDB>> children = root2.getChildren();
            TableDB value = tt1_1.getSelectionModel().getSelectedItem().getValue();
            boolean flag = false;
            for (TreeItem<TableDB> child : children) {
                if (child.getValue().equals(value)) flag = true;
            }
            if (!flag) {
                TreeItem<TableDB> itemTable = new TreeItem<>(value);
                for (TreeItem<TableDB> itemColumn : tt1_1.getSelectionModel().getSelectedItem().getChildren()) {
                    itemTable.getChildren().add(new TreeItem<>(itemColumn.getValue()));
                }
                root2.getChildren().add(itemTable);
            }

        }
    }

    public void actionButtonPressed(ActionEvent actionEvent) {
        if (!(actionEvent.getSource() instanceof Button)) return;

//        Person item = (Person) tableAddressBook.getSelectionModel().getSelectedItem();
//        System.out.println(item);
        Button btn = (Button) actionEvent.getSource();
        switch (btn.getId()) {
            case "btnOk":
                showDialog();
                break;
            case "btnClose":
                ConsoleStage.close();
                break;
            case "btnQuery":
                buildQuery();
                break;

        }


    }

    private void buildQuery() {
        StringBuilder sqlQuery = new StringBuilder("select ");
        Map<String, Set<String>> map = new HashMap<>();
        for (FieldDB fieldDB : fieldsList.getList()) {
            map.put(fieldDB.getTable(), new HashSet<>());
        }
        for (String s : map.keySet()) {
            Set<String> set = map.get(s);
            for (FieldDB fieldDB : fieldsList.getList()) {
                if (fieldDB.getTable().equals(s)){
                    set.add(fieldDB.getTitle());
                }
            }
        }

        String selectQuery = "";

        for (FieldDB fieldDB : fieldsList.getList()) {
            if(!selectQuery.isEmpty()) selectQuery+= ", ";
            selectQuery += fieldDB.getTitle();
        }
        sqlQuery.append(selectQuery);

        sqlQuery.append(" from ");

        HashSet<String> set = new HashSet<>();
        for (FieldDB fieldDB : fieldsList.getList()) {
            set.add(fieldDB.getTable());
        }
        selectQuery = "";
        for (String s : set) {
            if(!selectQuery.isEmpty()) selectQuery+= ", ";
            selectQuery += s;
        }
        sqlQuery.append(selectQuery);
        System.out.println(sqlQuery);
    }

    public void showDialog(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Запрос");
        alert.setContentText("Сохранить запрос?");
        alert.setHeaderText("");
        alert.showAndWait();
    }

}
