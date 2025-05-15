package report

import exception.ErrorCode

fun interface ValidationReporter {

    fun report(failures: List<ErrorCode>)
}
