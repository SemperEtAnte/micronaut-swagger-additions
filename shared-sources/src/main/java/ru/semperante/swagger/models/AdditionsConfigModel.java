package ru.semperante.swagger.models;

import java.util.Set;

public final class AdditionsConfigModel {
   private Set<String> handlers;
   private String title;
   private String version;
   private boolean proceeded = false;

   public AdditionsConfigModel() {
   }

   public AdditionsConfigModel(Set<String> handlers, String title, String version) {
      this.handlers = handlers;
      this.title = title;
      this.version = version;
   }

   public Set<String> getHandlers() {
      return handlers;
   }

   public String getTitle() {
      return title;
   }

   public String getVersion() {
      return version;
   }

   public boolean isProceeded() {
      return proceeded;
   }

   public AdditionsConfigModel setProceeded(boolean proceeded) {
      this.proceeded = proceeded;
      return this;
   }

   public AdditionsConfigModel setHandlers(Set<String> handlers) {
      this.handlers = handlers;
      return this;
   }

   public AdditionsConfigModel setTitle(String title) {
      this.title = title;
      return this;
   }

   public AdditionsConfigModel setVersion(String version) {
      this.version = version;
      return this;
   }
}
