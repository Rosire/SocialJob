package modelo.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import modelo.dto.TipoUsuario;
import modelo.hibernate.config.Sesion;

public class TipoUsuarioDAO {

	private Sesion sesionPostgres;

	public TipoUsuarioDAO() {
		super();
	}

	public List<TipoUsuario> obtenerTiposUsuarios() {
		List<TipoUsuario> datos = new ArrayList<TipoUsuario>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<TipoUsuario>) session.createCriteria(
					TipoUsuario.class).list();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}

		return datos;
	}

	public TipoUsuario obtenerTipoUsuario(int id) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		TipoUsuario dato = null;
		try {
			dato = (TipoUsuario) sesion.get(TipoUsuario.class, id);
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

	public TipoUsuario obtenerTipoUsuarioPorNombre(String nombre) {
		@SuppressWarnings("static-access")
		Session session = sesionPostgres.openSession();
		TipoUsuario dato = null;
		try {
			dato = (TipoUsuario) session
					.createQuery("FROM TipoUsuario tu WHERE tu.nombre =:nombre")
					.setParameter("nombre", nombre).uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
		}
		return dato;
	}
}
