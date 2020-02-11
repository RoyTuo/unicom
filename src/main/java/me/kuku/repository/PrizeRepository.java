package me.kuku.repository;

import me.kuku.entity.Prize;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrizeRepository extends JpaRepository<Prize, Integer> {
}
