package com.example.demo.repository;

import com.example.demo.model.Member;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<Member, Integer> {
}
