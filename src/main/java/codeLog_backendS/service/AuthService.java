package codeLog_backendS.service;

import codeLog_backendS.domain.User;
import codeLog_backendS.dto.LoginRequest;
import codeLog_backendS.dto.LoginResponse;
import codeLog_backendS.dto.SignupRequest;
import codeLog_backendS.repository.UserRepository;
import codeLog_backendS.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signup(SignupRequest req){
        String name = req.getName() == null ? "" : req.getName();
        String username = req.getUsername() == null ? "" : req.getUsername();
        String password = req.getPassword() == null ? "" : req.getPassword();

        if (name.isEmpty() || username.isEmpty() || password == null || password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "필수 값이 없습니다.");
        }
        if (password.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호는 8자 이상이어야 합니다.");
        }
        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다.");
        }

        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req){
        if (req.getUsername() == null || req.getPassword() == null
                || req.getUsername().isBlank() || req.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username/password가 필요합니다.");
        }
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."));
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        String token = jwtTokenProvider.createToken(user.getId(),user.getUsername());
        return new LoginResponse(token);

    }


}
