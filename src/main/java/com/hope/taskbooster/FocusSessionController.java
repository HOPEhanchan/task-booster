package com.hope.taskbooster;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
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

    @GetMapping("/sessions/{id}/finish")
    public String finishForm(@PathVariable Long id, Model model) {
        FocusSession focusSession = focusSessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid session id: " + id));

        model.addAttribute("focusSession", focusSession);

        return "sessions/finish";
    }

    @PostMapping("/sessions/{id}/finish")
    public String finish(@PathVariable Long id, @RequestParam Integer focusScore) {
        FocusSession focusSession = focusSessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid session id: " + id));

        LocalDateTime endedAt = LocalDateTime.now();
        long durationMinutes = Duration.between(focusSession.getStartedAt(), endedAt).toMinutes();

        focusSession.setEndedAt(endedAt);
        focusSession.setDurationMinutes(durationMinutes);
        focusSession.setFocusScore(focusScore);
        focusSession.setFeedbackMessage(generateFeedback(durationMinutes, focusScore));

        focusSessionRepository.save(focusSession);

        return "redirect:/sessions/" + focusSession.getId() + "/result";
    }

    @GetMapping("/sessions/{id}/result")
    public String result(@PathVariable Long id, Model model) {
        FocusSession focusSession = focusSessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid session id: " + id));

        model.addAttribute("focusSession", focusSession);

        return "sessions/result";
    }

    private String generateFeedback(long durationMinutes, int focusScore) {
        if (durationMinutes < 30 && focusScore <= 2) {
            return "短時間かつ集中度が低め💦 タスクが大きすぎるか、作業前の分解が甘かった可能性があるかも？";
        }

        if (durationMinutes >= 60 && focusScore >= 4) {
            return "良い集中ができています！このクオリティを次回も目指していきましょう！👍";
        }

        if (durationMinutes >= 90 && focusScore <= 2) {
            return "長時間作業していますが、集中度が低めです、、、😢 休憩やタスクの再設計が必要かも？";
        }

        return "作業ログを記録しました。集中の質をさらに上げれるよう一緒に頑張りましょう！💪";
    }
}
