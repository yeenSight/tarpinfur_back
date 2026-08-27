package fr.yeensight.tarpinfur.domain;

/**
 * Base exception for all domain-level errors.
 * Runtime exception to avoid checked exception boilerplate.
 *
 * SOLID - SRP: Represents domain invariant violations only.
 */
public abstract class DomainException extends RuntimeException {
    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}

