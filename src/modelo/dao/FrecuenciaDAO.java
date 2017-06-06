package modelo.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import modelo.dto.Cargo;
import modelo.dto.Frecuencia;
import modelo.dto.Tarea;
import modelo.hibernate.config.Sesion;

public class FrecuenciaDAO {

	private Sesion sesionPostgres;

	public FrecuenciaDAO() {
		super();
	}

	public void registrarFrecuencia(Frecuencia dato) {
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

	public void eliminarFrecuencia(Frecuencia dato) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		Transaction tx = null;
		try {
			tx = sesion.beginTransaction();
			sesion.delete(dato);
			tx.commit();

		} catch (Exception e) {
			tx.rollback();

			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			sesion.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Frecuencia> obtenerFrecuenciasPorEmpresa(int idEmpresa) {

		List<Frecuencia> datos = new ArrayList<Frecuencia>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Frecuencia>) session
					.createQuery(
							"FROM Frecuencia f WHERE f.empresa.id = :idEmpresa")
					.setParameter("idEmpresa", idEmpresa).list();
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

	public Frecuencia obtenerFrecuencia(int id) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		Frecuencia dato = null;
		try {
			dato = (Frecuencia) sesion.get(Frecuencia.class, id);
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

	public Frecuencia obtenerFrecuenciaPorNombrePorEmpresa(String nombre,
			int idEmpresa) {
		@SuppressWarnings("static-access")
		Session session = sesionPostgres.openSession();
		Frecuencia dato = null;
		try {
			dato = (Frecuencia) session
					.createQuery(
							"FROM Frecuencia f WHERE f.nombre = :nombre AND f.empresa.id = :idEmpresa")
					.setParameter("idEmpresa", idEmpresa)
					.setParameter("nombre", nombre).uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
		}
		return dato;
	}

	public void actualizarFrecuencia(Frecuencia dato) {
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

}
