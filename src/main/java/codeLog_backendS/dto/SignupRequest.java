package codeLog_backendS.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String password;
    private String username;
    private String name;
}
