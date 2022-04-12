package es.tfg.grupo11.controller;

import java.util.Optional;

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

import es.tfg.grupo11.modelo.User;
import es.tfg.grupo11.service.UserDao;

@RestController
@RequestMapping("/usuarios")
public class UserController {

	@Autowired
	private UserDao userDao;
	
	//Añadir usuario(Registro)
	@PostMapping(path="añadir", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> añadirUsuario(@RequestBody User u){
		boolean guardar = userDao.guardarUsuario(u);
		if(guardar) {
			return new ResponseEntity<User>(u,HttpStatus.CREATED);
		}else {
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		}
	}
	
	//Buscar usuario(Login)
	@GetMapping(path="buscarUsuario", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity <User> buscarUsuario(@RequestBody User u){
		boolean encontrado = userDao.buscarUser(u);			
		if(encontrado) {
			return new ResponseEntity<User>(HttpStatus.OK);
		}else {
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		}		
	}
	
	//Modificar usuario
	@PutMapping(path="modificar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> modificarUsuario(@RequestBody User u){
		boolean modificado = userDao.modificarUsuario(u);
		if(modificado) {
			return new ResponseEntity<User>(u,HttpStatus.OK);
		}else {
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		}
	}
	
	@DeleteMapping(path = "borrarUsuario")
	public ResponseEntity<User> borrarUsuario(@RequestParam(name="id") int id){
		boolean borrado = userDao.borrarUsuario(id);
		if(borrado) {
			return new ResponseEntity<User>(HttpStatus.OK);
		}else {
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		}
	}
	
}
