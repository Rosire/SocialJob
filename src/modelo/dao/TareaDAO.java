package modelo.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import modelo.dto.Cargo;
import modelo.dto.Tarea;
import modelo.hibernate.config.Sesion;

public class TareaDAO {

	private Sesion sesionPostgres;

	public TareaDAO() {
		super();
	}

	public void registrarTarea(Tarea dato) {
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

	public void actualizarTarea(Tarea dato) {
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

	public List<Tarea> obtenerTareas() {
		List<Tarea> datos = new ArrayList<Tarea>();
		@SuppressWarnings("static-access")
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Tarea>) session.createCriteria(Tarea.class).list();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}

		return datos;
	}

	public Tarea obtenerTarea(int id) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		Tarea dato = null;
		try {
			dato = (Tarea) sesion.get(Tarea.class, id);
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

	public Tarea obtenerTareaPorNombrePorEmpresa(String nombre, int idEmpresa) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		Tarea dato = null;
		try {
			dato = (Tarea) sesion
					.createQuery(
							"FROM Tarea t WHERE t.nombre = :nombre AND t.empresa.id = :idEmpresa")
					.setParameter("nombre", nombre)
					.setParameter("idEmpresa", idEmpresa).uniqueResult();
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

	@SuppressWarnings({ "static-access", "unchecked" })
	public List<Tarea> obtenerTareasActivasPorEmpresa(int idEmpresa) {

		List<Tarea> datos = new ArrayList<Tarea>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<Tarea>) em
					.createQuery(
							"FROM Tarea t WHERE t.estatus.nombre = :estatus AND t.empresa.id = :idEmpresa")
					.setParameter("estatus", "ACTIVO")
					.setParameter("idEmpresa", idEmpresa).list();
		} catch (Exception e) {
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			em.close();
		}

		return datos;
	}

	public List<Tarea> obtenerTareasInactivasPorEmpresa(int idEmpresa) {
		List<Tarea> datos = new ArrayList<Tarea>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<Tarea>) em
					.createQuery(
							"FROM Tarea t WHERE t.estatus.nombre = :estatus AND t.empresa.id = :idEmpresa")
					.setParameter("estatus", "INACTIVO")
					.setParameter("idEmpresa", idEmpresa).list();
		} catch (Exception e) {
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			em.close();
		}

		return datos;
	}

}
