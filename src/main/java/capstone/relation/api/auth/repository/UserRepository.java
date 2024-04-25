package capstone.relation.api.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import capstone.relation.api.auth.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);

	Optional<User> findByEmailAndProvider(String email, String provider);

	Optional<User> findByEmail(String email);
}
