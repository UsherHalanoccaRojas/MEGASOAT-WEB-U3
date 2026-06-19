package com.example.demo.application.port.in;

import java.util.List;
import java.util.Map;

public interface DashboardPort {
    Map<String, Object> getOverview();
    List<Map<String, Object>> getRanking();
    List<Map<String, Object>> getMapMarkers();
    Map<String, Object> getStats();
    List<Map<String, Object>> getAlerts();
}
