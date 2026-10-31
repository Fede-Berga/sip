package dev.sip.application.fake;

import dev.sip.domain.Finding;
import dev.sip.domain.port.AlertPort;

import java.util.ArrayList;
import java.util.List;

public class FakeAlertPort implements AlertPort {

    private final List<Finding> sentAlerts = new ArrayList<>();

    @Override
    public void send(Finding finding) {
        sentAlerts.add(finding);
    }

    public List<Finding> sentAlerts() { return List.copyOf(sentAlerts); }
    public int alertCount()           { return sentAlerts.size(); }
    public boolean wasAlerted()       { return !sentAlerts.isEmpty(); }
    public void clear()               { sentAlerts.clear(); }
}
