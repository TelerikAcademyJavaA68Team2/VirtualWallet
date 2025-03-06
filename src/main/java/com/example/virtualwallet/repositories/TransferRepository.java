package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.Transfer;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, UUID> {

    List <Transfer> findTransferByWallet_Owner_Id(UUID walletOwnerId, Sort sort);
}
