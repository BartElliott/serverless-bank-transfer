package exceptions

/* TODO: Make an abstract Exception class that returns 4xy error codes with custom messages
 * (throwing one of these returns a generic 500 right now)
 */
class UserNotFoundException extends Throwable {}

class InsufficientBalanceException extends Throwable {}

class DatabaseFailureException extends Throwable {}

class NegativeTransferException extends Throwable {}
