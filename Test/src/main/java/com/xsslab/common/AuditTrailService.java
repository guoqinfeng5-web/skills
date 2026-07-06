package com.xsslab.common;

import com.xsslab.scenario07.LogService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 * 跨模块审计追踪 — 搜索、登录、反馈、上传等模块共用，
 * 用户输入经此写入日志，最终在管理后台（scenario07）二次展示。
 */
@Service
public class AuditTrailService {

    private final LogService logService;

    public AuditTrailService(LogService logService) {
        this.logService = logService;
    }

    public void trace(String action, HttpSession session, String message, String rawDetail) {
        String user = UserSessionHelper.getCurrentUser(session);
        logService.recordAction(action, user, message, rawDetail);
    }

    public void trace(String action, String user, String message, String rawDetail) {
        logService.recordAction(action, user, message, rawDetail);
    }
}
