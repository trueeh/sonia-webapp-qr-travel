package sonia.webapp.qrtravel.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import sonia.webapp.qrtravel.QrTravelAdminToken;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
@Component
@Order(1)
public class AuthFilter implements Filter
{
  private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(
    AuthFilter.class.getName());

  @Override
  public void doFilter( ServletRequest servletRequest, 
    ServletResponse servletResponse,
    FilterChain chain) throws IOException, ServletException
  {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    
    boolean followChain = true;
    
    String uri = request.getRequestURI();
    if ( uri.startsWith("/sys") || uri.startsWith("/admin"))
    {
      LOGGER.debug("Logging Request {} : {}", request.getMethod(), uri );
      QrTravelAdminToken token = QrTravelAdminToken.fromHttpRequest(request);
      LOGGER.trace( token.toString() );
      
      if ( uri.startsWith("/admin") && !token.isAuthenticated())
      {
        followChain = false;
        response.sendRedirect("/sys/login");
      }
    }

    if ( followChain )
    {
      chain.doFilter(request, response);
    }
  }
}
