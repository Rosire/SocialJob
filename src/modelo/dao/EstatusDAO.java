package modelo.dao;

import org.hibernate.Session;

import modelo.dto.Estatus;
import modelo.hibernate.config.Sesion;

public class EstatusDAO {

	private Sesion sesionPostgres;

	public EstatusDAO() {
		super();
	}

	public Estatus obtenerEstatus(int id) throws Exception {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		Estatus dato = null;
		try {
			dato = (Estatus) sesion.get(Estatus.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage(), e.getCause());
		} finally {
			sesion.close();
		}
		return dato;
	}

	public Estatus obtenerEstatusPorNombre(String nombre) {
		@SuppressWarnings("static-access")
		Session session = sesionPostgres.openSession();
		Estatus dato = null;
		try {
			dato = (Estatus) session.createQuery(
					"FROM Estatus e WHERE e.nombre =:nombre").setParameter(
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

}
