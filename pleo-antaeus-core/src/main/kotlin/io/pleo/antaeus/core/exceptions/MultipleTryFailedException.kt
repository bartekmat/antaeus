package io.pleo.antaeus.core.exceptions

import java.lang.Exception

class MultipleTryFailedException : Exception("Unable to proceed due to network problem. Max number of retries exceeded"){

}
