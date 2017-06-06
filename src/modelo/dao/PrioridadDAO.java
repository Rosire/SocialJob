package modelo.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import modelo.dto.Prioridad;
import modelo.hibernate.config.Sesion;

public class PrioridadDAO {

	private Sesion sesionPostgres;

	public PrioridadDAO() {
		super();
	}
	
	
	public void registrarPrioridad(Prioridad dato) {
		@SuppressWarnings("static-access")
		Session session = sesionPostgres.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.save(dato);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
		}
	}

	public void actualizarPrioridad(Prioridad dato) {
		@SuppressWarnings("static-access")
		Session em = sesionPostgres.openSession();
		Transaction tx = null;
		try {
			tx = em.beginTransaction();
			em.update(dato);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			em.close();
		}
	}

	public void eliminarPrioridad(Prioridad dato) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		Transaction tx = null;
		try {
			tx = sesion.beginTransaction();
			sesion.delete(dato);
			tx.commit();

		} catch (Exception e) {
			tx.rollback();

			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			sesion.close();
		}
	}

	public Prioridad obtenerPrioridad(int id) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		Prioridad dato = null;
		try {
			dato = (Prioridad) sesion.get(Prioridad.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			sesion.close();
		}
		return dato;
	}



	public List<Prioridad> obtenerPrioridades() {
		List<Prioridad> datos = new ArrayList<Prioridad>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Prioridad>) session.createCriteria(Prioridad.class)
					.list();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}

		return datos;

	}



	


}
