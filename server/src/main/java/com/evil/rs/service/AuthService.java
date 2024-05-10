package com.evil.rs.service;

import com.evil.rs.model.AuthUser;
import com.evil.rs.utils.Result;

public interface AuthService {

    public Result authentication(AuthUser authUser);

}
