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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Predicate;
import models.Contacto;

/**
 *
 * @author Cristina Domenech <linkedin.com/in/c-domenech github.com/C-Domenech>
 */
public class DBManager {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("agenda_contactos.odb");
    private EntityManager em = emf.createEntityManager();

    public void crearContacto(Contacto c) {
        em.getTransaction().begin();
        em.persist(c);
        em.getTransaction().commit();
        em.refresh(c);
    }

    public ObservableList<Contacto> listarContactos() {
        ObservableList<Contacto> contactos = FXCollections.observableArrayList();
        TypedQuery<Contacto> query = em.createQuery("SELECT c FROM Contacto c", Contacto.class);
        contactos.addAll(query.getResultList());
        System.out.println(contactos);
        return contactos;
    }

    public void editarContacto(Contacto c, String nombre, String apellidos, String empresa, ArrayList<String> telefonos, ArrayList<String> emails) {
        em.getTransaction().begin();
        c.setNombre(nombre);
        c.setApellidos(apellidos);
        c.setEmpresa(empresa);
        c.setTelefonos(telefonos);
        c.setEmails(emails);
        em.getTransaction().commit();
        em.refresh(c);
    }

    public void eliminarContacto(Contacto c) {
        em.getTransaction().begin();
        em.remove(c);
        em.getTransaction().commit();
    }

    public boolean comprobarEmailExiste(ArrayList<String> emails) {
        boolean emailExists = false;
//        TypedQuery<Contacto> query = em.createQuery("SELECT c FROM Contacto c WHERE c.emails = :emails", Contacto.class);
//        Contacto c = query.setParameter("emails", emails).getSingleResult();
        return emailExists;
    }

}
