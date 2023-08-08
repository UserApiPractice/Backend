package com.example.demo.controller;

import com.example.demo.model.Member;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/save")
    public Member addUser(@RequestBody Member member) {
        return userRepository.save(member);
    }

    @GetMapping("/{id}")
    public Optional<Member> getUser(@PathVariable Integer id) {
        return userRepository.findById(id);
    }

    @PutMapping("/{id}")
    public Member updateUser(@PathVariable Integer id, @RequestBody Member member) {
        member.setUserId(id);
        return userRepository.save(member);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userRepository.deleteById(id);
    }
}
