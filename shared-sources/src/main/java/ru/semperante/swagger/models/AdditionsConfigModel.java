package ru.semperante.swagger.models;

import java.util.Set;

public record AdditionsConfigModel(Set<String> handlers, String title, String version) {
}
