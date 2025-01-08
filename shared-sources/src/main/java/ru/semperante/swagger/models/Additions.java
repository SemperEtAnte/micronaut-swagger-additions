package ru.semperante.swagger.models;

import java.util.*;

public final class Additions {
   private String uri;
   private String method;
   private List<String> exceptions = new LinkedList<>();
   private Set<String> tags = new HashSet<>(2);

   public Additions() {
   }

   public Additions(String uri, String method) {
      this.uri = uri;
      this.method = method;
   }

   public String getUri() {
      return uri;
   }

   public Additions setUri(String uri) {
      this.uri = uri;
      return this;
   }

   public String getMethod() {
      return method;
   }

   public Additions setMethod(String method) {
      this.method = method;
      return this;
   }

   public List<String> getExceptions() {
      return exceptions;
   }

   public Additions setExceptions(List<String> exceptions) {
      this.exceptions = exceptions;
      return this;
   }

   public Set<String> getTags() {
      return tags;
   }

   public Additions setTags(Set<String> tags) {
      this.tags = tags;
      return this;
   }
}
