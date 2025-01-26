package com.learnSpringBoot.eCom.repository;

import com.learnSpringBoot.eCom.model.AppRole;
import com.learnSpringBoot.eCom.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(AppRole appRole);
}
