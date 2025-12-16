package com.example.Lab10.eventsourcing.repository;

import com.example.Lab10.eventsourcing.model.AccountSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccountSnapshotRepository extends JpaRepository<AccountSnapshot, Long> {
    Optional<AccountSnapshot> findFirstByAccountIdOrderBySnapshotVersionDesc(String accountId);
}




