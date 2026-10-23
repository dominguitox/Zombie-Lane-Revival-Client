package zlport.stubs;

public final class NotPortedException extends UnsupportedOperationException {
   private static final long serialVersionUID = 1L;
   private final String signature;

   public NotPortedException(String signature) {
      super("Não portado: " + signature + " (stub gerado, ADR-005; para explorar: -Dzlport.stubs=lenient)");
      this.signature = signature;
   }

   public String signature() {
      return this.signature;
   }
}
