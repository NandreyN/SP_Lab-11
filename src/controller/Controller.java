package controller;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Client;
import model.ClientCollectionParser;
import model.Email;
import model.Validator;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class Controller {

    @FXML
    private TableView<Client> tableView;

    @FXML
    private MenuItem deleteButton, addButton, saveButton;
    @FXML
    private TableColumn<Client, Integer> idColumn, ageColumn;
    @FXML
    private TableColumn<Client, String> phoneColumn, statusColumn, eMailColumn, nameColumn;
    @FXML
    private TextField idTextField, nameTextField, emailTextField, phoneTextField, ageTextField, statusTextField;

    @FXML
    private MenuBar menuBar;

    private Validator validator = new Validator();
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
        configureInputControls();
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
            boolean name = validator.isValid(nameTextField.getText(), Validator.Modes.WORD),
                    address = validator.isValid(emailTextField.getText(), Validator.Modes.EMAIL),
                    phone = validator.isValid(phoneTextField.getText(), Validator.Modes.PHONE),
                    age = validator.isValid(ageTextField.getText(), Validator.Modes.ID),
                    status = validator.isValid(statusTextField.getText().toLowerCase(), Validator.Modes.USER_STATUS);
            if (!(name && phone && age && address && status))
                return;

            Client newClient = new Client(nameTextField.getText(), new Email(emailTextField.getText()),
                    phoneTextField.getText(), Client.getMaxId(), Integer.parseInt(ageTextField.getText()),
                    (statusTextField.getText().equals("premium") ? Client.Status.PREMIUM : Client.Status.STANDARD));
            clientObservableList.add(newClient);
        });
        saveButton.setOnAction((e) -> {
            boolean containsInvalid = clientObservableList.stream().anyMatch(x -> !x.isValid());
            if (containsInvalid) {
                new Alert(Alert.AlertType.ERROR, "Fill in missing fields").showAndWait();
                return;
            }
            // save here
            try {
                ClientCollectionParser.save(clientObservableList);
            } catch (TransformerException | ParserConfigurationException e1) {
                e1.printStackTrace();
            }
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

    private void configureInputControls() {
        nameTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> reflectState(nameTextField, Validator.Modes.WORD, newValue, oldValue));
        emailTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> reflectState(emailTextField, Validator.Modes.EMAIL, newValue, oldValue));
        phoneTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> reflectState(phoneTextField, Validator.Modes.PHONE, newValue, oldValue));
        ageTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> reflectState(ageTextField, Validator.Modes.ID, newValue, oldValue));
        statusTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> reflectState(statusTextField, Validator.Modes.USER_STATUS,
                        newValue.toLowerCase(), oldValue.toLowerCase()));
    }

    private void reflectState(TextField textField, Validator.Modes mode, String newValue, String oldValue) {
        if (!validator.isValid(newValue, mode)) {
            if (!textField.getStyleClass().contains("error"))
                textField.getStyleClass().add("error");
        } else {
            textField.getStyleClass().remove("error");
        }
    }
}
