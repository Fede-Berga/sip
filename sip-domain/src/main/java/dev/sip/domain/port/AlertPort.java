package dev.sip.domain.port;

import dev.sip.domain.Finding;

public interface AlertPort {

    void send(Finding finding);
}
