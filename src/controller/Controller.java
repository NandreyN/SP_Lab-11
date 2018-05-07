package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Client;
import model.ClientCollectionParser;
import model.Email;
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
        configureColumnListeners();
        tableView.setItems(clientObservableList);
        tableView.setEditable(true);
        tableView.getColumns().forEach(x -> x.setEditable(true));
        idColumn.setEditable(false);
        tableView.getSelectionModel().setCellSelectionEnabled(true);
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
        addButton.setOnAction((e) -> {
            Client newClient = new Client("", null, "", Client.getMaxId(), -1, null);
            clientObservableList.add(newClient);
        });
        saveButton.setOnAction((e) -> {
            boolean containsInvalid = clientObservableList.stream().anyMatch(x -> !x.isValid());
            if (containsInvalid) {
                new Alert(Alert.AlertType.ERROR, "Fill in missing fields").showAndWait();
                return;
            }
            // save here
        });
    }

    private void configureColumnListeners() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setId(event.getNewValue());
        });

        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        ageColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setAge(event.getNewValue());
        });

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setName(event.getNewValue());
        });

        eMailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        eMailColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setEmail(new Email(event.getNewValue()));
        });

        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setPhone(event.getNewValue());
        });

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setOnEditCommit(event -> {
            String stringStatus = event.getNewValue();
            Client.Status newStatus;
            switch (stringStatus.toLowerCase()) {
                case "standard":
                    newStatus = Client.Status.STANDARD;
                    break;
                case "premium":
                    newStatus = Client.Status.PREMIUM;
                    break;
                default:
                    new Alert(Alert.AlertType.ERROR, "Invalid client type").showAndWait();
                    return;
            }
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setStatus(newStatus);
        });
    }
}
