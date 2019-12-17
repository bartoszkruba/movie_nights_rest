package com.example.movie_nights_rest.repository;

import com.example.movie_nights_rest.model.RequestResponseLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestResponseLogRepository extends PagingAndSortingRepository<RequestResponseLog, String> {

}
