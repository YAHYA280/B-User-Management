package com.marketingconfort.brainboost.repository;

import com.marketingconfort.brainboost_common.usermanagement.models.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
    Optional<Parent> findByGatewayCustomerId(String customerId);
    @Query("SELECT p FROM Parent p WHERE " +
            "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
            "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Parent> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(@Param("name") String name);
    Parent findByEmail(String mail);
}
