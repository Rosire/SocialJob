package modelo.dao;

import org.hibernate.Session;

import modelo.dto.EstatusSolicitud;
import modelo.hibernate.config.Sesion;

public class EstatusSolicitudDAO {

	private Sesion sesionPostgres;

	public EstatusSolicitudDAO() {
		super();
	}

	public EstatusSolicitud obtenerEstatusSolicitudPorNombre(String nombre) {
		@SuppressWarnings("static-access")
		Session session = sesionPostgres.openSession();
		EstatusSolicitud dato = null;
		try {
			dato = (EstatusSolicitud) session.createQuery(
					"FROM EstatusSolicitud en WHERE en.nombre = :nombre")
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

	public EstatusSolicitud obtenerEstatusSolicitud(int id) throws Exception {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		EstatusSolicitud dato = null;
		try {
			dato = (EstatusSolicitud) sesion.get(EstatusSolicitud.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage(), e.getCause());
		} finally {
			sesion.close();
		}
		return dato;
	}
}
