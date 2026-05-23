package br.com.challenge.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Hidden
@RestController
@RequestMapping("/health")
public class HealthController {

  @GetMapping("/check")
  public ResponseEntity<Void> getOk() {
    return ResponseEntity.ok().build();
  }
}
