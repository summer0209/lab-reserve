package com.labreserve.user;

import com.labreserve.common.BizException;
import jakarta.servlet.http.HttpSession;

public final class CurrentUser {
    public static final String ID = "LOGIN_USER_ID";
    public static final String ROLE = "LOGIN_ROLE";
    public static final String NAME = "LOGIN_NAME";
    public static final String STUDENT_NO = "LOGIN_STUDENT_NO";

    private CurrentUser() {}

    public static void save(HttpSession session, User user) {
        session.setAttribute(ID, user.getId());
        session.setAttribute(ROLE, user.getRole());
        session.setAttribute(NAME, user.getName());
        session.setAttribute(STUDENT_NO, user.getStudentNo());
    }

    public static Long requireId(HttpSession session) {
        Object id = session.getAttribute(ID);
        if (id == null) {
            throw new BizException("请先登录");
        }
        return (Long) id;
    }

    public static String requireRole(HttpSession session) {
        requireId(session);
        return String.valueOf(session.getAttribute(ROLE));
    }

    public static void requireAdmin(HttpSession session) {
        if (!"ADMIN".equals(requireRole(session))) {
            throw new BizException("仅管理员可操作");
        }
    }
}
