package modelo.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import modelo.dto.NotificacionTareaAsignada;
import modelo.hibernate.config.Sesion;

public class NotificacionTareaAsignadaDAO {

	private Sesion sesionPostgres;

	public NotificacionTareaAsignadaDAO() {
		super();
	}

	public void registrarNotificacionTareaAsignada(
			NotificacionTareaAsignada dato) {
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

	public void actualizarNotificacionTareaAsignada(
			NotificacionTareaAsignada dato) {
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

	public long contarNotificacionesTareasAsignadasPorEmpleadoPorLeer(
			int idEmpleado) {
		Session session = sesionPostgres.openSession();
		long dato = 0;
		try {
			dato = (Long) session
					.createQuery(
							"SELECT COUNT(nta.id) FROM NotificacionTareaAsignada nta WHERE nta.empleado.id =:idEmpleado AND nta.estatusNotificacion.nombre =:estatus")
					.setParameter("idEmpleado", idEmpleado)
					.setParameter("estatus", "POR LEER").uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			session.close();
		}
		return dato;
	}

	public List<NotificacionTareaAsignada> obtenerNotificacionesTareasAsignadasPorEmpleado(
			int idEmpleado) {
		List<NotificacionTareaAsignada> datos = new ArrayList<NotificacionTareaAsignada>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<NotificacionTareaAsignada>) session
					.createQuery(
							"FROM NotificacionTareaAsignada nta WHERE nta.empleado.id =:idEmpleado")
					.setParameter("idEmpleado", idEmpleado).list();
		} catch (Exception e) {

			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			session.close();
		}

		return datos;
	}

	public NotificacionTareaAsignada obtenerNotificacionTareaAsignada(int id) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		NotificacionTareaAsignada dato = null;
		try {
			dato = (NotificacionTareaAsignada) sesion.get(
					NotificacionTareaAsignada.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			sesion.close();
		}
		return dato;

	}

}
