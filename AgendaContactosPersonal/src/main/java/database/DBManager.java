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
package database;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import models.Contacto;

/**
 *
 * @author Cristina Domenech <linkedin.com/in/c-domenech github.com/C-Domenech>
 */
public class DBManager {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("agenda_contactos.odb");
    private EntityManager em = emf.createEntityManager();

    /**
     * Persist a new contact
     *
     * @param c
     */
    public void crearContacto(Contacto c) {
        em.getTransaction().begin();
        em.persist(c);
        em.getTransaction().commit();
//        em.refresh(c);
    }

    /**
     * List all contacts
     *
     * @return
     */
    public ObservableList<Contacto> listarContactos() {
        ObservableList<Contacto> contactos = FXCollections.observableArrayList();
        TypedQuery<Contacto> query = em.createQuery("SELECT c FROM Contacto c", Contacto.class);
        contactos.addAll(query.getResultList());
//        System.out.println(contactos);
        return contactos;
    }

    /**
     * Edit a contact that is in the database
     *
     * @param c
     * @param nombre
     * @param apellidos
     * @param empresa
     * @param telefonos
     * @param emails
     */
    public void editarContacto(Contacto c, String nombre, String apellidos, String empresa, ArrayList<String> telefonos, ArrayList<String> emails) {
        em.getTransaction().begin();
        c.setNombre(nombre);
        c.setApellidos(apellidos);
        c.setEmpresa(empresa);
        c.setTelefonos(telefonos);
        c.setEmails(emails);
        em.getTransaction().commit();
//        em.refresh(c);
    }

    /**
     * Remove a contact from the database
     *
     * @param c
     */
    public void eliminarContacto(Contacto c) {
        em.getTransaction().begin();
        em.remove(c);
        em.getTransaction().commit();
    }

    /**
     * Check if email exists
     *
     * @param emails
     * @return
     */
    public boolean comprobarEmailExiste(ArrayList<String> emails) {
        boolean emailExists = false;
        TypedQuery<Contacto> query = em.createQuery("SELECT c FROM Contacto c", Contacto.class);
        List<Contacto> contactos = query.getResultList();
        for (String email : emails) {
            for (Contacto c : contactos) {
                if (c.getEmails().contains(email)) {
                    emailExists = true;
                    break;
                }
            }
        }
        return emailExists;
    }

    /**
     * Get the companies of every contact
     *
     * @return
     */
    public ObservableList<PieChart.Data> datosContactosPorEmpresa() {
        Query query = em.createQuery("SELECT c.empresa, count(c.empresa) FROM Contacto c GROUP BY c.empresa");
        List<Object[]> results = query.getResultList();

        List<String> empresas = new ArrayList<>();
        List<Long> numContactos = new ArrayList<>();

        for (Object[] result : results) {
            String empresa = (String) result[0];
            Long num = (Long) result[1];

            empresas.add(empresa);
            numContactos.add(num);
        }

        Object[] arrEmpresas = empresas.toArray();
        Object[] arrNumero = numContactos.toArray();

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        // Add every element that need to be shown in the chart
        for (int i = 0; i < empresas.size(); i++) {
            PieChart.Data dato = new PieChart.Data(arrEmpresas[i].toString(), Double.parseDouble(arrNumero[i].toString()));
            pieChartData.add(dato);
        }

        return pieChartData;
    }

}
