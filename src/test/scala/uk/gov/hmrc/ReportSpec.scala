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

import com.typesafe.config.{Config, ConfigFactory}
import play.api.libs.json.{Json, Reads}
import uk.gov.hmrc.utils.{HttpClient, ZapConfiguration}
import uk.gov.hmrc.zap.ZapAlert
import uk.gov.hmrc.zap.ZapReport._

import scala.xml.{Elem, Node, NodeSeq, XML}

class ReportSpec extends BaseSpec {

  trait TestSetup {
    val httpClient: HttpClient = mock[HttpClient]

    implicit val zapAlertReads: Reads[ZapAlert] = Json.reads[ZapAlert]
    val alerts: List[ZapAlert] = Json.parse(alertJson).as[List[ZapAlert]]
    val threshold = "AUniqueThreshold"
    val config: Config = ConfigFactory.parseResources("test.conf").getConfig("zap-automation-config")
    val zapConfiguration = new ZapConfiguration(config)
  }

  "html report" should {
    "should contain the failure threshold so that " in new TestSetup {
      val reportHtmlAsString: String = generateHtmlReport(alerts, threshold, spiderScanCompleted = true, activeScanCompleted = false)

      reportHtmlAsString should include("AUniqueThreshold")
    }

    "should contain the correct alert count by risk in the Summary of Alerts table" in new TestSetup {
      val reportHtmlAsString: String = generateHtmlReport(alerts, "AUniqueThreshold", spiderScanCompleted = true, activeScanCompleted = false)
      val reportXml: Elem = XML.loadString(reportHtmlAsString)

      getByAtt(reportXml, "id", "summary-high-count").text shouldBe "1"
      getByAtt(reportXml, "id", "summary-medium-count").text shouldBe "1"
      getByAtt(reportXml, "id", "summary-low-count").text shouldBe "1"
      getByAtt(reportXml, "id", "summary-info-count").text shouldBe "1"
    }

    "should show the correct scan status in the Summary of Scan table when spiderScan and activeScan is not completed" in new TestSetup {
      val reportHtmlAsString: String = generateHtmlReport(alerts, "AUniqueThreshold", spiderScanCompleted = false, activeScanCompleted = false)
      val reportXml: Elem = XML.loadString(reportHtmlAsString)

      getByAtt(reportXml, "id", "passive-scan").text shouldBe "Run"
      getByAtt(reportXml, "id", "spider-scan").text shouldBe "Not Run"
      getByAtt(reportXml, "id", "active-scan").text shouldBe "Not Run"
    }

    "should show the correct scan status in the Summary of Scan table when spiderScan and ActiveScan is completed" in new TestSetup {
      val reportHtmlAsString: String = generateHtmlReport(alerts, "AUniqueThreshold", spiderScanCompleted = true, activeScanCompleted = true)
      val reportXml: Elem = XML.loadString(reportHtmlAsString)

      getByAtt(reportXml, "id", "passive-scan").text shouldBe "Run"
      getByAtt(reportXml, "id", "spider-scan").text shouldBe "Run"
      getByAtt(reportXml, "id", "active-scan").text shouldBe "Run"
    }

    "should display the details of 4 alerts" in new TestSetup {
      val reportHtmlAsString: String = generateHtmlReport(alerts, "AUniqueThreshold", spiderScanCompleted = true, activeScanCompleted = true)
      val reportXml: Elem = XML.loadString(reportHtmlAsString)

      getByAtt(reportXml, "type", "alert-details").size shouldBe 4
    }
  }

  def getByAtt(e: Elem, att: String, value: String): NodeSeq = {
    def filterAttribute(node: Node, att: String, value: String) = (node \ ("@" + att)).text == value

    e \\ "_" filter { n => filterAttribute(n, att, value) }
  }

  val alertJson: String =
    """
      [{
      "sourceid": "3",
      "other": "",
      "method": "GET",
      "evidence": "no-store",
      "pluginId": "10049",
      "cweid": "524",
      "confidence": "Medium",
      "wascid": "13",
      "description": "A short description",
      "messageId": "1",
      "url": "http://some.url",
      "reference": "https://tools.ietf.org/html/rfc7234",
      "solution": "A suggested solution",
      "alert": "Non-Storable Content",
      "param": "",
      "attack": "",
      "name": "Non-Storable Content",
      "risk": "High",
      "id": "1"
      },
      {
      "sourceid": "3",
      "other": "",
      "method": "GET",
      "evidence": "no-store",
      "pluginId": "10049",
      "cweid": "524",
      "confidence": "Medium",
      "wascid": "13",
      "description": "A short description",
      "messageId": "1",
      "url": "http://some.url",
      "reference": "https://tools.ietf.org/html/rfc7234",
      "solution": "A suggested solution",
      "alert": "Non-Storable Content",
      "param": "",
      "attack": "",
      "name": "Non-Storable Content",
      "risk": "Medium",
      "id": "2"
      },
      {
      "sourceid": "3",
      "other": "",
      "method": "GET",
      "evidence": "no-store",
      "pluginId": "10049",
      "cweid": "524",
      "confidence": "Medium",
      "wascid": "13",
      "description": "A short description",
      "messageId": "1",
      "url": "http://some.url",
      "reference": "https://tools.ietf.org/html/rfc7234",
      "solution": "A suggested solution",
      "alert": "Non-Storable Content",
      "param": "",
      "attack": "",
      "name": "Non-Storable Content",
      "risk": "Low",
      "id": "3"
      },
      {
      "sourceid": "3",
      "other": "",
      "method": "GET",
      "evidence": "no-store",
      "pluginId": "10049",
      "cweid": "524",
      "confidence": "Medium",
      "wascid": "13",
      "description": "A short description",
      "messageId": "1",
      "url": "http://some.url",
      "reference": "https://tools.ietf.org/html/rfc7234",
      "solution": "A suggested solution",
      "alert": "Non-Storable Content",
      "param": "",
      "attack": "",
      "name": "Non-Storable Content",
      "risk": "Informational",
      "id": "4"
    }]
    """
}
