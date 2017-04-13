package eu.sonata.nfv.nec.resolver.core.exceptions.persistence;

/**
 * Exceptions of the son-imagestore tool. The exceptions
 * are inspired by the javax.persistence.PersistenceException
 * which are not used due to a dependency to GPL.
 *
 * @author Michael Bredel
 */
public class PersistenceException extends RuntimeException {

    public PersistenceException(){}

    public PersistenceException(String message) {
        super(message);
    }

    public  PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }
}
