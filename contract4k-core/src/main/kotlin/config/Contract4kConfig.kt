package config

import report.ValidationReporter
import report.ConsoleValidationReporter

object Contract4kConfig{
    var defaultSortConditionRepoter: ValidationReporter = ConsoleValidationReporter
}