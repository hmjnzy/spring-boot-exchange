package com.exchange.core.domain.dto;

import com.exchange.core.domain.enums.ActiveEnum;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class EmailConfirm implements Serializable {
	private static final long serialVersionUID = 8560063755244112920L;
	private String hashEmail;
    private String code;
    private String email;
    private ActiveEnum sendYn;
    private LocalDateTime regDtm;
}
