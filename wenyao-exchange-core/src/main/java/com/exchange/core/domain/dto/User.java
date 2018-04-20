package com.exchange.core.domain.dto;

import com.exchange.core.domain.enums.ActiveEnum;
import com.exchange.core.domain.enums.LevelEnum;
import com.exchange.core.domain.enums.RoleEnum;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class User implements Serializable {
	private static final long serialVersionUID = -7210991847641135129L;
	private Long id;
    private String email;
    private String pwd;
    private LevelEnum level;
    private String otpHash;
    private ActiveEnum active;
    private RoleEnum role;
    private LocalDateTime regDtm;
    private LocalDateTime delDtm;
}
