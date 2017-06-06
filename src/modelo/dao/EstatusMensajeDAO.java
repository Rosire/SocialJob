package modelo.dao;

import org.hibernate.Session;

import modelo.dto.EstatusMensaje;
import modelo.hibernate.config.Sesion;

public class EstatusMensajeDAO {

	private Sesion sesionPostgres;

	public EstatusMensajeDAO() {
		super();
	}

	public EstatusMensaje obtenerEstatusMensajePorNombre(String nombre) {
		@SuppressWarnings("static-access")
		Session session = sesionPostgres.openSession();
		EstatusMensaje dato = null;
		try {
			dato = (EstatusMensaje) session.createQuery(
					"SELECT em FROM EstatusMensaje AS em WHERE em.nombre = :nombre").setParameter(
					"nombre", nombre).uniqueResult();
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

	public EstatusMensaje obtenerEstatusMensaje(int id) throws Exception {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		EstatusMensaje dato = null;
		try {
			dato = (EstatusMensaje) sesion.get(EstatusMensaje.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage(), e.getCause());
		} finally {
			sesion.close();
		}
		return dato;
	}

}
