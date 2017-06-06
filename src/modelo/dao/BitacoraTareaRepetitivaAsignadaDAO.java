package modelo.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import modelo.dto.BitacoraTareaAsignada;
import modelo.dto.BitacoraTareaRepetitivaAsignada;
import modelo.hibernate.config.Sesion;

public class BitacoraTareaRepetitivaAsignadaDAO {

	private Sesion sesionPostgres;

	public BitacoraTareaRepetitivaAsignadaDAO() {
		super();
	}

	public void registrarBitacoraTareaRepetitivaAsignada(
			BitacoraTareaRepetitivaAsignada dato) {
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

	public BitacoraTareaRepetitivaAsignada obtenerBitacoraTareaRepetitivaAsignada(
			int id) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		BitacoraTareaRepetitivaAsignada dato = null;
		try {
			dato = (BitacoraTareaRepetitivaAsignada) sesion.get(
					BitacoraTareaRepetitivaAsignada.class, id);
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

	public List<BitacoraTareaRepetitivaAsignada> obtenerBitacorasPorTarea(
			int idTareaAsignada) {

		List<BitacoraTareaRepetitivaAsignada> datos = new ArrayList<BitacoraTareaRepetitivaAsignada>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<BitacoraTareaRepetitivaAsignada>) em
					.createQuery(
							"FROM BitacoraTareaRepetitivaAsignada bta WHERE bta.tareaRepetitivaAsignada.id = :idTareaAsignada ")
					.setParameter("idTareaAsignada", idTareaAsignada).list();
		} catch (Exception e) {

			throw e;
		} finally {
			em.close();
		}

		return datos;
	}
}
