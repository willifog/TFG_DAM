package es.tfg.grupo11.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.tfg.grupo11.modelo.Receta;
import es.tfg.grupo11.reposiory.RecetaRepository;

@Service
public class RecetaDao {
	
	@Autowired
	private RecetaRepository recetaRepo;

	//Añadir receta
	public boolean guardarReceta(Receta receta) {
		boolean encontrada = recetaRepo.existsByTitulo(receta.getTitulo());
		boolean respuesta = false;
		
		if(receta != null && !encontrada) {
			recetaRepo.save(receta);
			respuesta = true;
		}
		return respuesta;
	}
	
	//Buscar receta por Titulo(nombre)
	public Receta buscarReceta(String titulo) {
		boolean recetaEncontrada = recetaRepo.existsByTitulo(titulo);		
		Receta recetaBuscada = null;
		
		if(recetaEncontrada) {
			recetaBuscada = recetaRepo.findByTitulo(titulo);
		}
		
		return recetaBuscada;	
	}
	
	//Editar Receta
	public boolean editarReceta(Receta receta) {  
		boolean editada = false;
		boolean existe = recetaRepo.existsByTitulo(receta.getTitulo());		
		
		if(existe) {
			Receta recetaBuscada = recetaRepo.findByTitulo(receta.getTitulo());
			receta.setId(recetaBuscada.getId());
			
			recetaRepo.save(receta);
			editada = true;
		}
		
		return editada;
	}
	
	//Eliminar Receta
	public boolean eliminarReceta(String titulo) {
		boolean eliminada = false;
		Receta recetaBuscada = recetaRepo.findByTitulo(titulo);
		
		if(recetaBuscada != null) {
			recetaRepo.deleteById(recetaBuscada.getId());;
			eliminada = true;
		}
		
		return eliminada;
	}
	
}
