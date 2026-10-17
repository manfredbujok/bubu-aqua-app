package tech.taiiku.controlapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.taiiku.controlapi.dto.SeleniumGridStatus;
import tech.taiiku.controlapi.service.SeleniumStatusService;

import java.util.Map;

@RestController
@RequestMapping("/api/selenium")
public class SeleniumController {

    private final SeleniumStatusService seleniumStatusService;

    public SeleniumController(SeleniumStatusService seleniumStatusService) {
        this.seleniumStatusService = seleniumStatusService;
    }

    @GetMapping("/status")
    public SeleniumGridStatus status() throws Exception {
        return seleniumStatusService.getStatus();
    }
}