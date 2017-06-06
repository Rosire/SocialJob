package modelo.dao;

import org.hibernate.Session;

import modelo.dto.EstatusTareaAsignada;
import modelo.hibernate.config.Sesion;

public class EstatusTareaAsignadaDAO {

	private Sesion sesionPostgres;

	public EstatusTareaAsignadaDAO() {
		super();
	}

	public EstatusTareaAsignada obtenerEstatus(int id) throws Exception {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		EstatusTareaAsignada dato = null;
		try {
			dato = (EstatusTareaAsignada) sesion.get(
					EstatusTareaAsignada.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage(), e.getCause());
		} finally {
			sesion.close();
		}
		return dato;
	}

	public EstatusTareaAsignada obtenerEstatusPorNombre(String nombre) {
		@SuppressWarnings("static-access")
		Session session = sesionPostgres.openSession();
		EstatusTareaAsignada dato = null;
		try {
			dato = (EstatusTareaAsignada) session
					.createQuery(
							"FROM EstatusTareaAsignada et WHERE et.nombre = :nombre")
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

}
