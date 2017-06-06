package modelo.dao;

import java.util.ArrayList;
import java.util.List;

import modelo.dto.ItemMenu;
import modelo.hibernate.config.Sesion;

import org.hibernate.Session;

public class ItemMenuDAO {

	private Sesion sesionPostgres;

	public ItemMenuDAO() {
		super();
	}

	@SuppressWarnings("unchecked")
	public List<ItemMenu> obtenerItemsMenu() throws Exception {

		List<ItemMenu> datos = new ArrayList<ItemMenu>();
		@SuppressWarnings("static-access")
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<ItemMenu>) session.createCriteria(ItemMenu.class)
					.list();
		} catch (Exception e) {

			throw new Exception(e.getMessage(), e.getCause());
		} finally {
			session.close();
		}

		return datos;
	}

	public ItemMenu obtenerItemMenu(int id) throws Exception {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		ItemMenu dato = null;
		try {
			dato = (ItemMenu) sesion.get(ItemMenu.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage(), e.getCause());
		} finally {
			sesion.close();
		}
		return dato;
	}

}
