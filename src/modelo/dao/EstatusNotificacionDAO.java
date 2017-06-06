package modelo.dao;

import org.hibernate.Session;

import modelo.dto.EstatusNotificacion;
import modelo.hibernate.config.Sesion;

public class EstatusNotificacionDAO {

	private Sesion sesionPostgres;

	public EstatusNotificacionDAO() {
		super();
	}

	public EstatusNotificacion obtenerEstatusNotificacionPorNombre(String nombre) {
		@SuppressWarnings("static-access")
		Session session = sesionPostgres.openSession();
		EstatusNotificacion dato = null;
		try {
			dato = (EstatusNotificacion) session
					.createQuery(
							"FROM EstatusNotificacion en WHERE en.nombre = :nombre")
					.setParameter("nombre", nombre).uniqueResult();
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

	public EstatusNotificacion obtenerEstatusNotificacion(int id)
			throws Exception {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		EstatusNotificacion dato = null;
		try {
			dato = (EstatusNotificacion) sesion.get(EstatusNotificacion.class,
					id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage(), e.getCause());
		} finally {
			sesion.close();
		}
		return dato;
	}
}
