package modelo.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import modelo.dto.Empresa;
import modelo.dto.Usuario;
import modelo.hibernate.config.Sesion;

public class EmpresaDAO {

	private Sesion sesionPostgres;

	public EmpresaDAO() {
		super();
	}

	public void registrarEmpresa(Empresa dato) {
		@SuppressWarnings("static-access")
		Session em = sesionPostgres.openSession();
		Transaction tx = null;
		try {
			tx = em.beginTransaction();
			em.save(dato);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			em.close();
		}
	}

	public Empresa obtenerEmpresa(int id) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		Empresa dato = null;
		try {
			dato = (Empresa) sesion.get(Empresa.class, id);
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

	public void actualizarEmpresa(Empresa dato) {
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

	public Empresa obtenerEmpresa(String rif) {
		Empresa dato = null;
		@SuppressWarnings("static-access")
		Session em = sesionPostgres.openSession();
		try {
			dato = (Empresa) em.createQuery("FROM Empresa e WHERE e.rif = :rif")
					.setParameter("rif", rif).uniqueResult();
		} catch (Exception e) {
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			em.close();
		}
		return dato;
	}
}
