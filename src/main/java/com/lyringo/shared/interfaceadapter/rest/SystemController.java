package com.lyringo.shared.interfaceadapter.rest;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class SystemController {

  @GetMapping
  public Map<String, String> index() {
    return Map.of("service", "lyringo-backend", "status", "ok");
  }
}
