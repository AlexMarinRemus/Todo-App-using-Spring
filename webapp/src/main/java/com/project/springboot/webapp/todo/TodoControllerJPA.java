package com.project.springboot.webapp.todo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.validation.Valid;

@Controller
@SessionAttributes("name")
public class TodoControllerJPA {


    private TodoRepository todoRepository;

    public TodoControllerJPA(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @RequestMapping("todo-list")
    public String listAlltodos(ModelMap model) {
        List<Todo> todos = todoRepository.findByUsername(getLoggedinUsername());
        model.addAttribute("todos", todos);
        return "todo-list";
    }

    @RequestMapping(value="add-todo", method = RequestMethod.GET)
    public String showNewTodoPage(ModelMap model) {
        String username = getLoggedinUsername();
        Todo todo = new Todo(0, username, "", LocalDate.now(), false);
        model.put("todo", todo);
        return "todo";
    }

    @RequestMapping(value = "add-todo", method = RequestMethod.POST)
    public String addTodo(@Valid Todo todo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "todo";
        }
        todo.setUsername(getLoggedinUsername());
        todoRepository.save(todo);
        return "redirect:todo-list";
    }

    @RequestMapping(value="delete-todo")
    public String deleteTodo(@RequestParam int id){
        todoRepository.deleteById(id);
        return "redirect:todo-list";
    }

    @RequestMapping(value="edit-todo", method = RequestMethod.GET)
    public String showEditTodo(ModelMap modelMap, @RequestParam int id){
        Todo todo = todoRepository.findById(id).get();
        modelMap.addAttribute("todo", todo);
        return "todo";
    }

    @RequestMapping(value="edit-todo", method = RequestMethod.POST)
    public String editTodo(@Valid Todo todo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "todo";
        }
        String username = getLoggedinUsername();
        todo.setUsername(username);
        todoRepository.save(todo);
        return "redirect:todo-list";
    }

    private String getLoggedinUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
