package com.hope.taskbooster;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/tasks")
    public String index(Model model) {
        model.addAttribute("tasks", taskRepository.findAll());
        return "tasks/index";
    }

    @PostMapping("/tasks")
    public String create(@RequestParam String title, RedirectAttributes redirectAttributes) {
        long taskCount = taskRepository.count();

        if (taskCount >= 3) {
            redirectAttributes.addFlashAttribute("errorMessage", "「今日のタスク」は3つまで! 増やす前に、まずは1つ始めましょう。");
            return "redirect:/tasks";
        }

        Task task = new Task();
        task.setTitle(title);
        task.setCreatedAt(LocalDateTime.now());

        taskRepository.save(task);

        return "redirect:/tasks";
    }
}
