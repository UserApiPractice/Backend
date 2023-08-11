package com.example.demo.controller;

import com.example.demo.model.LoginRequest;
import com.example.demo.model.Member;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.MemberService;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.Optional;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserRepository userRepository;

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest dto){
        return ResponseEntity.ok().body(memberService.login(dto.getUserName()));
    }

    @GetMapping("/loginCheck")
    public ResponseEntity<String> loginCheck(Authentication authentication){
        return ResponseEntity.ok().body(authentication.getName() +"님이 로그인 하셨습니다.");
    }

    @PostMapping("/save")
    public ResponseEntity<String> addUser (@RequestBody Member member) {
        Optional<Member> existingMember  = userRepository.findById(member.getUserId());
        if(existingMember.isPresent()) {
            return ResponseEntity.ok().body("중복 회원입니다.");
        }else{
            userRepository.save(member);
            return ResponseEntity.ok().body("회원 가입되셨습니다.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getUser(@PathVariable Integer id) {
        Optional<Member> memberOptional = userRepository.findById(id);
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            return ResponseEntity.ok(member);
        } else {
            return ResponseEntity.notFound().build();
        }
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
