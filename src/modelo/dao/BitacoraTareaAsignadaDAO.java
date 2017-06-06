package modelo.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import modelo.dto.BitacoraTareaAsignada;
import modelo.dto.Cargo;
import modelo.hibernate.config.Sesion;

public class BitacoraTareaAsignadaDAO {

	private Sesion sesionPostgres;

	public BitacoraTareaAsignadaDAO() {
		super();
	}

	public void registrarBitacoraTareaAsignada(BitacoraTareaAsignada dato) {
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

	public BitacoraTareaAsignada obtenerBitacoraTareaAsignada(int id) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		BitacoraTareaAsignada dato = null;
		try {
			dato = (BitacoraTareaAsignada) sesion.get(
					BitacoraTareaAsignada.class, id);
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

	public List<BitacoraTareaAsignada> obtenerBitacorasPorTarea(
			int idTareaAsignada) {

		List<BitacoraTareaAsignada> datos = new ArrayList<BitacoraTareaAsignada>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<BitacoraTareaAsignada>) em
					.createQuery(
							"FROM BitacoraTareaAsignada bta WHERE bta.tareaAsignada.id = :idTareaAsignada ")
					.setParameter("idTareaAsignada", idTareaAsignada).list();
		} catch (Exception e) {

			throw e;
		} finally {
			em.close();
		}

		return datos;
	}
}
