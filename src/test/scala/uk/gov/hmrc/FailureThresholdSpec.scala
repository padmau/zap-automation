/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc

import uk.gov.hmrc.zap.ZapAlert

class FailureThresholdSpec extends BaseSpec {

  it("should fail if there are alerts above the threshold specified in the config") {

    updateTestConfigWith("failureThreshold=Medium")

    val alerts = List[ZapAlert](
      ZapAlert(cweid = "16", url = "http://localhost:9999/hello/SB363126A/here", risk = "Low"),
      ZapAlert(cweid = "16", url = "http://localhost:9999/hello/YZ570921/here", risk = "High"),
      ZapAlert(cweid = "16", url = "http://localhost:9999/hello/YZ570921/here", risk = "Informational")
    )
    assert(!testSucceeded(alerts))
  }

  it("should fail if there are alerts matching the threshold specified in the config") {

    updateTestConfigWith("failureThreshold=Medium")

    val alerts = List[ZapAlert](
      ZapAlert(cweid = "16", url = "http://localhost:9999/hello/SB363126A/here", risk = "Low"),
      ZapAlert(cweid = "16", url = "http://localhost:9999/hello/YZ570921/here", risk = "Medium"),
      ZapAlert(cweid = "16", url = "http://localhost:9999/hello/YZ570921/here", risk = "Informational")
    )

    assert(!testSucceeded(alerts))
  }

  it("should not fail if there are alerts below the threshold specified in the config") {

    updateTestConfigWith("failureThreshold=Medium")

    val alerts = List[ZapAlert](
      ZapAlert(cweid = "16", url = "http://localhost:9999/hello/SB363126A/here", risk = "Low"),
      ZapAlert(cweid = "16", url = "http://localhost:9999/hello/YZ570921/here", risk = "Informational")
    )

    assert(testSucceeded(alerts))
  }

  it("should not fail for an Informational alert") {

    updateTestConfigWith("failureThreshold=Medium")

    val alerts = List[ZapAlert](
      ZapAlert(cweid = "16", url = "http://localhost:9999/hello/YZ570921/here", risk = "Informational")
    )
    assert(testSucceeded(alerts))
  }
}
