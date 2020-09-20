package io.pleo.antaeus.core.logger

import mu.KLogging

class Logger {
    companion object {
        val log = KLogging().logger()
    }
}