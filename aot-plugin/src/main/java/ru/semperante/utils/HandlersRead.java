package ru.semperante.utils;

import java.util.Map;

public record HandlersRead(String mainFileName, Map<String, TempExceptionHolder> examples) {
}
