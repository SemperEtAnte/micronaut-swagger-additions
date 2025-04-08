package ru.semperante.utils;

import io.micronaut.http.HttpStatus;

public record TempExceptionHolder(HttpStatus status, String example, String description) {
   public TempExceptionHolder(HttpStatus status, String example) {
      this(status, example, status.getCode() + " " + status.getReason());
   }
}
