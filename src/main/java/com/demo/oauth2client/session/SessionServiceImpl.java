package com.demo.oauth2client.session;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Service("sessionService")
public class SessionServiceImpl implements SessionService {
    private SessionInfo session;

    @Override
    public void initRol() {
        session = new SessionInfo();
    }

    @Override
    public SessionInfo getSessionInfo() {
        return session;
    }

}
