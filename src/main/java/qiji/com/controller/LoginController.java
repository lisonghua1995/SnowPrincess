package qiji.com.controller;

import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import qiji.com.entity.sys.TUser;
import qiji.com.service.sys.UserService;
import qiji.com.util.MD5;
import qiji.com.util.UserUtil;

@Controller
public class LoginController {
	
	@Autowired
	protected UserService userService;

/*	@RequestMapping(value = { "/login" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
public String login() {
	return "login";
}*/
@RequestMapping(value = { "/goLoginning" }, method = {RequestMethod.POST})
public Object goLoginning(Map<String, Object> map, Model model,@RequestParam("username") String username,
		@RequestParam("password") String password) throws Exception {
	    String msg = "";  
	    UsernamePasswordToken token = new UsernamePasswordToken(username,MD5.encode(password));  
	    token.setRememberMe(true);  
	    Subject subject = SecurityUtils.getSubject();  
	    try {  
	        subject.login(token);  
	        if (subject.isAuthenticated()) {
	        	return new ModelAndView("redirect:/toLogin.html");
	        } else {  
	        	return "/relogin";  
	        }  
	    } catch (IncorrectCredentialsException e) {  
	        msg = "登录密码错误. Password for account " + token.getPrincipal() + " was incorrect.";  
	        model.addAttribute("message", msg);  
	        System.out.println(msg);  
	    } catch (ExcessiveAttemptsException e) {  
	        msg = "登录失败次数过多";  
	        model.addAttribute("message", msg);  
	        System.out.println(msg);  
	    } catch (LockedAccountException e) {  
	        msg = "帐号已被锁定. The account for username " + token.getPrincipal() + " was locked.";  
	        model.addAttribute("message", msg);  
	        System.out.println(msg);  
	    } catch (DisabledAccountException e) {  
	        msg = "帐号已被禁用. The account for username " + token.getPrincipal() + " was disabled.";  
	        model.addAttribute("message", msg);  
	        System.out.println(msg);  
	    } catch (ExpiredCredentialsException e) {  
	        msg = "帐号已过期. the account for username " + token.getPrincipal() + "  was expired.";  
	        model.addAttribute("message", msg);  
	        System.out.println(msg);  
	    } catch (UnknownAccountException e) {  
	        msg = "帐号不存在. There is no user with username of " + token.getPrincipal();  
	        model.addAttribute("message", msg);  
	        System.out.println(msg);  
	    } catch (UnauthorizedException e) {  
	        msg = "您没有得到相应的授权！" + e.getMessage();  
	        model.addAttribute("message", msg);  
	    }  
	    return "/relogin";  
}
@RequestMapping(value = { "toLogin" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET})
public String toLogin() {
	return "/welcome";
}
  @RequestMapping(value = { "LoginOut" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET})
  public String LoginOut() {
	  Subject currentUser = SecurityUtils.getSubject();  
	  Session session = currentUser.getSession(); 
	  TUser user = (TUser)session.getAttribute("login_user");
	   session.removeAttribute("login_user");
	   UserUtil.UserMap.clear();
	  //退出系统 销毁session 清空所有变量
	   session.stop();//session清空
	   return "/relogin";  
}
}

