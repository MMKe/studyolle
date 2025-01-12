package com.studyolle.study;

import com.studyolle.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {

    List<Study> findByTitle(String title);

    Optional<Study> findByPath(String path);

    boolean existsByPath(String path);
}
