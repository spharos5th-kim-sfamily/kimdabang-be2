package com.kimdabang.kdbserver.auth.application;

import com.kimdabang.kdbserver.auth.dto.in.PasswordRequestDto;
import com.kimdabang.kdbserver.auth.dto.in.SignInRequestDto;
import com.kimdabang.kdbserver.auth.dto.in.SignUpRequestDto;
import com.kimdabang.kdbserver.auth.dto.in.TestTokenRequestDto;
import com.kimdabang.kdbserver.auth.dto.out.PasswordVerifyResponseDto;
import com.kimdabang.kdbserver.auth.dto.out.SignInResponseDto;
import com.kimdabang.kdbserver.auth.dto.out.TestTokenResponseDto;
import com.kimdabang.kdbserver.auth.infrastructure.AuthRepository;
import com.kimdabang.kdbserver.common.jwt.JwtTokenProvider;
import com.kimdabang.kdbserver.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService{

    private final AuthRepository authRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void signUp(SignUpRequestDto signUpRequestDto) {
        User user = authRepository.findByLoginId(signUpRequestDto.getLoginId()).orElse(null);
        if (user != null) {
            throw new IllegalArgumentException("이미 가입된 회원입니다.");
        }
        authRepository.save(signUpRequestDto.toEntity(passwordEncoder));
    }

    @Override
    public SignInResponseDto signIn(SignInRequestDto signInRequestDto) {

        log.info("signInRequestDto : {}", signInRequestDto);
        User user = authRepository.findByLoginId(signInRequestDto.getLoginId()).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디를 가진 회원이 없습니다.")
        );
        log.info("user : {}", user);

        try {
           Authentication authentication =
                   authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            //todo Email-> loginid로 바꾸기
                            signInRequestDto.getPassword()
                    )
           );
           return SignInResponseDto.builder()
                   .accessToken(createToken(authentication))
                   .name(user.getName())
                   .build();
        } catch (Exception e) {
            throw new IllegalArgumentException("로그인 실패");
        }
    }

    @Override
    public void putPassword(PasswordRequestDto passwordRequestDto) {
        String uuid = jwtTokenProvider.useToken(passwordRequestDto.getAccessToken());
        User user = authRepository.findByUuid(uuid).orElseThrow(
                () -> new IllegalArgumentException("회원을 찾을 수 없습니다.")
        );
        user.updatePassword(passwordEncoder.encode(passwordRequestDto.getPassword()));
        authRepository.save(user);
    }

    @Override
    public PasswordVerifyResponseDto verifyPassword(PasswordRequestDto passwordRequestDto) {
        String uuid = jwtTokenProvider.useToken(passwordRequestDto.getAccessToken());
        User user = authRepository.findByUuid(uuid).orElseThrow(
                () -> new IllegalArgumentException("회원을 찾을 수 없습니다.")
        );
        log.info("passwordEncoder.encode(passwordRequestDto.getPassword()):{}",passwordEncoder.encode(passwordRequestDto.getPassword()));
        log.info("user.getPassword():{}",user.getPassword());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            //todo Email-> loginid로 바꾸기
                            passwordRequestDto.getPassword()
                            )
                    );
            return PasswordVerifyResponseDto.builder()
                    .Verification(true)
                    .build();
        } catch (Exception e) {
            return PasswordVerifyResponseDto.builder()
                    .Verification(false)
                    .build();
        }
    }

    @Override
    public TestTokenResponseDto testToken(TestTokenRequestDto testTokenRequestDto) {
        String uuid = jwtTokenProvider.useToken(testTokenRequestDto.getAccessToken());
        return TestTokenResponseDto.builder()
                .uuid(uuid).build();
    }

    private String createToken(Authentication authentication) {
        return jwtTokenProvider.generateAccessToken(authentication);
        //return jwtTokenProvider.generateAccessToken(uuid);
    }



}
