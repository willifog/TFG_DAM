package es.tfg.grupo11.reposiory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.tfg.grupo11.modelo.Receta;

@Repository
public interface RecetaRepository extends JpaRepository<Receta,Integer>{

	public Receta findByTitulo(String titulo);
	public boolean existsByTitulo(String titulo);
	public void deleteByTitulo(String titulo);
}
