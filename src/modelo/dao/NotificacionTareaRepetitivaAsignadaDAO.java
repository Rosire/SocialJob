package modelo.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import modelo.dto.NotificacionTareaRepetitivaAsignada;
import modelo.hibernate.config.Sesion;

public class NotificacionTareaRepetitivaAsignadaDAO {

	private Sesion sesionPostgres;

	public NotificacionTareaRepetitivaAsignadaDAO() {
		super();
	}

	public void registrarNotificacionTareaRepetitivaAsignada(
			NotificacionTareaRepetitivaAsignada dato) {
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

	public void actualizarNotificacionTareaRepetitivaAsignada(
			NotificacionTareaRepetitivaAsignada dato) {
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

	public long contarNotificacionesTareaRepetitivaAsignadaEmpleadoPorLeer(
			int idEmpleado) {
		Session session = sesionPostgres.openSession();
		long dato = 0;
		try {
			dato = (Long) session
					.createQuery(
							"SELECT COUNT(ntra.id) FROM NotificacionTareaRepetitivaAsignada ntra WHERE ntra.empleado.id =:idEmpleado AND ntra.estatusNotificacion.nombre =:estatus")
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

	public List<NotificacionTareaRepetitivaAsignada> obtenerNotificacionesTareasRepetitivasAsignadasPorEmpleado(
			int idEmpleado) {
		List<NotificacionTareaRepetitivaAsignada> datos = new ArrayList<NotificacionTareaRepetitivaAsignada>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<NotificacionTareaRepetitivaAsignada>) session
					.createQuery(
							"FROM NotificacionTareaRepetitivaAsignada ntra WHERE ntra.empleado.id =:idEmpleado")
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

	public NotificacionTareaRepetitivaAsignada obtenerNotificacionTareaRepetitivaAsignada(
			int id) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		NotificacionTareaRepetitivaAsignada dato = null;
		try {
			dato = (NotificacionTareaRepetitivaAsignada) sesion.get(
					NotificacionTareaRepetitivaAsignada.class, id);
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
