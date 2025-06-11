package com.marketingconfort.brainboost.repository;

import com.marketingconfort.brainboost_common.usermanagement.models.Admin;
import com.marketingconfort.brainboost_common.usermanagement.models.Role;
import com.marketingconfort.brainboost_common.usermanagement.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    boolean existsByRoles(Role role);
    Admin findByEmail(String mail);
}