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
import com.jfoenix.controls.JFXTextField;
import database.DBManager;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private AnchorPane pNuevoContacto;
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

    DBManager DB = new DBManager();
    private boolean nuevoContactoEsVisible;
    private boolean ContactosEsVisible;
    private boolean HomeEsVisible;

    /**
     * Method that refresh the data of the table
     */
    public void actualizarTabla() {
        // Set the data that is going to be shown in the TableView
        cNombre.setCellValueFactory(new PropertyValueFactory<Contacto, String>("nombre"));
        cApellidos.setCellValueFactory(new PropertyValueFactory<Contacto, String>("apellidos"));
        cEmpresa.setCellValueFactory(new PropertyValueFactory<Contacto, String>("empresa"));
        cTlfn.setCellValueFactory(new PropertyValueFactory<Contacto, ArrayList>("telefonos"));
        cEmail.setCellValueFactory(new PropertyValueFactory<Contacto, ArrayList>("emails"));
        // Clear table
        tbContactos.getItems().clear();
        // Set objects Evento from the database
        tbContactos.setItems(DB.listarContactos());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pNuevoContacto.setVisible(false);
        btnHome.fire();
        actualizarTabla();
    }

    @FXML
    private void abrirPanelNuevoContacto(ActionEvent event) {
        if (nuevoContactoEsVisible) {
            pNuevoContacto.setVisible(false);
            nuevoContactoEsVisible = false;
        } else {
            pNuevoContacto.setVisible(true);
            nuevoContactoEsVisible = true;
        }
    }

    @FXML
    private void cancelarCerrarPane(ActionEvent event) {
        tfNombre.clear();
        tfApellidos.clear();
        tfEmpresa.clear();
        tfTelefono.clear();
        tfEmail.clear();
        pNuevoContacto.setVisible(false);
        nuevoContactoEsVisible = false;

    }

    @FXML
    private void guardarContacto(ActionEvent event) {
        if (checkData()) {
            String nombre = tfNombre.getText();
            String apellidos = tfApellidos.getText();
            String empresa = tfEmpresa.getText();

            String tlfns[] = tfTelefono.getText().split(" \\| ");
            String emls[] = tfEmail.getText().split(" \\| ");

            ArrayList<String> telefonos = new ArrayList<>();
            ArrayList<String> emails = new ArrayList<>();

            telefonos.addAll(Arrays.asList(tlfns));
            emails.addAll(Arrays.asList(emls));
//            PRUEBA EMAIL EXISTE EN DB
            boolean existe = DB.comprobarEmailExiste(emails);
            if (existe) {
                System.out.println("EXISTEE!!");
            } else {
                System.out.println("NO EXISTEE!");
            }
            Contacto c = new Contacto(nombre, apellidos, empresa, telefonos, emails);
            DB.crearContacto(c);
            btnCancelar.fire();
            actualizarTabla();
        }
    }

    @FXML
    private void anadirOtroTelefono(KeyEvent event) {
        tfTelefono.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if (t.getCode() == KeyCode.ENTER) {
                    tfTelefono.setText(tfTelefono.getText() + " | ");
                    Robot r = new Robot();
                    r.keyPress(KeyCode.END);
                }
            }
        });
    }

    @FXML
    private void anadirOtroEmail(KeyEvent event) {
        tfEmail.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if (t.getCode() == KeyCode.ENTER) {
                    tfEmail.setText(tfEmail.getText() + " | ");
                    Robot r = new Robot();
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
            if (tlfn.length() != 9) {
                lbError.setText("Introduzca un teléfono válido");
                validTlfn = false;
                break;
            } else {
                lbError.setText("");
                validTlfn = true;
            }
        }
        return validTlfn;
    }

    @FXML
    private void activarHome(ActionEvent event) {
        tbContactos.setVisible(false);
        btnHome.setStyle("-fx-border-width: 0 0 0 3;\n-fx-border-color: #ffffff;");
        btnContactos.setStyle(null);
    }

    @FXML
    private void activarContactos(ActionEvent event) {
        tbContactos.setVisible(true);
        btnContactos.setStyle("-fx-border-width: 0 0 0 3;\n-fx-border-color: #ffffff;");
        btnHome.setStyle(null);
    }

}
