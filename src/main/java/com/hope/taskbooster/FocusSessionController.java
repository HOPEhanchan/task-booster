package com.hope.taskbooster;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.time.LocalDateTime;

@Controller
public class FocusSessionController {

    private final TaskRepository taskRepository;
    private final FocusSessionRepository focusSessionRepository;

    public FocusSessionController(
            TaskRepository taskRepository,
            FocusSessionRepository focusSessionRepository
    ) {
        this.taskRepository = taskRepository;
        this.focusSessionRepository = focusSessionRepository;
    }

    @PostMapping("/sessions/start/{taskId}")
    public String start(@PathVariable Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid task id: " + taskId));

        FocusSession focusSession = new FocusSession();
        focusSession.setTask(task);
        focusSession.setStartedAt(LocalDateTime.now());

        focusSessionRepository.save(focusSession);

        return "redirect:/sessions/" + focusSession.getId() + "/working";
    }

    @GetMapping("/sessions/{id}/working")
    public String working(@PathVariable Long id, Model model) {
        FocusSession focusSession = focusSessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid session id: " + id));

        model.addAttribute("focusSession", focusSession);

        return "sessions/working";
    }
}
