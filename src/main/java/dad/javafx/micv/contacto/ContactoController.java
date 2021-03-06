package dad.javafx.micv.contacto;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import dad.javafx.micv.contacto.model.Contacto;
import dad.javafx.micv.contacto.model.Email;
import dad.javafx.micv.contacto.model.Telefono;
import dad.javafx.micv.contacto.model.TipoTelefono;
import dad.javafx.micv.contacto.model.Web;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ContactoController implements Initializable {

//		model

	private ObjectProperty<Contacto> contacto = new SimpleObjectProperty<>();

//		view
	@FXML
	private SplitPane view;

	@FXML
	private TableView<Telefono> telefonosTable;

	@FXML
	private TableColumn<Telefono, String> numeroColumn;

	@FXML
	private TableColumn<Telefono, TipoTelefono> tipoColumn;

	@FXML
	private Button anadirTelefonoButton;

	@FXML
	private Button eliminarTelefonoButton;

	@FXML
	private TableView<Email> emailTable;

	@FXML
	private TableColumn<Email, String> emailColumn;

	@FXML
	private Button anadirEmailButton;

	@FXML
	private Button eliminarEmailButton;

	@FXML
	private TableView<Web> webTable;

	@FXML
	private TableColumn<Web, String> urlColumn;

	@FXML
	private Button anadirWebButton;

	@FXML
	private Button eliminarWebButton;

	public ContactoController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/contacto/ContactoView.fxml"));
		loader.setController(this);
		loader.load();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		numeroColumn.setCellValueFactory(v -> v.getValue().numeroProperty());
		tipoColumn.setCellValueFactory(v -> v.getValue().tipoProperty());

		emailColumn.setCellValueFactory(v -> v.getValue().direccionProperty());

		urlColumn.setCellValueFactory(v -> v.getValue().urlProperty());

		numeroColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		tipoColumn.setCellFactory(ComboBoxTableCell.forTableColumn(TipoTelefono.values()));

		emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());

		urlColumn.setCellFactory(TextFieldTableCell.forTableColumn());

		contacto.addListener((o, ov, nv) -> onContactoChanged(o, ov, nv));
		



	}

	private void onContactoChanged(ObservableValue<? extends Contacto> o, Contacto ov, Contacto nv) {

		System.out.println("ov=" + ov + "/nv=" + nv);

		if (ov != null) {
			// unbind tabla telefonos
			telefonosTable.itemsProperty().unbindBidirectional(ov.telefonosProperty());
			// unbind tabla email
			emailTable.itemsProperty().unbindBidirectional(ov.emailsProperty());
			// unbind tabla web
			webTable.itemsProperty().unbindBidirectional(ov.websProperty());

		}

		if (nv != null) {
			// bind tabla telefonos
			telefonosTable.itemsProperty().bindBidirectional(nv.telefonosProperty());
			// bind tabla email
			emailTable.itemsProperty().bindBidirectional(nv.emailsProperty());
			// bind tabla web
			webTable.itemsProperty().bindBidirectional(nv.websProperty());
		}
		
		eliminarEmailButton.disableProperty().bind(Bindings.isEmpty(getContacto().emailsProperty()));
		eliminarTelefonoButton.disableProperty().bind(Bindings.isEmpty(getContacto().telefonosProperty()));
		eliminarWebButton.disableProperty().bind(Bindings.isEmpty(getContacto().websProperty()));
		eliminarEmailButton.disableProperty().bind(Bindings.isEmpty(emailTable.getSelectionModel().getSelectedItems()));
		eliminarTelefonoButton.disableProperty().bind(Bindings.isEmpty(telefonosTable.getSelectionModel().getSelectedItems()));
		eliminarWebButton.disableProperty().bind(Bindings.isEmpty(webTable.getSelectionModel().getSelectedItems()));
	}

	public SplitPane getView() {
		return view;
	}

	@FXML
	void onAnadirEmailAction(ActionEvent event) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Nuevo e-mail");
		dialog.setHeaderText("Crear una nueva dirección de correo.");
		dialog.setContentText("E-mail:");
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("/images/cv64x64.png"));
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			Email anadido = new Email();
			anadido.setDireccion(result.get());
			contacto.getValue().getEmails().add(anadido);
		}
	}

	@FXML
	void onAnadirTelefonoAction(ActionEvent event) {
		AnadirTelefonoDialog dialog = new AnadirTelefonoDialog();
		Optional<Telefono> result = dialog.showAndWait();
		if (result.isPresent()) {
			System.out.println(result.get());
			getContacto().getTelefonos().add(result.get());
		}
	}

	@FXML
	void onAnadirWebAction(ActionEvent event) {
		TextInputDialog dialog = new TextInputDialog("http://");
		dialog.setTitle("Nueva Web");
		dialog.setHeaderText("Crear una nueva dirección web.");
		dialog.setContentText("URL:");
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("/images/cv64x64.png"));
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			Web anadida = new Web();
			anadida.setUrl(result.get());
			;
			contacto.getValue().getWebs().add(anadida);
		}
	}

	@FXML
	void onEliminarEmailAction(ActionEvent event) {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Eliminar");
    	alert.setHeaderText("¿Seguro que deseas borrar este registro?");
    	alert.setContentText("Email: " + emailTable.getSelectionModel().getSelectedItem().getDireccion());
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("/images/cv64x64.png"));
    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    	    // ... user chose OK
    		emailTable.getItems().removeAll(emailTable.getSelectionModel().getSelectedItem());
    	} else {
    	    // ... user chose CANCEL or closed the dialog
    	}
		
	}

	@FXML
	void onEliminarTelefonoAction(ActionEvent event) {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Eliminar");
    	alert.setHeaderText("¿Seguro que deseas borrar este registro?");
    	alert.setContentText("Teléfono: " + telefonosTable.getSelectionModel().getSelectedItem().getNumero() +
    			"\nTipo : " + telefonosTable.getSelectionModel().getSelectedItem().getTipo());
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("/images/cv64x64.png"));
    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    	    // ... user chose OK
    		telefonosTable.getItems().removeAll(telefonosTable.getSelectionModel().getSelectedItem());

    	} else {
    	    // ... user chose CANCEL or closed the dialog
    	}
	}

	@FXML
	void onEliminarWebAction(ActionEvent event) {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Eliminar");
    	alert.setHeaderText("¿Seguro que deseas borrar este registro?");
    	alert.setContentText("URL: " + webTable.getSelectionModel().getSelectedItem().getUrl());
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("/images/cv64x64.png"));
    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    	    // ... user chose OK
    		webTable.getItems().removeAll(webTable.getSelectionModel().getSelectedItem());
    	} else {
    	    // ... user chose CANCEL or closed the dialog
    	}
		
	}

	public final ObjectProperty<Contacto> contactoProperty() {
		return this.contacto;
	}

	public final Contacto getContacto() {
		return this.contactoProperty().get();
	}

	public final void setContacto(final Contacto contacto) {
		this.contactoProperty().set(contacto);
	}

}
