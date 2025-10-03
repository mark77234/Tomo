package com.example.tomo.Users;

import com.example.tomo.Users.dtos.RequestUserSignDto;
import com.example.tomo.Users.dtos.ResponseUniformDto;
import com.example.tomo.Users.dtos.addFriendRequestDto;
import com.example.tomo.Users.dtos.getFriendResponseDto;
import com.example.tomo.global.ApiResponse;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/friends")
    public ResponseEntity<ResponseUniformDto> addFriendsUsingPhoneNumber(@RequestBody addFriendRequestDto dto) {
       try{
           return ResponseEntity.ok().body(userService.addFriends(dto));
       }catch (EntityExistsException e){
           return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseUniformDto(false, e.getMessage()));
       }
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseUniformDto> signUser(@RequestBody RequestUserSignDto dto) {
        try {
            return ResponseEntity.ok(userService.signUser(dto));
        } catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseUniformDto(false, e.getMessage()));
        } catch (DataIntegrityViolationException e){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ResponseUniformDto(false,"같은 이메일로 가입 내역이 존재합니다"));
        }

    }

    // 로그인도 만들어야 함
    // 인자로 뭘 받을 지 조금 더 고민해봄
   /* @PostMapping
    public ResponseEntity<ResponseLoginDto> loginUser(@RequestBody RequestLoginDto dto){


    }*/
    @GetMapping("/api/protected/user")
    public String getUser(@AuthenticationPrincipal String uid) {
        // SecurityContext에 등록된 Firebase UID 가져오기
        return "Hello, user " + uid;
    }


}
