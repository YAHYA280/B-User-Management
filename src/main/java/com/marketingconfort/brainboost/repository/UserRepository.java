package com.marketingconfort.brainboost.repository;

import com.marketingconfort.brainboost_common.usermanagement.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String mail);
}
