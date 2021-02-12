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
package models;

import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author Cristina Domenech <linkedin.com/in/c-domenech github.com/C-Domenech>
 */
@Entity
public class Contacto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Integer id_contacto;

    private String nombre;
    private String apellidos;
    private String empresa;
    private ArrayList<String> telefonos;
    private ArrayList<String> emails;

    /**
     *
     */
    public Contacto() {
    }

    /**
     *
     * @param nombre
     * @param apellidos
     * @param empresa
     * @param telefonos
     * @param emails
     */
    public Contacto(String nombre, String apellidos, String empresa, ArrayList<String> telefonos, ArrayList<String> emails) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.empresa = empresa;
        this.telefonos = telefonos;
        this.emails = emails;
    }

    /**
     *
     * @return
     */
    public Integer getId_contacto() {
        return id_contacto;
    }

    /**
     *
     * @param id_contacto
     */
    public void setId_contacto(Integer id_contacto) {
        this.id_contacto = id_contacto;
    }

    public String getNombre() {
        return nombre;
    }

    /**
     *
     * @param nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     *
     * @return
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     *
     * @param apellidos
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     *
     * @return
     */
    public String getEmpresa() {
        return empresa;
    }

    /**
     *
     * @param empresa
     */
    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public ArrayList<String> getTelefonos() {
        return telefonos;
    }

    /**
     *
     * @param telefonos
     */
    public void setTelefonos(ArrayList<String> telefonos) {
        this.telefonos = telefonos;
    }

    /**
     *
     * @return
     */
    public ArrayList<String> getEmails() {
        return emails;
    }

    /**
     *
     * @param emails
     */
    public void setEmails(ArrayList<String> emails) {
        this.emails = emails;
    }

    /**
     *
     * @return
     */
    public String getTelefonosFormatted() {
        String tlfns = this.getTelefonos().toString().replace("[", "").replace("]", "");
        return tlfns;
    }

    /**
     *
     * @return
     */
    public String getEmailsFormatted() {
        String emls = this.getEmails().toString().replace("[", "").replace("]", "");
        return emls;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "Contacto{" + "id_contacto=" + id_contacto + ", nombre=" + nombre + ", apellidos=" + apellidos + ", empresa=" + empresa + '}';
    }

}
