package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Client;
import model.ClientCollectionParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Controller {

    @FXML
    private TableView<Client> tableView;

    @FXML
    private MenuItem deleteButton, addButton, saveButton;

    @FXML
    private TableColumn<Client, Integer> idColumn;
    @FXML
    private TableColumn<Client, Integer> ageColumn;
    @FXML
    private TableColumn<Client, String> nameColumn;
    @FXML
    private TableColumn<Client, String> eMailColumn;
    @FXML
    private TableColumn<Client, String> phoneColumn;
    @FXML
    private TableColumn<Client, String> statusColumn;

    @FXML
    private MenuBar menuBar;


    private ObservableList<Client> clientObservableList =
            FXCollections.observableArrayList(new ClientCollectionParser().getCollection());

    public Controller() throws IOException, SAXException, ParserConfigurationException {
    }

    @FXML
    protected void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        eMailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        tableView.setItems(clientObservableList);

        configureMenuCommands();
    }

    private void configureMenuCommands() {
        deleteButton.setOnAction((e) -> {
            Client selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            clientObservableList.remove(selected);
            // reflect deletions on id (if max deleted)
            if (selected.isMaximal())
                Client.decreaseId();
        });
    }
}
