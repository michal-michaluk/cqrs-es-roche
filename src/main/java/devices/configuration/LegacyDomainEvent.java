package devices.configuration;

public interface LegacyDomainEvent {

    static DomainEvent normalise(DomainEvent event) {
        if (event instanceof LegacyDomainEvent) {
            return ((LegacyDomainEvent) event).normalise();
        } else {
            return event;
        }
    }

    DomainEvent normalise();
}
