/*
 * Copyright (C) 2021 Cristina Domenech <linkedin.com/in/c-domenech/>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.cdomenech.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import database.DBManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.robot.Robot;

import models.Contacto;

public class MainViewController implements Initializable {

    @FXML
    private Button btnNuevoContacto;
    @FXML
    private Button btnHome;
    @FXML
    private Button btnContactos;
    @FXML
    private AnchorPane pFormularioContacto;
    @FXML
    private Label lbError;
    @FXML
    private JFXButton btnGuardar;
    @FXML
    private JFXButton btnCancelar;
    @FXML
    private JFXTextField tfNombre;
    @FXML
    private JFXTextField tfApellidos;
    @FXML
    private JFXTextField tfEmpresa;
    @FXML
    private JFXTextField tfTelefono;
    @FXML
    private JFXTextField tfEmail;
    @FXML
    private TableView<Contacto> tbContactos;
    @FXML
    private TableColumn<Contacto, String> cNombre;
    @FXML
    private TableColumn<Contacto, String> cApellidos;
    @FXML
    private TableColumn<Contacto, String> cEmpresa;
    @FXML
    private TableColumn<Contacto, ArrayList> cTlfn;
    @FXML
    private TableColumn<Contacto, ArrayList> cEmail;
    @FXML
    private MenuItem miEditar;
    @FXML
    private MenuItem miEliminar;
    @FXML
    private JFXSnackbar snackBarNoti;
    @FXML
    private PieChart pieChartEmpresas;

    DBManager DB = new DBManager();
    private boolean formularioContactoEsVisible;
    private Contacto contactoParaEditar;

    /**
     * Method that refresh the data of the table
     */
    public void actualizarTabla() {
        // Set the data that is going to be shown in the TableView
        cNombre.setCellValueFactory(new PropertyValueFactory<Contacto, String>("nombre"));
        cApellidos.setCellValueFactory(new PropertyValueFactory<Contacto, String>("apellidos"));
        cEmpresa.setCellValueFactory(new PropertyValueFactory<Contacto, String>("empresa"));
        cTlfn.setCellValueFactory(new PropertyValueFactory<Contacto, ArrayList>("telefonosFormatted"));
        cEmail.setCellValueFactory(new PropertyValueFactory<Contacto, ArrayList>("emailsFormatted"));
        // Clear table
        tbContactos.getItems().clear();
        // Set objects Contacto from the database
        tbContactos.setItems(DB.listarContactos());
        // Populate Pie Chart
        pieChartEmpresas.setData(DB.datosContactosPorEmpresa());
    }
    /**
     * Initizalize the view
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pieChartEmpresas.setVisible(true);
        // Get the data from the database and setet data into the Pie Chart
        pieChartEmpresas.setData(DB.datosContactosPorEmpresa());
        // Set the size of the columns in proportion to the window
        cNombre.prefWidthProperty().bind(tbContactos.widthProperty().divide(8));
        cApellidos.prefWidthProperty().bind(tbContactos.widthProperty().divide(8));
        cEmpresa.prefWidthProperty().bind(tbContactos.widthProperty().divide(6));
        cTlfn.prefWidthProperty().bind(tbContactos.widthProperty().divide(4));
        cEmail.prefWidthProperty().bind(tbContactos.widthProperty().divide(3));
        // Set invisible the form pane and go to Home view
        pFormularioContacto.setVisible(false);
        btnHome.fire();
        actualizarTabla();
    }
    /**
     * Open panel and set it to save a new contact
     */
    @FXML
    private void abrirPanelNuevoContacto(ActionEvent event) {
        btnGuardar.setText("GUARDAR");
        if (formularioContactoEsVisible) {
            pFormularioContacto.setVisible(false);
            formularioContactoEsVisible = false;
            btnCancelar.fire();
        } else {
            pFormularioContacto.setVisible(true);
            formularioContactoEsVisible = true;
        }
    }
    /**
     * Cancel operation and clear the data
     */
    @FXML
    private void cancelarCerrarPane(ActionEvent event) {
        tfNombre.clear();
        tfApellidos.clear();
        tfEmpresa.clear();
        tfTelefono.clear();
        tfEmail.clear();
        pFormularioContacto.setVisible(false);
        formularioContactoEsVisible = false;

    }
    /**
     * Save a new contact and refresh table
     */
    @FXML
    private void guardarContacto(ActionEvent event) {
        // Check if data it is correct or not
        if (checkData()) {
            // Get all the inputs
            String nombre = tfNombre.getText();
            String apellidos = tfApellidos.getText();
            String empresa = tfEmpresa.getText();
            // Convert it to Array, then, add to the ArrayList
            String tlfns[] = tfTelefono.getText().split(" \\| ");
            String emls[] = tfEmail.getText().split(" \\| ");

            ArrayList<String> telefonos = new ArrayList<>();
            ArrayList<String> emails = new ArrayList<>();

            telefonos.addAll(Arrays.asList(tlfns));
            emails.addAll(Arrays.asList(emls));
            // Check if it is a new contact or user is editing an existing one
            if (contactoParaEditar == null) {
                // Check if the email exists
                if (!DB.comprobarEmailExiste(emails)) {
                    // Create the contact and send it to DBManager
                    Contacto c = new Contacto(nombre, apellidos, empresa, telefonos, emails);
                    DB.crearContacto(c);
                    // Show a succeed message in a Snack Bar
                    snackBarNoti.enqueue(new JFXSnackbar.SnackbarEvent("Contacto creado correctamente"));
                    // Clear Text Fields and update table
                    btnCancelar.fire();
                    actualizarTabla();
                } else {
                    // Warning
                    lbError.setText("Ya existe un contacto con ese email");
                }
            } else {
                // User is editing a contact, so that send the new info
                DB.editarContacto(contactoParaEditar, nombre, apellidos, empresa, telefonos, emails);
                // Show a succeed message in a Snack Bar
                snackBarNoti.enqueue(new JFXSnackbar.SnackbarEvent("Contacto editado correctamente"));
                // Clear Text Fields and update table
                btnCancelar.fire();
                actualizarTabla();
            }
        }
    }
    /**
     * While writing in the field if the user tap ENTER, it will add a symbol and they could add more tha one number
     */
    @FXML
    private void anadirOtroTelefono(KeyEvent event) {
        tfTelefono.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                // If user tap ENTER
                if (t.getCode() == KeyCode.ENTER) {
                    // Add the symbol and spaces
                    tfTelefono.setText(tfTelefono.getText() + " | ");
                    // Robot class automate a process
                    Robot r = new Robot();
                    // Tap END so the user can continue writting
                    r.keyPress(KeyCode.END);
                }
            }
        });
    }
    /**
     * While writing in the field if the user tap ENTER, it will add a symbol and they could add more tha one email
     */
    @FXML
    private void anadirOtroEmail(KeyEvent event) {
        tfEmail.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                // If user tap ENTER
                if (t.getCode() == KeyCode.ENTER) {
                    // Add the symbol and spaces
                    tfEmail.setText(tfEmail.getText() + " | ");
                    // Robot class automate a process
                    Robot r = new Robot();
                    // Tap END so the user can continue writting
                    r.keyPress(KeyCode.END);
                }
            }
        });
    }

    /**
     * Check if the fields are filled and if the email is correct
     *
     * @return boolean if the fields have been completed and the email is
     * correct
     */
    public boolean checkData() {
        boolean validData;

        if (tfNombre.getText().isBlank() || tfApellidos.getText().isBlank() || tfEmpresa.getText().isBlank() || tfEmail.getText().isBlank() || tfTelefono.getText().isBlank()) {
            validData = false;
            lbError.setText("Todos los campos son obligatorios");
        } else {
            // Check telephone and email
            validData = isValidTelephone() && isValidEmail();
        }
        return validData;
    }

    /**
     * Check if the structure of the email is correct
     *
     * @return boolean
     */
    public boolean isValidEmail() {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        String emls[] = tfEmail.getText().split(" \\| ");
        boolean validEmail = false;
        for (String eml : emls) {
            validEmail = eml.matches(regex);
            if (!validEmail) {
                lbError.setText("Introduzca un email válido");
                break;
            } else {
                lbError.setText("");
            }
        }
        return validEmail;
    }

    /**
     * Check if telephone number is correct
     *
     * @return boolean
     */
    public boolean isValidTelephone() {
        String tlfns[] = tfTelefono.getText().split(" \\| ");
        boolean validTlfn = false;
        for (String tlfn : tlfns) {
            try {
                int t = Integer.parseInt(tlfn);
                // Check length
                if (tlfn.length() != 9) {
                    lbError.setText("Introduzca un teléfono válido");
                    validTlfn = false;
                    break;
                } else {
                    lbError.setText("");
                    validTlfn = true;
                }
            } catch (NumberFormatException e) {
                lbError.setText("Introduzca un teléfono válido");
                validTlfn = false;
            }
        }
        return validTlfn;
    }
    /**
     * Set visible Home Pie Chart and hide the table
     */
    @FXML
    private void activarHome(ActionEvent event) {
        pieChartEmpresas.setVisible(true);
        tbContactos.setVisible(false);
        btnHome.setStyle("-fx-border-width: 0 0 0 3;\n-fx-border-color: #ffffff;");
        btnContactos.setStyle(null);
    }
    /**
     * Set visible contacts table and hide the Pie Chart
     */
    @FXML
    private void activarContactos(ActionEvent event) {
        pieChartEmpresas.setVisible(false);
        tbContactos.setVisible(true);
        btnContactos.setStyle("-fx-border-width: 0 0 0 3;\n-fx-border-color: #ffffff;");
        btnHome.setStyle(null);
    }
    /**
     * Open panel and set it to edit the selected contact
     */
    @FXML
    private void abrirPanelEditarContacto(ActionEvent event) {
        // Get selected contact
        Contacto c = tbContactos.getSelectionModel().getSelectedItem();
        // Save it in a global variable to use it as a flag (that is how we know it is an existing contact)
        contactoParaEditar = c;
        // Set the data of the contact in every Text Field
        if (formularioContactoEsVisible) {
            tfNombre.setText(c.getNombre());
            tfApellidos.setText(c.getApellidos());
            tfEmpresa.setText(c.getEmpresa());

            StringBuilder strTlfn = new StringBuilder();
            for (String tlfn : c.getTelefonos()) {
                strTlfn.append(tlfn).append(" | ");
            }
            String telefonos = strTlfn.toString();

            StringBuilder strEml = new StringBuilder();
            for (String em : c.getTelefonos()) {
                strEml.append(em).append(" | ");
            }
            String emails = strEml.toString();

            tfTelefono.setText(telefonos);
            tfEmail.setText(emails);
            btnGuardar.setText("EDITAR");
        } else {
            pFormularioContacto.setVisible(true);
            formularioContactoEsVisible = true;
            tfNombre.setText(c.getNombre());
            tfApellidos.setText(c.getApellidos());
            tfEmpresa.setText(c.getEmpresa());

            StringBuilder strTlfn = new StringBuilder();
            for (String tlfn : c.getTelefonos()) {
                strTlfn.append(tlfn).append(" | ");
            }
            String telefonos = strTlfn.toString();

            StringBuilder strEml = new StringBuilder();
            for (String em : c.getEmails()) {
                strEml.append(em).append(" | ");
            }
            String emails = strEml.toString();

            tfTelefono.setText(telefonos);
            tfEmail.setText(emails);
            btnGuardar.setText("EDITAR");
        }
    }
    /**
     * Remove selected contact and refresh table
     */
    @FXML
    private void eliminarContacto(ActionEvent event) {
        Contacto c = tbContactos.getSelectionModel().getSelectedItem();
        DB.eliminarContacto(c);
        actualizarTabla();
        // Show a succeed message in a Snack Bar
        snackBarNoti.enqueue(new JFXSnackbar.SnackbarEvent("Contacto eliminado correctamente"));
    }

}
