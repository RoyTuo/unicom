package me.kuku.repository;

import me.kuku.entity.PhoneLa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhoneRepository extends JpaRepository<PhoneLa, Integer> {

    PhoneLa findByPhone(String phone);

    List<PhoneLa> findAllByPhone(String phone);

}
