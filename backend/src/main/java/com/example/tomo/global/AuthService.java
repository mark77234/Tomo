package com.example.tomo.global;


import com.example.tomo.Users.User;
import com.example.tomo.Users.UserRepository;
import com.example.tomo.Users.UserService;
import com.example.tomo.firebase.ResponseFirebaseLoginDto;
import com.example.tomo.jwt.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// Service
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserService userService;



    public ResponseFirebaseLoginDto loginWithFirebase(String uid) {
        String accessToken = jwtTokenProvider.createAccessToken(uid);
        String refreshToken = jwtTokenProvider.createRefreshToken(uid);

        // DB에 RefreshToken 저장
        userService.saveRefreshToken(uid, refreshToken);

        return new ResponseFirebaseLoginDto(accessToken, refreshToken);
    }

    public ResponseFirebaseLoginDto reissueAccessToken(String refreshToken) {
        try {
            String uid = jwtTokenProvider.validateRefreshTokenAndGetUuid(refreshToken);
            User user = userRepository.findByFirebaseId(uid)
                    .orElseThrow(() -> new EntityNotFoundException("사용자 없음"));

            if (!refreshToken.equals(user.getRefreshToken())) {
                throw new EntityNotFoundException("Refresh token 불일치");
            }

            String newAccessToken = jwtTokenProvider.createAccessToken(uid);
            String newRefreshToken = jwtTokenProvider.createRefreshToken(uid);


            user.setRefreshToken(newRefreshToken);
            userRepository.save(user);

            return new ResponseFirebaseLoginDto(newAccessToken, newRefreshToken);

        } catch (Exception e) {
            throw new RuntimeException("Refresh token 검증 실패", e);
        }
    }

}
