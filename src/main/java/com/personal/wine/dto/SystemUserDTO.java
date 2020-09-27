package com.personal.wine.dto;

import com.personal.wine.model.SystemUser;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class SystemUserDTO extends SystemUser {
    private String token;
}
