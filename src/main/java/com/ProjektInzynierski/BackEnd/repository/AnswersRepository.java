package com.ProjektInzynierski.BackEnd.repository;

import com.ProjektInzynierski.BackEnd.data.entity.Answers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface AnswersRepository extends JpaRepository<Answers, Integer> {
    @Transactional
    @Modifying
    @Query("UPDATE Answers a SET a.question.id=:id WHERE a.id =:aId")
    void updateAnswer(int id, int aId);

}