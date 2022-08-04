package hello.login.web.filter;

import hello.login.web.SessionConst;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Log4j2
public class LoginFilter implements Filter {

    private static final String[] whiteList = {
            "/", "/members/add", "/login", "/logout", "/css/*"
    };

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestURI = request.getRequestURI();

        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try{
            log.info("인증 체크 필터 시작 {}", requestURI);

            if(isLoginCheckPath(requestURI)){
                log.info("인증 체크 로직 실행 {}", requestURI);
                HttpSession session = request.getSession(false);
                if(session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null){
                    log.info("미인증 사용자 요청 {}", requestURI);

                    // 미인증 사용자 요청이기 때문에 redirect 시키면 된다.
                    response.sendRedirect("/login?redirectURL=" + requestURI);
                    return;
                }
            }

            chain.doFilter(servletRequest,servletResponse);
        } catch (Exception e){
            throw e;
        } finally {
            log.info("인증 체크 필터 종료");
        }

    }

    /**
     * 화이트 리스트의 경우 인증 체크 x
     */
    private boolean isLoginCheckPath(String requestURI){
        return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
    }

}
