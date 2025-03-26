package com.user_microservice.user.application.dto.userdto;

import com.user_microservice.user.application.util.ApplicationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    @NotBlank(message = ApplicationConstants.NAME_NOT_BLANK)
    private String name;

    @NotBlank(message = ApplicationConstants.LAST_NAME_NOT_BLANK)
    private String lastName;

    @NotBlank(message = ApplicationConstants.DOCUMENT_NUMBER_NOT_BLANK)
    @Pattern(regexp = "^\\d+$", message = ApplicationConstants.DOCUMENT_NUMBER_PATTERN)
    private String documentNumber;

    @NotBlank(message = ApplicationConstants.PHONE_NOT_BLANK)
    @Pattern(regexp = "^\\+?\\d{1,13}$", message = ApplicationConstants.PHONE_PATTERN)
    private String phone;

    @NotNull(message = ApplicationConstants.DATE_OF_BIRTH_NOT_NULL)
    private LocalDate birthDate;

    @NotBlank(message = ApplicationConstants.EMAIL_NOT_BLANK)
    @Email(message = ApplicationConstants.EMAIL_PATTERN)
    private String email;

    @NotBlank(message = ApplicationConstants.PASSWORD_NOT_BLANK)
    private String password;

    @NotBlank(message = ApplicationConstants.NAME_ROLE_NOT_BLANK)
    private String role;
}
