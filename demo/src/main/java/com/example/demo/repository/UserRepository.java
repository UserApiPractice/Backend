package com.example.demo.repository;

import com.example.demo.model.Member;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<Member, Integer> {
}
