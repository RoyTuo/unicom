package me.kuku.repository;

import me.kuku.entity.Prize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrizeRepository extends JpaRepository<Prize, Integer> {

    List<Prize> findAllByPhone(String phone);
}
