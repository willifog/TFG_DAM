package es.tfg.grupo11.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.tfg.grupo11.modelo.Receta;
import es.tfg.grupo11.service.RecetaDao;

@RestController
@RequestMapping("/recetas")
public class RecetaController {

	@Autowired
	private RecetaDao recetaDao;
	
	@PostMapping(path="añadir", consumes= MediaType.APPLICATION_JSON_VALUE, produces= MediaType.APPLICATION_JSON_VALUE)
	public  ResponseEntity<Receta> añadirReceta(@RequestBody Receta receta){
		boolean guardarReceta = recetaDao.guardarReceta(receta);
		
		if(guardarReceta) {
			return new ResponseEntity<Receta>(HttpStatus.CREATED);
		}else {
			return new ResponseEntity<Receta>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping(path="buscar", produces = MediaType.APPLICATION_JSON_VALUE)
	public  ResponseEntity<Receta> buscarReceta(@RequestParam String titulo){
		Receta recetaBuscada = recetaDao.buscarReceta(titulo);
		
		if(recetaBuscada != null) {
			return new ResponseEntity<Receta>(recetaBuscada,HttpStatus.OK);
		}else {
			return new ResponseEntity<Receta>(HttpStatus.NOT_FOUND);
		}	
	}
	
	@PutMapping(path="modificar", consumes= MediaType.APPLICATION_JSON_VALUE, produces= MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Receta> modificarReceta(@RequestBody Receta receta){
		boolean recetaModificada = recetaDao.editarReceta(receta);
		
		if(recetaModificada) {
			return new ResponseEntity<Receta>(receta,HttpStatus.CREATED);
		}else {
			return new ResponseEntity<Receta>(HttpStatus.NOT_FOUND);
		}		
	}
	
	@DeleteMapping(path="eliminar")
	public ResponseEntity<Receta> eliminarReceta(@RequestParam(name="titulo") String titulo){
		boolean eliminado = recetaDao.eliminarReceta(titulo);
		
		if(eliminado) {
			return new ResponseEntity<Receta>(HttpStatus.OK);
		}else {
			return new ResponseEntity<Receta>(HttpStatus.NOT_FOUND);
		}
	}
	
	
}
