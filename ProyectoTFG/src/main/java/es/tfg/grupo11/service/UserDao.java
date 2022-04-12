package es.tfg.grupo11.service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.tfg.grupo11.modelo.User;
import es.tfg.grupo11.reposiory.UserRepository;

@Service
public class UserDao {

	@Autowired
	private UserRepository userRepo;
	
	//Añadir usuario
	public boolean guardarUsuario(User u) {
		boolean validacionEmail = validacionEmail(u.getEmail());
		boolean encontrado = userRepo.existsByEmail(u.getEmail());	
		boolean respuesta = false;
		
		
		if(u != null && !encontrado  && validacionEmail) { //Si no encuentra un usuario lo guarda ya que no existe
			userRepo.save(u);
			respuesta = true;
		}
		
		return respuesta;	
	}
	
	//Buscar usuario(Login)
	@SuppressWarnings("null")
	public boolean buscarUser(User u) {
		boolean validacionEmail = validacionEmail(u.getEmail());
		boolean encontrado = userRepo.existsByEmail(u.getEmail()); //comprobamos si existe el email
		boolean respuesta = false;
		User user = null;	
				
		if(encontrado && validacionEmail) {
			user = userRepo.findByEmail(u.getEmail()); //Busca usuario por email(campo unico) 
			
			if((user.getPass().equals(u.getPass()) && user.getEmail().equalsIgnoreCase(u.getEmail()))) {
				respuesta = true; //si coincide la pass (200 ok)
			}			
		}
		
		return respuesta;
	}
	
	//Modificar Usuario
	public boolean modificarUsuario(User u) {
		boolean resultado = userRepo.existsByEmail(u.getEmail()); //Si existe lo actualizamos (hace falta (id))
		if(resultado) {
			userRepo.save(u);
		}else {
			resultado = false;
		}
		return resultado;
	}
	
	//Eliminar
	public boolean borrarUsuario(int id) {
		Optional<User> u = userRepo.findById(id);
		boolean resultado;
		if(u.isPresent()) {
			userRepo.deleteById(id);
			resultado = true;
		}else {
			resultado = false;
		}
		return resultado;
	}
	
	
	//Validacion Email
	public boolean validacionEmail(String email) {
		boolean resultado;
		Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		Matcher mather = pattern.matcher(email);
		
		if (mather.find() == true) {
            resultado = true;
        } else {
        	resultado = false;
        }
		
		return resultado;	
	}
}
