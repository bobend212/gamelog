package com.matkon.gamelog.repos;

import com.matkon.gamelog.data.tvshow.season.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {}
