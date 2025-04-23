package com.javarush.jira.bugtracking.task;

import com.javarush.jira.bugtracking.Handlers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private Handlers.ActivityHandler activityHandler;

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private TaskService taskService;


    @Test
    void workingTime_successfulCalculation() {

        long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);

        LocalDateTime inProgressTime = LocalDateTime.parse("2024-04-10T11:30:10");
        LocalDateTime readyForReviewTime = LocalDateTime.parse("2024-04-11T12:30:10");

        Activity inProgressActivity = new Activity(1L, taskId, 1L, inProgressTime,
                "", TaskService.IN_PROGRESS_STATUS, "low", null,
                "In Progress", null, 3
        );

        Activity readyForReviewActivity = new Activity(2L, taskId, 1L, readyForReviewTime,
                "", TaskService.READY_FOR_REVIEW_STATUS, "low", null,
                "Ready For Review", null, 3
        );

        // Логи для отладки
        System.out.println("В процессе: " + inProgressActivity.getUpdated());
        System.out.println("Готовая к тестированию: " + readyForReviewActivity.getUpdated());

        given(activityHandler.getRepository()).willReturn(activityRepository);

        given(activityRepository.findAllByTaskIdOrderByUpdatedDesc(anyLong()))
                .willReturn(List.of(inProgressActivity, readyForReviewActivity));

        Long actualResult = taskService.workingTime(task);

        assertEquals(1500L, actualResult);
    }

    @Test
    void testingTime_successfulCalculation() {

        long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);

        LocalDateTime readyForReviewTime = LocalDateTime.parse("2024-04-11T12:30:10");
        LocalDateTime doneTime = LocalDateTime.parse("2024-04-12T13:30:00");

        Activity readyForReviewActivity = new Activity(3L, taskId, 1L, readyForReviewTime,
                "", TaskService.READY_FOR_REVIEW_STATUS, "low", null,
                "Ready For Review", null, 3
        );

        Activity doneActivity = new Activity(4L, taskId, 1L, doneTime,
                "", TaskService.DONE_STATUS, "low", null,
                "Done", null, 3
        );

        // Логи для отладки
        System.out.println("Готовая к тестированию: " + readyForReviewActivity.getUpdated());
        System.out.println("Завершенная: " + doneActivity.getUpdated());

        given(activityHandler.getRepository()).willReturn(activityRepository);

        given(activityRepository.findAllByTaskIdOrderByUpdatedDesc(anyLong()))
                .willReturn(List.of(readyForReviewActivity, doneActivity));

        Long actualResult = taskService.testingTime(task);

        assertEquals(1500L, actualResult);
    }


    @Test
    void checkTimeBetweenStatuses_whenNotEnoughData_throwException() {

        long taskId = 2L;
        Task task = new Task();
        task.setId(taskId);

        LocalDateTime readyForReviewTime = LocalDateTime.parse("2024-04-11T12:30:10");

        Activity singleActivity = new Activity(1L, taskId, 1L, readyForReviewTime,
                "", TaskService.READY_FOR_REVIEW_STATUS, "low", null,
                "Ready For Review", null, 3
        );

        given(activityHandler.getRepository()).willReturn(activityRepository);

        given(activityRepository.findAllByTaskIdOrderByUpdatedDesc(anyLong()))
                .willReturn(Collections.singletonList(singleActivity));

        assertThrows(IllegalStateException.class, () -> taskService.testingTime(task),
                "Исключение должно быть брошено при недостаточности данных");
    }

}
