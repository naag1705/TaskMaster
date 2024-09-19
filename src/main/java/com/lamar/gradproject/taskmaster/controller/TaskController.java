package com.lamar.gradproject.taskmaster.controller;

// src/main/java/com/example/todoapp/controller/TaskController.java

import com.lamar.gradproject.taskmaster.model.Task;
import com.lamar.gradproject.taskmaster.model.User;
import com.lamar.gradproject.taskmaster.repository.TaskRepository;
import com.lamar.gradproject.taskmaster.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String viewTasks(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername());
        List<Task> tasks = taskRepository.findByUser(user);
        model.addAttribute("tasks", tasks);
        return "task-list";
    }

    @GetMapping("/add")
    public String showAddTaskForm(Model model) {
        model.addAttribute("task", new Task());
        return "add-task";
    }

    @PostMapping("/add")
    public String addTask(@ModelAttribute("task") Task task, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername());
        task.setUser(user);
        taskRepository.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String showEditTaskForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid task Id:" + id));

        // Ensure the task belongs to the logged-in user
        if (!task.getUser().getEmail().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("You are not authorized to edit this task.");
        }

        model.addAttribute("task", task);
        return "edit-task";
    }

    @PostMapping("/edit/{id}")
    public String editTask(@PathVariable Long id, @ModelAttribute("task") Task updatedTask, @AuthenticationPrincipal UserDetails userDetails) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid task Id:" + id));

        // Ensure the task belongs to the logged-in user
        if (!existingTask.getUser().getEmail().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("You are not authorized to edit this task.");
        }

        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setCompleted(updatedTask.isCompleted());
        taskRepository.save(existingTask);

        return "redirect:/tasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid task Id:" + id));

        // Ensure the task belongs to the logged-in user
        if (!task.getUser().getEmail().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("You are not authorized to delete this task.");
        }

        taskRepository.delete(task);
        return "redirect:/tasks";
    }
}

