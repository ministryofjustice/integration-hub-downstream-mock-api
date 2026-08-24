package uk.gov.justice.digital.hmpps.templatepackagename

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BenefitCheckerMockApi

fun main(args: Array<String>) {
  runApplication<BenefitCheckerMockApi>(*args)
}
