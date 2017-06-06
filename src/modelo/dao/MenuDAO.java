package modelo.dao;

import java.util.ArrayList;
import java.util.List;

import modelo.dto.Estatus;
import modelo.dto.Menu;
import modelo.dto.Usuario;
import modelo.hibernate.config.Sesion;

import org.hibernate.Session;

public class MenuDAO {

	private Sesion sesionPostgres;

	public MenuDAO() {
		super();
	}

	public List<Menu> obtenerMenuSuperiorPorTipoUsuario(int idTipoUsuario) {
		@SuppressWarnings("static-access")
		List<Menu> datos = new ArrayList<Menu>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<Menu>) em
					.createQuery(
							"FROM Menu m WHERE m.tipoUsuario.id = :idTipoUsuario AND m.itemMenu.tipoItem.nombre=:tipoItem")
					.setParameter("idTipoUsuario", idTipoUsuario)
					.setParameter("tipoItem", "SUPERIOR").list();
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

	public List<Menu> obtenerMenuLateralPorTipoUsuario(int idTipoUsuario) {
		@SuppressWarnings("static-access")
		List<Menu> datos = new ArrayList<Menu>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<Menu>) em
					.createQuery(
							"FROM Menu m WHERE m.tipoUsuario.id = :idTipoUsuario AND m.itemMenu.tipoItem.nombre=:tipoItem")
					.setParameter("idTipoUsuario", idTipoUsuario)
					.setParameter("tipoItem", "LATERAL").list();
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
