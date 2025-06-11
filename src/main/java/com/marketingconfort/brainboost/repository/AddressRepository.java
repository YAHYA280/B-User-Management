package com.marketingconfort.brainboost.repository;

import com.marketingconfort.brainboost_common.usermanagement.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
