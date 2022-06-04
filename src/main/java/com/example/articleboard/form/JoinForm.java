package com.example.articleboard.form;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class JoinForm {
    @NotBlank
    @Length(min = 5)
    private String username;
    @NonNull
    private String password;
    @NonNull
    private String nick;
}
