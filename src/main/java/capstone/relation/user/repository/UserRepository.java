package capstone.relation.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import capstone.relation.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUserName(String username);

	Optional<User> findByEmailAndProvider(String email, String provider);

	Optional<User> findByEmail(String email);
}
