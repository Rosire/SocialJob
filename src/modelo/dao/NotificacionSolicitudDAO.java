package modelo.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import modelo.dto.NotificacionSolicitud;
import modelo.hibernate.config.Sesion;

public class NotificacionSolicitudDAO {

	private Sesion sesionPostgres;

	public NotificacionSolicitudDAO() {
		super();
	}

	public void registrarNotificacionSolicitud(NotificacionSolicitud dato) {
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

	public void actualizarNotificacionSolicitud(NotificacionSolicitud dato) {
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

	public long contarNotificacionesSolicitudesPorEmpleadoPorLeer(int idEmpleado) {
		Session session = sesionPostgres.openSession();
		long dato = 0;
		try {
			dato = (Long) session
					.createQuery(
							"SELECT COUNT(ns.id) FROM NotificacionSolicitud ns WHERE ns.empleado.id =:idEmpleado AND ns.estatusNotificacion.nombre =:estatus")
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

	public List<NotificacionSolicitud> obtenerNotificacionesSolicitudesPorEmpleado(
			int idEmpleado) {
		List<NotificacionSolicitud> datos = new ArrayList<NotificacionSolicitud>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<NotificacionSolicitud>) session
					.createQuery(
							"FROM NotificacionSolicitud ns WHERE ns.empleado.id =:idEmpleado")
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

	public NotificacionSolicitud obtenerNotificacionSolicitud(int id) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		NotificacionSolicitud dato = null;
		try {
			dato = (NotificacionSolicitud) sesion.get(
					NotificacionSolicitud.class, id);
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
