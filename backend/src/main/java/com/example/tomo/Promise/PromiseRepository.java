package com.example.tomo.Promise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromiseRepository extends JpaRepository<Promise, Long> {

    boolean existsByPromiseName(String name);
    boolean existsByPromiseDateAndPromiseTime( LocalDate date,LocalTime time);

    @Query("SELECT new com.example.tomo.Promise.ResponseGetPromiseDto(p.promiseName, p.promiseDate, p.promiseTime, p.place" +
            ") FROM Promise p WHERE p.moim.id =:moim_id")
    List<ResponseGetPromiseDto> findByMoimId(@Param("moim_id") Long moim_id);

    Optional<Promise> findByPromiseName(String name);
}
