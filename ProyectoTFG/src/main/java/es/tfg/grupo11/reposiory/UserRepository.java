package es.tfg.grupo11.reposiory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.tfg.grupo11.modelo.User;

@Repository
public interface UserRepository  extends JpaRepository<User,Integer>{
	
	public User findByEmail(String email);
	public boolean existsByEmail(String email);
	public void deleteByEmail(String email);
}
