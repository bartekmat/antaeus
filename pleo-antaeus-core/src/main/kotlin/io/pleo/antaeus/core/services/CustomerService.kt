/*
    Implements endpoints related to customers.
 */

package io.pleo.antaeus.core.services

import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Customer
import java.util.*

class CustomerService(private val dal: AntaeusDal) {
    fun fetchAll(): List<Customer> {
        return dal.fetchCustomers()
    }

    fun fetch(id: Int): Optional<Customer> {
        val customer = dal.fetchCustomer(id)
        return if (customer == null) Optional.empty() else Optional.of(customer)
    }
}
