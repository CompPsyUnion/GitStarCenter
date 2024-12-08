package cn.yiming1234.gitstarcenter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex, Model model) {
        ModelAndView modelAndView = new ModelAndView("error");
        model.addAttribute("message", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(SessionExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ModelAndView handleSessionExpiredException(SessionExpiredException ex) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/login");
        return modelAndView;
    }
}