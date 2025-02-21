package com.user_microservice.user.application.dto.authenticationdto;

import com.user_microservice.user.application.util.ApplicationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    @NotBlank(message = ApplicationConstants.EMAIL_NOT_BLANK)
    @Email(message = ApplicationConstants.EMAIL_PATTERN)
    private String email;

    @NotBlank(message = ApplicationConstants.PASSWORD_NOT_BLANK)
    private String password;

}
